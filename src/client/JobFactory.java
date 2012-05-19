package CS247;

import java.io.*;

// class to create subclasses of job for the client to execute.
public class JobFactory {
	JobFactory(){
		
	}
	
	Job makeJob(DataInputStream in) throws IOException {
		Job ret = null;
		Job j = Job.deserialize(in);
		
		switch(j.type){
			case Job.RSS:
				ret = new RSSJob(j);
			break;
			case Job.WOLFRAM_ALPHA:
				ret = new WolframAlphaJob(j);
			break;
			case Job.RELEVANCY:
				ret = new RelevancyJob(j);
			break;
			case Job.ALCHEMY_ENTITY:
				ret = new AlchemyEntityJob(j);
			break;
			case Job.ALCHEMY_SENTIMENT:
				ret = new AlchemySentimentJob(j);
			break;
			case Job.TWITTER:
				ret = new TwitterJob(j);
			break;
			case Job.TEST: 
			default:
				ret = new TestJob(j);
			break;
		}
		return ret;
	}

}
