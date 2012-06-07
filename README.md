About
=====

This system was built for the CS247 group project module at the University of Warwick, UK.

It searches the internet for news that might be relevant to stock market.

Then if an article is found it then determines what actions the user should take based on this news item to maximise gains or minimise losses. 

Finally it reports this information to a user via an android application.

Here is a high level overview of how the system works
![Overview](https://github.com/AaronKalair/CS247/raw/master/overview.jpg)

This is what the android application looks like
![Android](https://github.com/AaronKalair/CS247/raw/master/android.jpg)

Misc
====
You will need to define some values in order for this application to be fully functional.

In CS247App.java enter your google account id to enable C2DM support

In AlchemyJob.java enter your alchemy api key (http://www.alchemyapi.com/api/register.html )

In WolframAlpaJob.java enter your wolfram alpha app id (https://developer.wolframalpha.com/portal/apisignup.html)



Compiling the system
====================

To compile / build the system, run "ant".

To compile the app, you'll need to run 
"android update project -p /path/to/src/app"
followed by "ant app".

Or you can use the eclipse ADT plugin which is simpler.


Running the system
==================

The system is comprised of two components: server.jar and client.jar
To start the system, firstly run server.jar with the following command:
	java -jar server.jar

Then start a client with
	java -jar client.jar

It is possible, and encouraged, to run multiple clients. Just run the previous
command several times.

The clients need not be started on the same machine as the server, but you will
need to add the IP addresses of any clients to whitelist.txt on the machine
running the server. The server does NOT need to be restarted after a change to
whitelist.txt


Adding data sources
===================

Data sources can be added to the system in the sources.txt file.
Currently both RSS feeds and Twitter feeds are supported.

For an RSS feed, use the following syntax:
RSS <interval> <url>

For a Twitter feed, use:
TWT <interval> <twitter username>

where interval is the time period, in seconds, between re-parsing.

The server does NOT need to be restarted after changing sources.txt, it will
update as soon as the file is saved.


Running the android app
=======================

The android app can be run via the official emulator that Google provides,
I recommend using the eclipse ADT plugin to do this as it's more straightforward

The app attempts to connect to a server running on the same machine as the
emulator and will display whether or not is has successfully connected.

If you wish to change the address of the server the android application connects to you can edit it in the CS247App.java file.

