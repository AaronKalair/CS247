package CS247;

import java.io.*;
import java.net.*;

class ClientWorkerThread extends Thread {

	Socket connection;
	InputStream input;
	OutputStream output;

	ClientWorkerThread(Socket connection) {
		this.connection = connection;
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
			int bytes_in = 0;
			try {
				bytes_in = input.available();
			} catch (IOException e){
				e.printStackTrace();
			}
			if(bytes_in > 0){
			//FIXME: Currently this just echoes the text it receives, implement protocol.
				try {
					byte[] b = new byte[bytes_in];
					input.read(b);
			
					String s = new String(b);
					System.out.println(s);
					output.write(b);
					output.flush();
				} catch (IOException e){
					e.printStackTrace();
				}
			} else {
				// wait 10ms before looping.
				try {
					Thread.sleep(10);
				} catch(InterruptedException e){
					e.printStackTrace();
				}
			}
		}
	}

}
