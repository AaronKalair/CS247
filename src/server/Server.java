package CS247;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

public class Server {

	ServerSocket socket;
	Scheduler scheduler;
	ArrayList<String> whitelist;
	Logger logger;

	Server(){
		// create the listening socket, FIXME: choose port number.
		try {
			socket = new ServerSocket(12345);
		} catch(IOException e){
			throw new RuntimeException("Can't bind to port", e);
		}
		logger = Logger.getLogger("global");
		scheduler = new Scheduler(this);
		scheduler.start();
		//database = new Database(); FIXME: implement database.
		// add localhost to the whitelist for testing purposes.
		whitelist = new ArrayList<String>();
		whitelist.add("127.0.0.1");
	}
	
	boolean run() {
		// accept a connection, this will block until someone connects.
		Socket connection;
		try {
			connection = socket.accept();
		} catch(IOException e){
			e.printStackTrace();
			// return here so that we can try accepting again.
			return true;
		}
		// update the ip whitelist from the db.
		// whitelist = database.getWhitelist();
		// FIXME: maybe make this work with hostnames instead of ip addresses?
		String ip = connection.getInetAddress().getHostAddress();
		// create a new worker if this ip is whitelisted, print a warning otherwise.
		if(whitelist.contains(ip)){
			logger.log(Level.INFO, "new client from " + ip);
			ClientWorkerThread cwt = new ClientWorkerThread(connection, this);
			cwt.start();
		} else {
			logger.log(Level.WARNING, "Non-whitelisted ip " + ip + " attempted to connect.");
			try {
				connection.close();
			} catch(IOException e){
				e.printStackTrace();
			}
		}
		return true; //FIXME: add a server stop condition.
	}
	
	public static void main(String[] args){
		Server server = new Server();
		
		while(server.run());
	}

}
