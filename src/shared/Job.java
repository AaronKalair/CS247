package CS247;

import java.util.*;
import java.io.*;
import java.nio.*;

public class Job {
	byte type;
	ArrayList<String> params;
	
	public static final byte RSS = 0;
	public static final byte TWITTER = 1;
	public static final byte ALCHEMY_RELATION = 2;
	public static final byte ALCHEMY_SENTIMENT = 3;
	public static final byte TEST = 4;
	
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
		2 bytes - total size
		1 byte  - job type
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
		buffer.putShort(packet_size);
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
	
	void deserialize(byte[] raw_buffer){
		// get the data back, assuming it's in the format given by serialize()
		// this could maybe do with some better error checking.
		ByteBuffer buffer = ByteBuffer.wrap(raw_buffer);
		type = buffer.get();
		byte num_params = buffer.get();
		
		for(int i = 0; i < num_params; ++i){
			short p_len = buffer.getShort();
			byte[] param = new byte[p_len];
			buffer.get(param, 0, p_len);
			try {
				String p_str = new String(param, "US-ASCII");
				params.add(p_str);
			} catch(UnsupportedEncodingException e){
				throw new RuntimeException("URLs and Parameters must be ASCII! Can't deserialize.", e);
			}
		}
	
	}

}
