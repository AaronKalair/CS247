package CS247;

import java.util.*;
import java.io.*;
import java.nio.*;

class Result {
	
	String str;
		
	Result(String str){
		this.str = str;
	}
	/* currently results are just a string, when serialized they contain:
		2 bytes - size of following string
		x bytes - string
	*/
	byte[] serialize(){
		ByteBuffer buff = ByteBuffer.allocate(str.length() + 2);
		buff.putShort((short)str.length());
		try {
			byte[] b = str.getBytes("US-ASCII");
			buff.put(b);
		} catch(UnsupportedEncodingException e){
			throw new RuntimeException("URLs and Parameters must be ASCII! Can't serialize.", e);
		}
		return buff.array();
	}
	// deserialize a result from the inputstream. this blocks until it reads a result.
	static Result deserialize(InputStream in) throws IOException {
		ByteBuffer buffer;
		Result r = null;
		// read the fist two bytes, to get the string length;
		byte[] b = new byte[2];
		in.read(b, 0, 2);
		buffer = ByteBuffer.wrap(b);
		short size = buffer.getShort();
		// now read that length into the buffer.
		b = new byte[size];
		in.read(b, 0, size);
		// get the string.
		try {
			String str = new String(b, "US-ASCII");
			r = new Result(str);
		} catch(UnsupportedEncodingException e){
			throw new RuntimeException("URLs and Parameters must be ASCII! Can't deserialize.", e);
		}
		return r;
	}

}
