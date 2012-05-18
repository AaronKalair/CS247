package CS247;

class AlchemyEntityResult extends Result {
	
	private final ResultsThread results_thread;
	private final Scheduler scheduler;
	//private final Database database;

	AlchemyEntityResult(Result copy, ResultsThread rt){
		super(copy);
		this.results_thread = rt;
		this.scheduler = rt.server.scheduler;
	}
	
	@Override
	void process(){
		String url = params.get(0);
		String entity = params.get(1);
		
		Conclusion c = results_thread.getConclusionByURL(url);
		
		System.out.println("Alchemy entity result: " + params.get(1));
		
		if(c != null && !entity.equals("none")){
			c.entity = entity;
			// if this url is relevant to countries, get the sentiment.
			if(c.category != null && c.category.equals("Country") || c.category.equals("Stocks")){
				System.out.println("adding alchemy sentiment job");
				Job j = new Job(Job.ALCHEMY_SENTIMENT, url);
				
				scheduler.addJob(j);
			}
		}
	}
}
