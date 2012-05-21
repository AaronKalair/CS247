package CS247;

import java.util.*;
import java.io.*;
import java.nio.*;

public class Job {
	byte type;
	ArrayList<String> params;
	
	public static final byte TEST = -2;
	public static final byte INVALID = -1;
	public static final byte RSS = 0;
	public static final byte WOLFRAM_ALPHA = 1;
	public static final byte RELEVANCY = 2;
	public static final byte ALCHEMY_ENTITY = 3;
	public static final byte ALCHEMY_SENTIMENT = 4;
	public static final byte TWITTER = 5;
		
	// this empty constructor is private since only the static deserialize method should need it.
	private Job(){
		params = new ArrayList<String>();
	}
	
	// copy constructor for JobFactory / subclasses to use.
	Job(Job copy){
		this.type = copy.type;
		this.params = new ArrayList<String>(copy.params);
	}
	
	// method to be overridden by subclasses like RSSJob, e.t.c.
	Result execute(){
		return null;
	};
	
	// Constructor used by the server before serializing.
	Job(byte type, String url){
		this.type = type;
		params = new ArrayList<String>();
		params.add(url);
	}
	
	void addParam(String param){
		params.add(param);
	}
	
	/*
	When serialized Jobs take the following format:
		1 byte  - job type
		2 bytes - number of params (n)
		n * ? bytes - params.
	*/	
	void serialize(DataOutputStream out) throws IOException {
		// add the first 3 bytes as shown above.
		out.writeByte(type);
		out.writeShort((short)params.size());
		// add all the params, prefixed with their sizes.
		for(String s : params){
			try {
				out.writeUTF(s);
			} catch(Exception e){
				throw new IOException("Can't serialize!", e);
			}
		}
		out.flush();
	}
	
	static Job deserialize(DataInputStream in) throws IOException {
		Job j = new Job();
		// get the type and number of params.
		j.type = in.readByte();
		short num_params = in.readShort();
		if(num_params < 0) throw new IOException("Invalid packet.");
		// get all the params.
		for(int i = 0; i < num_params; ++i){
			try {
				j.addParam(in.readUTF());
			} catch(Exception e){
				throw new IOException("Invalid packet", e);
			}
		}
		return j;
	}

}
