package CS247;

import java.io.*;
import java.nio.*;
import java.net.*;

public class ClientWorkerThread extends Thread {

	private final Socket connection;
	private InputStream input;
	private OutputStream output;
	private final Scheduler scheduler;
	private final ResultsThread results_thread;
	private boolean results_pending;

	ClientWorkerThread(Socket connection, Server server) {
		super("ClientWorkerThread");
		this.connection = connection;
		this.scheduler = server.scheduler;
		this.results_thread = server.results_thread;
		results_pending = false;
		try {
			input = connection.getInputStream();
			output = connection.getOutputStream();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public void run() {
		// keep running until the client exits;
		while(connection.isConnected()){
			// if we are waiting on the results, deserialize them and (currently) print their string.
			if(results_pending){
				try {
					Result r = Result.deserialize(input);
					results_thread.addResult(r);
				} catch(IOException e){
					System.out.println("[Client removed] " + e.getMessage());
					// return here to end this thread, since the connection is broken.
					return;
				}
				results_pending = false;
			} else {
				// get the next job from the scheduler, this will block until one is available.
				Job j = scheduler.getNextJob();
				// serialize it and send it to our client.
				byte[] b = j.serialize();
				try {
					output.write(b);
				} catch(IOException e){
					System.out.println("[Client removed] " + e.getMessage());
					// return here to end this thread, since the connection is broken.
					return;
				}
				// and now we are waiting on the results.
				results_pending = true;
			}
		}
	}
}
