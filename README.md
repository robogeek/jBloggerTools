jBloggerTools
=============

Some little tools to deal with Blogger and Feeds

This is set up as a Netbeans project, and all the dependent .jar's are checked into the lib directory.  I know this is bad practice, sorry.  A problematic dependency is the old version of the ROME project.  Because dev.java.net was killed, the ROME project that corresponds to these .jar's cannot be retrieved.  I found a successor version of the ROME project on, IIRC, Google Code, but that project looks to be stalled.

The ROME project provides a big pile of useful packages for processing both RSS and Atom feeds.  While both are XML and Java has XML built in, the ROME project's code disentangled the various variants of RSS and Atom, providing a common API to deal with both.  This greatly simplifies the jBloggerTools code.  But because the ROME project doesn't exist for all intents and purposes, these .jar's are more-or-less black boxes.  I do have source code for the ROME project and maybe it would be a good idea to check it into github.

Once built Netbeans puts the build into the dist directory.  It's set up so you run it like so:--

    java -jar dist/jBloggerTools.jar command arguments

The .jar file is set up to use blogger.Main as the main class.  That class looks for a command name as the first argument, and subsequent arguments are interpreted by the code for the class.

Retrieving an RSS or Atom Feed
==============================

The feed2text command makes it real easy to dump the contents of a feed in a format that's usable by the rest of the tools.  This is due to a text format for describing blog posts, which is documented below.  

This is real simple

    java -jar dist/jBloggerTools.jar feed2text URL
    java -jar dist/jBloggerTools.jar feed2text URL hours

The feed content is printed on the standard output.  The 'hours' argument tosses away any item older than the number of hours specified.  

Posting to a Blogger Blog
=========================

The fromtext command takes a text file formatted as documented below, takes arguments on the command line to identify the blog to post to, and posts the items to the blog.

    java -jar dist/jBloggerTools.jar fromtext authorName userName userPasswd blogId inputFile postSummaryFile notPostedFile

The authorName can be any text string, such as your name.  

The userName is the google account (e.g. example@gmail.com) the blog is associated with.  

The userPassword is the password for that user.  

The blogId is the code string in the middle of the URL when you are logged in to the blogger dashboard.  

The inputFile is a text file (documented below) for the content to be posted.

The postSummaryFile has an HTML summary of what was posted written to it.

The notPostedFile gets a list of the items which were not posted.  If all goes well this file will be empty.  However if an error occurs, the error is trapped, the unposted entries go into this file, and then the error is rethrown.

Counting the number of items in a blog text file
================================================

    java -jar dist/jBloggerTools.jar counttext inputFile

Generating an HTML summary of items in a blog text file
=======================================================

    java -jar dist/jBloggerTools.jar summary inputFile summaryFile

Text format to represent blog posts
===================================

I don't know if there is any standard archival format for blog post entries.  This is a simple format that I came up with, and it works for my needs.  

The basic idea is to use blocks of "tag: value" fields to represent individual data items of a blog post, and to separate those blocks by blank lines.  Blank lines delineate the blog posts in the text file.

The important tags follow.  There are several more available, but these are the most useful.

    title: Title String

Gives the title of the blog post.  The title tag is required.

    date: #### ###

Let's you specify a date for the post.  The date must be two integers, the first is the number of miliseconds since 1970 (normal Unix date code) and the second is a timezone offset from GMT.  If a date is not specified, the current date is used.

    description: A paragraph of description text.  Can contain HTML code.

Whatever you put here is simply dumped into the output, surrounded by paragraph tags. This is optional, and can appear zero or more times.  Multiple description: tags turn into multiple paragraphs of text.

    tag: Tag Name

This attaches what Blogger calls a Label to the blog post.  You can of course list multiple tag: lines, and each one is converted into a Label. This is optional, and can appear zero or more times. 

    url: URL

Attaches a link to the end of the blog post. This is optional, and can appear zero or more times. You can only specify the URL, not the anchor text, nor any of the rel= options.

    youtubeUrl: URL

Constructs the embed code to play the video referenced in the URL.  This is optional, and currently only one youtubeUrl: line can be used.  The URL must be a youtube URL, pointing at the page for a given video.  The code for the video is extracted from the URL and used to construct the player.

    image: URL

Constructs an img tag to include the image. This is optional, and can appear zero or more times.




