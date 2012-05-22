package cs247.app;

import java.net.*;
import java.io.*;

import android.util.Log;

public class CS247ServerConnection {

	String host;
	Socket connection = null;
	DataOutputStream out = null;
	DataInputStream in = null;
	
	private static final int PORT = 45587;

	CS247ServerConnection(String host){
		this.host = host;
		try {
			connection = new Socket(host, PORT);
			out = new DataOutputStream(connection.getOutputStream());
			in = new DataInputStream(connection.getInputStream());
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	String[] pollForAlerts(String timestamp){
		String[] results = null;
		Log.d("debug", timestamp);
		if(connection != null && connection.isConnected()){
			try {
				out.writeByte((byte)3);
				out.writeUTF(timestamp);
			
				int num_results = in.readInt();
				Log.d("debug", "" + num_results);
				results = new String[num_results * 6];
			
				for(int i = 0; i < num_results * 6; ++i){
					results[i] = in.readUTF();
				}
			} catch(Exception e){
				connection = null;
				e.printStackTrace();
			}
		}
	
		return results;
	}
	
	String[] getAlertFromServer(int alert_id){
		String[] results = null;
		if(connection != null && connection.isConnected()){
			try {
				out.writeByte((byte)2);
				out.writeInt(alert_id);
				
				int num_results = in.readInt();
				results = new String[num_results];
			
				for(int i = 0; i < num_results; ++i){
					results[i] = in.readUTF();
				}
			} catch(Exception e){
				connection = null;
				e.printStackTrace();
			}
		}
		return results;
	}
	
	boolean registerC2DM(String email){
		boolean success = false;
		if(connection != null && connection.isConnected()){
			try {
				out.writeByte((byte)0);
				out.writeUTF(email);
			
				if(in.readByte() == 1) success = true;
			} catch(Exception e){
				connection = null;
				e.printStackTrace();
			}
		}
		return success;
	}
	
	boolean unregisterC2DM(String email){
		boolean success = false;
		if(connection != null && connection.isConnected()){
			try {
				out.writeByte((byte)1);
				out.writeUTF(email);
			
				if(in.readByte() == 1) success = true;
			} catch(Exception e){
				connection = null;
				e.printStackTrace();
			}
		}
		return success;
	}
	
	boolean isConnected(){
		return (connection != null && connection.isConnected());
	}
	
	void connect(){
		try {
			if(connection != null){
				connection.connect(new InetSocketAddress(host, PORT));
				out = new DataOutputStream(connection.getOutputStream());
				in = new DataInputStream(connection.getInputStream());
			} else {
				connection = new Socket(host, PORT);
				out = new DataOutputStream(connection.getOutputStream());
				in = new DataInputStream(connection.getInputStream());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void disconnect() {
		try {
			if(isConnected()){
				out.writeByte((byte)4);
			}
			connection.close();
		} catch (IOException e) {
			connection = null;
		}
	}

}
