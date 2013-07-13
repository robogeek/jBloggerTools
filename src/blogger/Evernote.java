
package blogger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.StringBufferInputStream;
import java.security.MessageDigest;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

import java.io.PrintStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.evernote.auth.EvernoteAuth;
import com.evernote.auth.EvernoteService;
import com.evernote.clients.ClientFactory;
import com.evernote.clients.NoteStoreClient;
import com.evernote.clients.UserStoreClient;
import com.evernote.edam.error.EDAMErrorCode;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteList;
import com.evernote.edam.type.Data;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.NoteSortOrder;
import com.evernote.edam.type.Notebook;
import com.evernote.edam.type.Resource;
import com.evernote.edam.type.ResourceAttributes;
import com.evernote.edam.type.Tag;
import com.evernote.thrift.transport.TTransportException;

public class Evernote {
    
    private UserStoreClient userStore;
    private NoteStoreClient noteStore;
    private String newNoteGuid;
    
    /**
     * Intialize UserStore and NoteStore clients. During this step, we
     * authenticate with the Evernote web service. All of this code is boilerplate
     * - you can copy it straight into your application.
     */
    public Evernote(String token) throws Exception {
        // Set up the UserStore client and check that we can speak to the server
        EvernoteAuth evernoteAuth = new EvernoteAuth(EvernoteService.SANDBOX, token);
        ClientFactory factory = new ClientFactory(evernoteAuth);
        userStore = factory.createUserStoreClient();
      
        boolean versionOk = userStore.checkVersion("Evernote EDAMDemo (Java)",
            com.evernote.edam.userstore.Constants.EDAM_VERSION_MAJOR,
            com.evernote.edam.userstore.Constants.EDAM_VERSION_MINOR);
        if (!versionOk) {
            System.err.println("Incompatible Evernote client protocol version");
            System.exit(1);
        }
      
        // Set up the NoteStore client
        noteStore = factory.createNoteStoreClient();
    }
    
    /**
     * Print out a list of tag names
     **/
    public static void tags(String[] args) throws Exception {
        String token    = args[1];
        Evernote evnt = new Evernote(token);
        evnt.printTags(token);
    }
    
    /**
     * Print out a list of notebook names
     **/
    public static void notebooks(String[] args) throws Exception {
        String token    = args[1];
        Evernote evnt = new Evernote(token);
        evnt.printNotebooks();
    }
    
    /**
     * Print out a list of notes given a notebook name and tag name
     **/
    public static void listnotes(String[] args) throws Exception {
        String token    = args[1];
        String notebook = args[2];
        String tag      = args[3];
        String outfn    = args.length >= 5 ? args[4] : "-";
        Evernote evnt = new Evernote(token);
        
        evnt.printSelectedNotes(outfn, notebook, tag);
    }
    
    private void printNotebooks() throws Exception {
        List<Notebook> notebooks = noteStore.listNotebooks();
        for (Notebook notebook : notebooks) {
            String nm = notebook.getName();
            System.out.println(nm +" Guid: "+ notebook.getGuid());
        }
    }
    
    private void printTags(String token) throws Exception {
        List<Tag> tags = noteStore.listTags();
        for (Tag tag : tags) {
            System.out.println(tag.getName() +" Guid: "+ tag.getGuid());
        }
    }
    
    private void printSelectedNotes(String outfn, String nmNotebook, String nmTag) throws Exception {
        PrintStream out = System.out;
        if (outfn != null && ! outfn.equals("-")) {
            out = new PrintStream(outfn);
            out.close();  // ensure it's truncated
            out = new PrintStream(outfn);
        }
        
        List<Notebook> notebooks = noteStore.listNotebooks();
        for (Notebook notebook : notebooks) {
            String nm = notebook.getName();
            if (! nm.equals(nmNotebook)) continue;
            
            List<String> tagGuids = new LinkedList<String>();
            List<Tag> tags = noteStore.listTags();
            for (Tag tag : tags) {
                if (tag.getName().equals(nmTag)) tagGuids.add(tag.getGuid());
            }
            
            NoteFilter filter = new NoteFilter();
            filter.setNotebookGuid(notebook.getGuid());
            if (tagGuids.size() > 0) filter.setTagGuids(tagGuids);
            filter.setOrder(NoteSortOrder.CREATED.getValue());
            filter.setAscending(true);
        
            NoteList noteList = noteStore.findNotes(filter, 0, 100);
            List<Note> notes = noteList.getNotes();
            for (Note note : notes) {
                Note fullNote = noteStore.getNote(note.getGuid(), true, true, true, true);
                printNote(out, fullNote);
            }
        }
        if (outfn != null && ! outfn.equals("-")) {
            out.close();
        }
    }
    
