package CS247;

// A job for testing out the protocol.
class TestJob extends Job {
	TestJob(Job copy){
		super(copy);
	}

	Result execute(){
		Result r = new Result(Result.TEST);
		r.addParam("Testing!");
		System.out.println("Test job executed.");
		return r;
	}
}
