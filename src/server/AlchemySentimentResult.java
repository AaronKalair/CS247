package CS247;

class AlchemySentimentResult extends Result {
	
	private final ResultsThread results_thread;
	private final Scheduler scheduler;
	//private final Database database;

	AlchemySentimentResult(Result copy, ResultsThread rt){
		super(copy);
		this.results_thread = rt;
		this.scheduler = rt.server.scheduler;
	}
	
	@Override
	void process(){
		String url = params.get(0);
		String sentiment = params.get(1);
		
		Conclusion c = results_thread.getConclusionByURL(url);
		
		if(c != null && !sentiment.equals("0")){
			c.sentiment = new Float(Float.parseFloat(sentiment));
			
			if(c.category != null && c.category.equals("Country") && c.entity != null && c.sentiment < 0){
				Job j = new Job(Job.WOLFRAM_ALPHA, url);
				j.addParam(c.entity + "+export+commodities");
			}
			
			if(c.category != null && c.category.equals("Stocks") && c.entity != null){
				String invest_or_sell = "none";
				
				if(c.sentiment < 0){
					invest_or_sell = "selling";
				} else {
					invest_or_sell = "investing in";
				}
				c.suggestion = "Consider " + invest_or_sell + " shares of " + c.entity + ".";
				results_thread.addConclusionToDatabase(c);
			}
		}
	}
}