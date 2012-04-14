package CS247;

import java.io.*;
import java.nio.*;
import java.net.*;

public class ClientWorkerThread extends Thread {

	Socket connection;
	InputStream input;
	OutputStream output;
	boolean results_pending;
	Scheduler scheduler;

	ClientWorkerThread(Socket connection, Server server) {
		super("ClientWorkerThread");
		this.connection = connection;
		this.scheduler = server.scheduler;
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
					System.out.println(r.str);
				} catch(IOException e){
					e.printStackTrace();
					// return here to end this thread, since the connection is broken.
					return;
				}
				results_pending = false;
			} else {
				// get the next job from the scheduler, this will block until one is available.
				Job j = null;
				try {
					j = scheduler.getNextJob();
				} catch (InterruptedException e){
					// if the scheduler was interrupted, skip over serializing.
					e.printStackTrace();
					continue;
				}
				// serialize it and send it to our client.
				byte[] b = j.serialize();
				try {
					output.write(b);
					output.flush();
				} catch(IOException e){
					e.printStackTrace();
					System.out.println("Client removed.");
					// return here to end this thread, since the connection is broken.
					return;
				}
				// and now we are waiting on the results.
				results_pending = true;
			}
		}
	}
}
