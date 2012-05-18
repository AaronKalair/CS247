package CS247;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class AndroidClient extends Thread {
	private final Socket socket;
	private final AndroidServer owner;
	private final OutputStream out;
	private final InputStream in;
	public boolean run;
	
	public AndroidClient(Socket socket, AndroidServer owner) {
		super("AndroidClient: "+socket.getInetAddress().getHostAddress());
		this.socket = socket;
		try
		{
			out = socket.getOutputStream();
			in = socket.getInputStream();
		}
		catch (IOException e)
		{throw new RuntimeException("Android client ("+socket.getInetAddress().getHostAddress()+") could not get socket stream.");}
		this.owner = owner;
		run = true;
	}
	
	public void run() {
		while(run)
		{run = false;}
		
		owner.dispose(this);
	}

}
