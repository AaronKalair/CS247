package CS247;

import java.util.*;
import java.util.concurrent.*;

public class ResultsThread extends Thread {
	
	final Server server;
	private ArrayBlockingQueue<Result> results_in;
	HashMap<String, Conclusion> conclusions;

	ResultsThread(Server server){
		this.server = server;
		results_in = new ArrayBlockingQueue<Result>(20);
		conclusions = new HashMap<String, Conclusion>();
	}
	
	public void addResult(Result r){
		while(true){
			try {
				results_in.put(r);
				break;
			} catch(InterruptedException e){
				e.printStackTrace();
				continue;
			}
		}
	}
	
	public void run(){
		while(true){
			try {
				Result r = makeResult(results_in.take());
				if(r == null){
					System.out.println("Recieved Invalid result.");
					continue;
				}
				r.process();
			} catch(InterruptedException e){
				e.printStackTrace();
				continue;
			}
		}
	}
	
	public void storeConclusion(Conclusion c){
		conclusions.put(c.url, c);
	}
	
	public Conclusion getConclusionByUrl(String url){
		return conclusions.get(url);
	}
	
	private Result makeResult(Result in){
		switch(in.type){
			case Result.TEST: 
				return new TestResult(in);
			case Result.RSS:
				return new RSSResult(in, this);
			case Result.INVALID:
			default:
				return null;
		}
	}
}
