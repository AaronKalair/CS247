package CS247;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

public class Server {

	ServerSocket socket;
	AndroidServer androidServer;
	Scheduler scheduler;
	ResultsThread results_thread;
	Database database;
	IPWhitelist ip_whitelist;
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
		results_thread = new ResultsThread(this);
		results_thread.start();
		database = new Database();
		androidServer = new AndroidServer(45587, database);
		androidServer.start();
		ip_whitelist = new IPWhitelist();
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
		if(ip_whitelist.checkIP(ip)){
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
	
	private void dispose() {
		
		androidServer.dispose();
		//dispose scheduler
		//dispose results thread
		//dispose database
		try
		{socket.close();}
		catch (IOException e)
		{logger.log(Level.WARNING, "Could not close server socket.");}
		
	}
	
	public static void main(String[] args){
		Server server = new Server();
		
		while(server.run());
		
		server.dispose();
	}

}
