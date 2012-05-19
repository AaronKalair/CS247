package CS247;

import java.util.*;
import java.util.concurrent.*;
import java.net.URLDecoder;

public class ResultsThread extends Thread {
	
	final Server server;
	//private final Database database;
	private ArrayBlockingQueue<Result> results_in;
	HashMap<String, Conclusion> conclusions;

	ResultsThread(Server server){
		super("ResultsThread");
		this.server = server;
		//this.database = server.database;
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
	
	public Conclusion getConclusionByURL(String url){
		return conclusions.get(url);
	}
	
	public void addConclusionToDatabase(Conclusion c){
		//TODO
		String fixedurl;
		try {
			fixedurl = URLDecoder.decode(c.url, "UTF-8");
		} catch(Exception e){
			fixedurl = "?";
		}
		System.out.println("===================");
		System.out.printf("REACHED CONCLUSION:\n %s\n\tCategory: %s\n\tEntity: %s\n\tSentiment: %s\n\tURL: %s\n",
					c.suggestion, c.category, c.entity, c.sentiment, fixedurl);
		System.out.println("===================");
	}
	
	private Result makeResult(Result in){
		switch(in.type){
			case Result.TEST: 
				return new TestResult(in);
			case Result.RSS:
				//return new TestResult(in);
				return new RSSResult(in, this);
			case Result.WOLFRAM_ALPHA:
				return new WolframAlphaResult(in, this);
			case Result.RELEVANCY:
				return new RelevancyResult(in, this);
			case Result.ALCHEMY_ENTITY:
				return new AlchemyEntityResult(in, this);
			case Result.ALCHEMY_SENTIMENT:
				return new AlchemySentimentResult(in, this);
			case Result.TWITTER:
				return new TwitterResult(in, this);
			case Result.INVALID:
			default:
				return null;
		}
	}
}
