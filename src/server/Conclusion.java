package CS247;

// a class to hold onto conclusions made from the results of jobs.
class Conclusion {
	// our suggestion about what to do.
	String suggestion;
	// the link that led to this conclusion.
	String url;
	// category of what this conclusion is relevant to: e.g. country, stock markets, e.t.c
	String category;
	// value of sentiment analysis.
	Float sentiment;
	// result that let to this conclusion.
	Result result;
	
	Conclusion(String url, Result result){
		sentiment = null;
		suggestion = null;
		this.url = url;
		this.result = result;
	}

}
