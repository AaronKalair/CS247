package CS247;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class AndroidServer extends Thread {
	private final int port;
	private final ServerSocket listenSocket;
	private final Logger logger;
	private final Database database;
	private List<AndroidClient> clients;
	private boolean removeClients;
	private ReentrantLock clientsLock;

	AndroidServer(int port, Database database){
		super("AndroidServer");
		this.database = database;
		this.port = port;
		try
		{listenSocket = new ServerSocket(port);}
		catch(IOException e)
		{throw new RuntimeException("Could not start Android Server: "+e);}
		logger = Logger.getLogger("global");
		removeClients = true;
		clientsLock = new ReentrantLock(); 
		clients = new LinkedList<AndroidClient>();
	}

	public void run(){
		Socket clientSocket = null;
		do
		{
			try
			{clientSocket = listenSocket.accept();}
			catch (IOException e)
			{logger.log(Level.WARNING, "Failed to accept a client");}
			logger.log(Level.INFO, "Accepted android client ("+clientSocket.getLocalAddress().getHostAddress()+")");

			AndroidClient client = new AndroidClient(clientSocket, database, this);
			clients.add(client);
			client.start();
		}
		while(clientSocket != null);
	}

	public boolean dispose()
	{
		try
		{listenSocket.close();}
		catch (IOException e)
		{
			logger.log(Level.WARNING, "Can't close android server socket.");
			return false;
		}


		clientsLock.lock();
		try
		{
			removeClients = false;
			int tryAgain;
			for(AndroidClient client : clients)
			{
				tryAgain = 5;
				while(tryAgain > 0)
				{
					try
					{client.join();}
					catch (InterruptedException e)
					{
						tryAgain--;
						if(tryAgain == 0)
						{logger.log(Level.WARNING, "Failed to close android client");}
					}
				}

			}
		}
		finally
		{clientsLock.unlock();}

		return true;
	}

	public void dispose(AndroidClient androidClient)
	{
		while(removeClients)
		{
			if(clientsLock.tryLock())
			{
				try
				{clients.remove(androidClient);}
				finally
				{clientsLock.unlock();}
				break;
			}
		}
	}

	private static class allowAll implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

	public void push(int alertId)
	{
		List<String> devices = database.getDevices();
		URL url;
		String postBase ="collapse_key=dbapp"+"&data.id="+alertId;
		String post = null;
		try
		{
			url = new URL("https://android.apis.google.com/c2dm/send");
			HttpsURLConnection con = null;
			OutputStream out = null;
			HttpsURLConnection.setDefaultHostnameVerifier(new allowAll());
			for(String id : devices)
			{
				post = postBase + "&registration_id="+URLEncoder.encode(id, "UTF-8");
				con = (HttpsURLConnection)url.openConnection();
				con.setDoOutput(true);
				con.setUseCaches(false);
				con.setRequestMethod("POST");
				con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
				con.setRequestProperty("Content-Length",Integer.toString(post.length()));
				con.setRequestProperty("Authorization", "GoogleLogin Auth=your google auth string");

				out = con.getOutputStream();
				out.write(post.getBytes());
				out.close();
			}
		}
		catch (MalformedURLException e)
		{e.printStackTrace();}
		catch (IOException e)
		{e.printStackTrace();}
	}

}
