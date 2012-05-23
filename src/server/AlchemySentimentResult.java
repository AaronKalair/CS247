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
			System.out.println("Alchemy sentiment: " + c.sentiment);
			c.reasoning += "Sentiment: " + c.sentiment + " (via AlchemyAPI).\n";
			
			if(c.category != null && c.category.equals("Disaster") && c.entity != null && c.sentiment < 0){
				Job j = new Job(Job.WOLFRAM_ALPHA, url);
				System.out.println("adding wolfram alpha job");
				j.addParam(new String(c.entity + "+export+commodities").replace(" ", "+"));
				scheduler.addJob(j);
			}
			
			if(c.category != null && c.category.equals("Stocks") && c.entity != null){				
				if(c.sentiment < 0){
					c.suggestion = "Consider selling shares of " + c.entity + ".";
					results_thread.addConclusionToDatabase(c);
				} else if(c.sentiment > 0.15) {
					c.suggestion = "Consider investing in shares of " + c.entity + ".";
					results_thread.addConclusionToDatabase(c);
				}
			}
		}
	}
}
