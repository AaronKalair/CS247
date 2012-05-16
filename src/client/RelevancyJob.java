package CS247;

/* 
This job is used to determine whether a piece of source info is worth looking
into further by matching it against keywords.

Currently it just does natural disasters, add more categories!
*/
public class RelevancyJob extends Job {

	// strings to match natural disasters, two sets to reduce false positives.
	private static String[] disasters1 = { "tsunami", "flood", "earthquake", "volcano", "explosion" };
	private static String[] disasters2 = { "devastate", "kill", "destroy", "richter" };
	
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
		
		if(matchDisasters()){
			// if it matches a natural disaster, we want to find out which country next.
			res.addParam("country");
		} else {
			// otherwise it is not relevant.
			res.addParam("none");
		}
		return res;
	}
	
	// match against the natural disaster strings, it must have atleast one word from each list.
	private boolean matchDisasters(){
		boolean stage2 = false;
		for(String s : disasters1){
			if(desc.toLowerCase().contains(s)) stage2 = true;
		}
		if(!stage2) return false;
		for(String s : disasters2){
			if(desc.toLowerCase().contains(s)) return true;
		}
		return false;
	}
}
