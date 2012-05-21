package CS247;

import java.io.*;
import java.util.*;
import java.net.URLDecoder;

class DataSourceManager {

    BufferedReader input = null;
    File sources_file = null;
    long last_modified = 0;
    JobURLComparator comp;
    
    ArrayList<PeriodicJob> source_jobs;
    
	DataSourceManager(){
		source_jobs = new ArrayList<PeriodicJob>();
		comp = new JobURLComparator();
		// get the path of server.jar in order to get whitelist.txt in the same directory.
		String path = Server.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		try {
			path = URLDecoder.decode(path, "UTF-8");
			path = (new File(path)).getParentFile().getPath() + File.separator + "sources.txt";
		} catch (Exception e){
			path = "sources.txt";
		}
		
		try {
			sources_file = new File(path);
			if(!sources_file.exists()){
				sources_file.createNewFile();
				PrintWriter output = new PrintWriter(new FileWriter(sources_file));
				output.println("# Add sources here in one of two formats:");
				output.println("# RSS <interval> <url> for an RSS feed.");
				output.println("# or");
				output.println("# TWT <interval> <twitter name> for a twitter feed.");
				output.println("RSS 600 http://feeds.bbci.co.uk/news/rss.xml");
				output.close();
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	void getSources(ArrayList<PeriodicJob> jobs){
		long mod = sources_file.lastModified();
		if(mod > last_modified){
			update(mod);
			Collections.sort(jobs, comp);
			
			for(PeriodicJob p : source_jobs){
				int i = Collections.binarySearch(jobs, p, comp);
				PeriodicJob q;
			
				if(i >= 0 && (q = jobs.get(i)).type == p.type){
					p.countdown = q.countdown;
				}
			}
			jobs.clear();
			jobs.addAll(source_jobs);
		}
	}
	
	private void update(long newtime){
		try {
			input = new BufferedReader(new FileReader(sources_file));
			source_jobs.clear();
			String line;
		    while ((line = input.readLine()) != null) {
		    	if(line.length() > 0 && line.charAt(0) == '#') continue;
		        String[] parts = line.split(" ");
		        if(parts.length != 3) continue;
		        byte type = Job.INVALID;
		        if(parts[0].equals("RSS")) type = Job.RSS;
		        if(parts[0].equals("TST")) type = Job.TEST;
		        if(parts[0].equals("TWT")) type = Job.TWITTER;
		        if(type == Job.INVALID) continue;
		        int interval = 0;
		        try {
		        	interval = Integer.parseInt(parts[1]) * 1000;
		        } catch(Exception e){
		        	continue;
		        }
		        PeriodicJob p = new PeriodicJob(interval, type, parts[2]);
		        source_jobs.add(p);
		    }
        } catch(Exception e){
        	e.printStackTrace();
        }
        last_modified = newtime;
	}
}

class JobURLComparator implements Comparator<Job> {
	public int compare(Job one, Job two){
		return one.params.get(0).compareTo(two.params.get(0));
	}
}