    private String timeStampString(long ts) {
        Date dt = new Date(ts);
        return dt.toString();
    }
    
    private void printNote(PrintStream out, Note note) throws Exception {
        out.println("");
        // out.println(note.toString());
        out.println("title: " + Utils.cleanTitle(Utils.cleanup(note.getTitle())));
        out.println("evernoteNoteGuid: " + note.getGuid().toString());
        out.println("createdTime: " + note.getCreated() +" 0000 "+ timeStampString(note.getCreated()));
        out.println("updatedTime: " + note.getUpdated() +" 0000 "+ timeStampString(note.getUpdated()));
        out.println("date: " + note.getUpdated() +" 0000 "+ timeStampString(note.getUpdated()));
        
        List<String> guids = note.getTagGuids();
        if (guids != null) {
            for (String tagGuid : guids) {
                out.println("tagGuid: " + tagGuid);
                Tag tag = noteStore.getTag(tagGuid);
                out.println("tag: " + tag.getName());
                // out.println("tagDetails: " + tag.toString());
            }
        }
        // *** This is handled by the previous loop
        // List<String> tnms = note.getTagNames();
        // if (tnms != null) {
        //     for (String tn : tnms) {
        //         out.println("tagName: " + tn);
        //     }
        // }
        // **** This seems to be solely images that would be inline somehow
        // List<Resource> rsrces = note.getResources();
        // if (rsrces != null) {
        //     for (Resource rsrc : rsrces) {
        //         out.println("resource: " + rsrc.toString());
        //     }
        // }
        // out.println("attributes: " + note.getAttributes().toString());
        String srcUrl = note.getAttributes().getSourceURL();
        if (srcUrl != null) {
            out.println("url: " + srcUrl);
        }
        String content = note.getContent();
        
        out.println("description: " +
            Utils.cleanup(Utils.removeNewLines(
                content
                .replaceAll("<\\?xml[^>]*>", "")
                .replaceAll("<!DOCTYPE[^>]*>", "")
                .replaceAll("<en-note[^>]*>", "")
                .replaceAll("</en-note>", "")
        )));
        
        /*
         * This actually takes quite a while to execute ..
         * So, using the replaceAll stuff above instead
         * 
        if (content != null) {
            // First we parse the content (because it's XML)
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new InputSource(new StringReader(content)));
            // Document doc = dBuilder.parse(new StringBufferInputStream(content));
            
            // Next we find the en-note tag
            NodeList nl = doc.getChildNodes(); // .getElementsByTagName("en-note");
            for (int i = 0; i < nl.getLength(); i++) {
                Node n = nl.item(i);
                if (n.getNodeType() != Node.ELEMENT_NODE) continue;
                Element e = (Element)n;
                if (! e.getTagName().equals("en-note")) continue;
                
                // Then with just the en-note tag in hand
                // we convert it into a string
                TransformerFactory transFactory = TransformerFactory.newInstance();
                Transformer transformer = transFactory.newTransformer();
                StringWriter buffer = new StringWriter();
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                transformer.setOutputProperty(OutputKeys.INDENT, "no");
                transformer.transform(new DOMSource(e), new StreamResult(buffer));
                
                // removing some tags
                String str = buffer.toString()
                            .replaceAll("<en-note[^>]*>", "")
                            .replaceAll("</en-note>", "");
                
                // cleaning it up and printing it
                out.println("description: " + Utils.cleanup(Utils.removeNewLines(str)));
            }
        }*/
        out.println("");
    }
    
    /**
     * Retrieve and display a list of the user's notes.
     */
    /* private void listNotes() throws Exception {
        // List the notes in the user's account
        System.out.println("Listing notes:");
      
        // First, get a list of all notebooks
        List<Notebook> notebooks = noteStore.listNotebooks();
      
        for (Notebook notebook : notebooks) {
            System.out.println("Notebook: " + notebook.getName());
            System.out.println(notebook.toString());
        
            // Next, search for the first 100 notes in this notebook, ordering
            // by creation date
            NoteFilter filter = new NoteFilter();
            filter.setNotebookGuid(notebook.getGuid());
            filter.setOrder(NoteSortOrder.CREATED.getValue());
            filter.setAscending(true);
        
            NoteList noteList = noteStore.findNotes(filter, 0, 100);
            List<Note> notes = noteList.getNotes();
            for (Note note : notes) {
                Note fullNote = noteStore.getNote(note.getGuid(), true, true, true, true);
                // System.out.println(" * " + note.getTitle());
                // System.out.println(note.toString());
                printNote(System.out, fullNote);
            }
        }
        System.out.println();
    } */

}