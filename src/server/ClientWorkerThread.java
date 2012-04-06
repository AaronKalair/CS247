package CS247;

import java.io.*;
import java.net.*;

public class ClientWorkerThread extends Thread {

	Socket connection;
	InputStream input;
	OutputStream output;
	boolean results_pending;
	Scheduler scheduler;

	ClientWorkerThread(Socket connection, Server server) {
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
			// if we are waiting on the results, wait for them + then process them FIXME: implement.
			if(results_pending){
				try {
					Thread.sleep(10);
				} catch(InterruptedException e){
					e.printStackTrace();
				}
			} else {
				// get the next job from the scheduler, it might be null which means no jobs are available.
				Job j = scheduler.getNextJob();
				if(j == null){
					try {
						Thread.sleep(10);
					} catch(InterruptedException e){
						e.printStackTrace();
					}
				} else {
					// if there is a job, serialize it and send it to our client.
					byte[] b = j.serialize();
					try {
						output.write(b);
						output.flush();
					} catch(IOException e){
						e.printStackTrace();
					}
					// and now we are waiting on the results.
					results_pending = true;
				}
			}
		}
	}
}
