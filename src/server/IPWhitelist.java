package CS247;

import java.io.*;
import java.util.*;
import java.net.URLDecoder;

class IPWhitelist {

    BufferedReader input = null;
    File whitelist_file = null;
    long last_modified = 0;
    
    ArrayList<String> whitelist;
    
	IPWhitelist(){
		whitelist = new ArrayList<String>();
		// get the path of server.jar in order to get whitelist.txt in the same directory.
		String path = Server.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		try {
			path = URLDecoder.decode(path, "UTF-8");
			path = (new File(path)).getParentFile().getPath() + File.separator + "whitelist.txt";
		} catch (Exception e){
			path = "whitelist.txt";
		}
		
		try {
			whitelist_file = new File(path);
			if(!whitelist_file.exists()){
				whitelist_file.createNewFile();
				PrintWriter output = new PrintWriter(new FileWriter(whitelist_file));
				output.println("127.0.0.1");
				output.flush();
				output.close();
			}
		    update(whitelist_file.lastModified());
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	boolean checkIP(String ip){
		long mod = whitelist_file.lastModified();
		if(mod > last_modified) update(mod);
		
		return whitelist.contains(ip);
	}
	
	private void update(long newtime){
		try {
			input = new BufferedReader(new FileReader(whitelist_file));
			whitelist.clear();
			String line;
		    while ((line = input.readLine()) != null) {
		        whitelist.add(line);
		    }
        } catch(Exception e){
        	e.printStackTrace();
        }
        last_modified = newtime;
	}
}
