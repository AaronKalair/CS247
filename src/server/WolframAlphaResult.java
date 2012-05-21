package CS247;

class WolframAlphaResult extends Result {
	
	private final ResultsThread results_thread;
	private final Scheduler scheduler;
	//private final Database database;

	WolframAlphaResult(Result copy, ResultsThread rt){
		super(copy);
		this.results_thread = rt;
		this.scheduler = rt.server.scheduler;
	}
	
	@Override
	void process(){
		String url = params.get(0);
		String res = params.get(1);
		
		System.out.println("WA result: " + res);
		
		Conclusion c = results_thread.getConclusionByURL(url);
		
		if(!res.equals("none") && c != null){
			// if we're getting country exports, we might be able to infer a price increase.
			if(c.category != null && c.category.equals("Country") && c.sentiment != null){
				String[] exports;
				if(res.contains("|")){
					exports = res.split("\\|");
				} else {
					exports = new String[1];
					exports[0] = res;
				}
				c.reasoning += "Country exports: " + exports[0].trim() + " (via WolframAlpha).\n";
				if(c.sentiment < 0){
					c.suggestion = "Prices of " + exports[0].trim() + " may increase.";
					results_thread.addConclusionToDatabase(c);
				} else {
				
				
				}
			}
		}
	}
}
