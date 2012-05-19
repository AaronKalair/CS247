package CS247;

import java.util.*;
import java.io.*;
import java.nio.*;

class Result {
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
	private Result(){
		params = new ArrayList<String>();
	}
	
	// copy constructor for ResultsFactory / subclasses to use.
	Result(Result copy){
		this.type = copy.type;
		this.params = new ArrayList<String>(copy.params);
	}
	
	// method to be overridden by subclasses.
	void process(){
		
	};
	
	// Constructor used by the client before serializing.
	Result(byte type){
		this.type = type;
		params = new ArrayList<String>();
	}
	
	void addParam(String param){
		params.add(param);
	}
	
	/*
	When serialized Results take the following format:
		1 byte  - result type
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
	
	static Result deserialize(DataInputStream in) throws IOException {
		Result r = new Result();
		// get the type and number of params.
		r.type = in.readByte();
		short num_params = in.readShort();
		if(num_params < 0) throw new IOException("Invalid packet.");
		// get all the params.
		for(int i = 0; i < num_params; ++i){
			try {
				r.addParam(in.readUTF());
			} catch(Exception e){
				throw new IOException("Can't deserialize!", e);
			}
		}
		return r;
	}

}
