package CS247;

/* 
	This job is used to determine whether a piece of source info is worth looking
	into further by matching it against keywords.

	Currently it just does natural disasters, add more categories!

	It should get two params from the server:
	0 = the url.
	1 = text to match against (description).

	And it will return a result with two params:
	0 = the url.
	1 = a category that this is relevant to, or "none".
*/
public class RelevancyJob extends Job {

	// strings to match natural disasters, two sets to reduce false positives.
	private static final String[] disasters1 = { "tsunami", "flood", "earthquake", "volcano", "explosion", "blast", "bomb" };
	private static final String[] disasters2 = { "devastate", "kill", "destroy", "richter" };
	// strings to match stock market related stuff.
	private static final String[] stocks1 = { "stocks", "shares", "trade", "trading" };
	private static final String[] stocks2 = { "aapl", "msft", "ftse", "nasdaq", "stock market", "share price" };
	
	private static final boolean debug = true;
	
	String url;
	String desc;
	
	RelevancyJob(Job j){
		super(j);
	}
	
	@Override
	Result execute(){
		if(params.size() < 2) return new Result(Result.INVALID);
		
		url = params.get(0);
		desc = params.get(1);

		Result res = new Result(Result.RELEVANCY);
		res.addParam(url);
		
		if(match2(disasters1, disasters2)){
			// if it matches a natural disaster, we want to find out which country next.
			if(debug) System.out.println("Relevant to Disaster.");
			res.addParam("Disaster");
		} else if(match2(stocks1, stocks2)){
			// if it matches stocks, set the result accordingly.
			if(debug) System.out.println("Relevant to Stocks.");
			res.addParam("Stocks");
		} else {
			// otherwise it is not relevant.
			if(debug) System.out.println("not relevant.");
			res.addParam("none");
		}
		return res;
	}
	
	// match against 2 sets of strings, it must have atleast one word from each list.
	private boolean match2(String[] one, String[] two){
		boolean stage2 = false;
		for(String s : one){
			if(desc.toLowerCase().contains(s)) stage2 = true;
		}
		if(!stage2) return false;
		for(String s : two){
			if(desc.toLowerCase().contains(s)) return true;
		}
		return false;
	}
}
