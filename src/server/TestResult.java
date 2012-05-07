package CS247;

class TestResult extends Result {

	TestResult(Result copy){
		super(copy);
	}
	
	@Override
	void process(){
		System.out.println("Test job results.");
	}
}
