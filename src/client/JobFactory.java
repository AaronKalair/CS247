package CS247;

import java.io.*;

// class to create subclasses of job for the client to execute.
public class JobFactory {
	JobFactory(){
		
	}
	
	Job makeJob(InputStream in) throws IOException {
		Job ret = null, j = Job.deserialize(in);
		
		switch(j.type){
			default:
			case Job.TEST: 
				ret = new TestJob(j);
			break;
		}
		return ret;
	}

}
