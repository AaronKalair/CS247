package CS247;

class RelevancyResult extends Result {
	
	private final ResultsThread results_thread;
	private final Scheduler scheduler;
	//private final Database database;

	RelevancyResult(Result copy, ResultsThread rt){
		super(copy);
		this.results_thread = rt;
		this.scheduler = rt.server.scheduler;
	}
	
	@Override
	void process(){
		String url = params.get(0);
		String category = params.get(1);
		
		Conclusion c = results_thread.getConclusionByURL(url);
		
		if(c != null && !category.equals("none")){
			c.category = category;
			// if this url is relevant to countries, get the sentiment.
			if(category.equals("Disaster")){
				c.reasoning += "Relevant to Disaster (keyword match).\n";
				System.out.println("adding alchemy entity job.");
				Job j = new Job(Job.ALCHEMY_ENTITY, url);
				j.addParam("Country");
				
				scheduler.addJob(j);
			}
			// for stocks, get the most relevant company.
			if(category.equals("Stocks")){
				c.reasoning += "Relevant to Stock market (keyword match).\n";
				System.out.println("adding alchemy entity job. " + url);
				Job j = new Job(Job.ALCHEMY_ENTITY, url);
				j.addParam("Company");
				
				scheduler.addJob(j);
			}
			
			if(category.equals("Product")){
				c.reasoning += "Relevant to New Product (keyword match).\n";
				System.out.println("adding alchemy entity job.");
				Job j = new Job(Job.ALCHEMY_ENTITY, url);
				j.addParam("Company");
				
				scheduler.addJob(j);
			}
		}
	}
}
