package CS247;

import java.util.*;
import java.util.concurrent.*;

public class ResultsThread extends Thread {
	
	private final Scheduler scheduler;
	private ArrayBlockingQueue<Result> results;

	ResultsThread(Server server){
		this.scheduler = server.scheduler;
		results = new ArrayBlockingQueue<Result>(20);
	}
	
	void addResult(Result r){
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
				Result r = results.take();
				System.out.println(r.str);
			} catch(InterruptedException e){
				e.printStackTrace();
				continue;
			}
		}
	}
}
