package CS247;

import java.util.*;
import java.util.concurrent.*;
import java.sql.ResultSet;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class ResultsThread extends Thread {
	
	final Server server;
	private final Database database;
	private ArrayBlockingQueue<Result> results_in;
	HashMap<String, Conclusion> conclusions;

	ResultsThread(Server server){
		super("ResultsThread");
		this.server = server;
		this.database = server.database;
		results_in = new ArrayBlockingQueue<Result>(20);
		conclusions = new HashMap<String, Conclusion>();
		// add all conclusions from the databse so we don't duplicate them.
		try {
			ResultSet rs = database.getAllAlertsSinceXAsResultSetObject("2000-01-01 00:00:00");
			while(rs.next()){
				String s = URLEncoder.encode(rs.getString("link"), "UTF-8");
				Conclusion c = new Conclusion(s, null);
				c.suggestion = rs.getString("suggestions");
				storeConclusion(c);
			}
			rs.close();
		} catch(Exception e){
			e.printStackTrace();
		}
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
		
		if(!database.isAlertPresent(fixedurl) && c.result != null){
			String title = c.result.type == Result.TWITTER ? "Tweet" : c.result.params.get(0);
			String desc = c.result.type == Result.RSS ? c.result.params.get(2) : c.result.params.get(0);
			database.insertAlert(title, fixedurl, desc, c.suggestion, 1);
		}
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
