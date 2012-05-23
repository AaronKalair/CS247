package CS247;

import java.io.*;
import java.net.*;
import java.nio.*;

public class Client {

	Socket connection;
	JobFactory job_factory;
	DataInputStream input;
	DataOutputStream output;

	public static void main(String[] args){
		Client client = new Client(args);
		while(client.run());
		System.out.println("Client exiting.");
	}

	public Client(String[] args){
		job_factory = new JobFactory();
		InetAddress server_addr;
		// use the first command line argument as the server to connect to,
		// or localhost if there was no arg.
		try {
			if(args.length > 0){
				server_addr = InetAddress.getByName(args[0]);
			} else {
				server_addr = InetAddress.getLocalHost();
			}
		} catch (UnknownHostException e){
			throw new RuntimeException("Host could not be found!", e);
		}
		try {
			// connect to the server, FIXME: choose port.
			connection = new Socket(server_addr, 12345);
			input = new DataInputStream(connection.getInputStream());
			output = new DataOutputStream(new BufferedOutputStream(connection.getOutputStream(), 16384));
			String local_addr = connection.getLocalAddress().toString();
			byte accepted = input.readByte();
			if(accepted == 0){
				System.out.println("This client's ip (" + local_addr + ") is not on the server's whitelist!");
				connection.close();
				connection = null;
			}
		} catch (IOException e){
			throw new RuntimeException("Can't connect to host!", e);
		}
	}

	public boolean run(){
		// get a job, execute it and return the results. note: makeJob blocks waiting for a job.
		if(connection == null || !connection.isConnected()) return false;
		try {	
			Job j = job_factory.makeJob(input);
			Result r = j.execute();
			r.serialize(output);
		} catch(IOException e){
			e.printStackTrace();
			// should probably try reconnecting here.
			return false;
		}
		return true;
	}

}
