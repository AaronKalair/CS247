package CS247;

import java.io.*;
import java.nio.*;
import java.net.*;

public class ClientWorkerThread extends Thread {

	private final Socket connection;
	private DataInputStream input;
	private DataOutputStream output;
	private final Scheduler scheduler;
	private final ResultsThread results_thread;
	private boolean results_pending;
	private Job current_job = null;

	ClientWorkerThread(Socket connection, Server server) {
		super("ClientWorkerThread");
		this.connection = connection;
		this.scheduler = server.scheduler;
		this.results_thread = server.results_thread;
		results_pending = false;
		try {
			input = new DataInputStream(connection.getInputStream());
			output = new DataOutputStream(new BufferedOutputStream(connection.getOutputStream(), 16384));
			output.writeByte((byte)1);
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
				} catch(Exception e){
					// tell the scheduler to resend this job.
					scheduler.addJob(current_job);
					System.out.println("[Client removed] " + e.getMessage());
					// return here to end this thread, since the connection is broken.
					return;
				}
				results_pending = false;
			} else {
				// get the next job from the scheduler, this will block until one is available.
				current_job = scheduler.getNextJob();
				// serialize it and send it to our client.
				try {
					current_job.serialize(output);
				} catch(Exception e){
					// tell the scheduler to resend this job.
					scheduler.addJob(current_job);
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
