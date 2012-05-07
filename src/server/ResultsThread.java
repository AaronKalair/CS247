package CS247;

import java.util.*;
import java.util.concurrent.*;

public class ResultsThread extends Thread {
	
	private Server server;
	private ArrayBlockingQueue<Result> results;

	ResultsThread(Server server){
		this.server = server;
		results = new ArrayBlockingQueue<Result>(20);
	}
	
	public void addResult(Result r){
		while(true){
			try {
				results.put(r);
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
				Result r = makeResult(results.take());
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
	
	private Result makeResult(Result in){
		switch(in.type){
			case Result.TEST: 
				return new TestResult(in);
			case Result.RSS:
				return new RSSResult(in, server);
			case Result.INVALID:
			default:
				return null;
		}
	}
}
