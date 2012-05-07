package CS247;

class RSSResult extends Result {
	
	private final Scheduler scheduler;
	//private final Database database;

	RSSResult(Result copy, Server server){
		super(copy);
		this.scheduler = server.scheduler;	
	}
	
	@Override
	void process(){
		for(int i = 1; i <= params.size(); i += 2){
			//TODO: check database for duplicate links.
			//TODO: every second param is a description, send it to be checked for relevancy.
			//Job j = new Job(Job.RELEVANT, params.get(i));
			//scheduler.addJob(j); 
		}
	}
}
