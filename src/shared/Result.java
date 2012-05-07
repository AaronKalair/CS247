package CS247;

import java.util.*;
import java.io.*;
import java.nio.*;

class Result {
	byte type;
	ArrayList<String> params;
	
	public static final byte INVALID = -1;
	public static final byte RSS = 0;
	public static final byte TWITTER = 1;
	public static final byte ALCHEMY_RELATION = 2;
	public static final byte ALCHEMY_SENTIMENT = 3;
	public static final byte TEST = 4;
	
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
	When serialized Jobs take the following format:
		2 bytes - total size
		1 byte  - result type
		1 byte  - number of params (n)
			n * 2 bytes - param size (x)
			n * x bytes - param
	*/	
	byte[] serialize(){
		short packet_size = 4;
		for(String s : params){
			packet_size += s.length() + 2;
		}
		// create a ByteBuffer to make this nicer.
		ByteBuffer buffer = ByteBuffer.allocate(packet_size);		
		// add the first 4 bytes as shown above.
		buffer.putShort((short)(packet_size - 2));
		buffer.put(type);
		buffer.put((byte)params.size());
		// add all the params, prefixed with their sizes.
		for(String s : params){
			try {
				byte[] b = s.getBytes("US-ASCII");
				buffer.putShort((short)b.length);
				buffer.put(b);
			} catch(UnsupportedEncodingException e){
				throw new RuntimeException("URLs and Parameters must be ASCII! Can't serialize.", e);
			}
		}
		
		return buffer.array();
	}
	
	static Result deserialize(InputStream in) throws IOException {
		// get the data back, assuming it's in the format given by serialize()
		// this could maybe do with some better error checking.
		ByteBuffer buffer;
		Result r = new Result();
		byte[] b = new byte[2];
		in.read(b, 0, 2);
		buffer = ByteBuffer.wrap(b);
		short size = buffer.getShort();
		if(size <= 0) throw new IOException("Invalid result packet");
		b = new byte[size];
		in.read(b, 0, size);
		buffer = ByteBuffer.wrap(b);
		
		r.type = buffer.get();
		byte num_params = buffer.get();
		
		for(int i = 0; i < num_params; ++i){
			short p_len = buffer.getShort();
			byte[] param = new byte[p_len];
			buffer.get(param, 0, p_len);
			try {
				String p_str = new String(param, "US-ASCII");
				r.addParam(p_str);
			} catch(UnsupportedEncodingException e){
				throw new IOException("Invalid result packet.", e);
			}
		}
		return r;
	}

}
