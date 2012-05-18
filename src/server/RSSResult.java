package CS247;

class RSSResult extends Result {
	
	private final ResultsThread results_thread;
	private final Scheduler scheduler;
	//private final Database database;

	RSSResult(Result copy, ResultsThread rt){
		super(copy);
		this.results_thread = rt;
		this.scheduler = rt.server.scheduler;
	}
	
	@Override
	void process(){
		System.out.println("RSS Results received with " +params.size() + " params.");
		for(int i = 0; i < params.size(); i += 3) {
			String title = params.get(i);
			String link = params.get(i+1);
			String desc = params.get(i+2);
			
			if(results_thread.getConclusionByURL(link) != null){
				continue;
			} else if(!link.equals("unavailable")){
				Result r = new Result(Result.RSS);
				r.addParam(title);
				r.addParam(link);
				r.addParam(desc);
				
				Conclusion c = new Conclusion(link, r);
				results_thread.storeConclusion(c);
				System.out.println("adding relevancy job");
				Job j = new Job(Job.RELEVANCY, link);
				j.addParam(title + " " + desc);
				scheduler.addJob(j);
			}
		}
	}
}
