package CS247;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AndroidClient extends Thread {
	private final Socket socket;
	private final AndroidServer owner;
	private final OutputStream out;
	private final InputStream in;
	private final Logger logger;
	private final String addr;
	private final Database database;
	public boolean run;
	
	public AndroidClient(Socket socket, Database database, AndroidServer owner) {
		super("AndroidClient: "+socket.getInetAddress().getHostAddress());
		this.database = database;
		addr = socket.getInetAddress().getHostAddress();
		this.socket = socket;
		try
		{
			out = socket.getOutputStream();
			in = socket.getInputStream();
		}
		catch (IOException e)
		{throw new RuntimeException("Android client ("+addr+") could not get socket stream.");}
		logger = Logger.getLogger("global");
		this.owner = owner;
		run = true;
	}
	
	public void run() {
		try
		{
			switch(in.read())
			{
			case 0://register
				if(database.insertRegistrationID(readRegistrationID()))
				{out.write(1);}
				else
				{out.write(0);}
				break;
				
			case 1://deregister
				if(database.removeRegistrationID(readRegistrationID()))
				{out.write(1);}
				else
				{out.write(0);}
				break;
				
			case 2://push
				int alert_id = in.read();
				String[] results = database.getAlertByIDAsArray(alert_id);
				if(results == null)
				{out.write(0);}
				else
				{
					out.write(1);
					for(int i=0;i<results.length;i++)
					{
						out.write(results[i].getBytes(), 0, results[i].length());
						out.write(0);
					}
				}
				break;
			}
		}
		catch (IOException e)
		{logger.log(Level.INFO, "Android client ("+addr+") dropped due to IOException.");}
		finally
		{owner.dispose(this);}
	}
	
	private String readRegistrationID() throws IOException
	{
		StringBuilder id = new StringBuilder();
		int c = 0;

		while((c = in.read()) > 0)
		{id.append((char)c);}
		
		return id.toString();
	}

}
