package CS247;

class TwitterResult extends Result {
	
	private final ResultsThread results_thread;
	private final Scheduler scheduler;
	//private final Database database;

	TwitterResult(Result copy, ResultsThread rt){
		super(copy);
		this.results_thread = rt;
		this.scheduler = rt.server.scheduler;
	}
	
	@Override
	void process(){
		System.out.println("Twitter Results received with " + params.size() + " params.");
		for(int i = 0; i < params.size(); i += 2) {
			String text = params.get(i);
			String link = params.get(i+1);
			
			if(results_thread.getConclusionByURL(link) != null){
				continue;
			} else {
				Result r = new Result(Result.TWITTER);
				r.addParam(text);
				r.addParam(link);
				
				Conclusion c = new Conclusion(link, r);
				results_thread.storeConclusion(c);
				System.out.println("adding relevancy job");
				Job j = new Job(Job.RELEVANCY, link);
				j.addParam(text);
				scheduler.addJob(j);
			}
		}
	}
}
