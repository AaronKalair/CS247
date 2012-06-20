package CS247;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.*;
/*
	A job for queries to WolframAlpha, currently only tested with "export commodities" type queries.
	input params:
		0 = the url of the story this job is from.
		1 = the query to send to WA e.g. "china+export+commodities"
	output params:
		0 = the url.
		1 = the result from WA.
*/
class WolframAlphaJob extends XMLJob {

	// the app id required for the api.
	private final String appid = "your app id";
	// boolean to stop request actually being sent while testing so we don't waste our 2000 requests / month.
	private final boolean simulate = false;

	String url;
	WolframAlphaHandler handler;

	WolframAlphaJob(Job copy){
		super(copy);
		// if simulating, just return something.
		if(simulate){
			ret = new Result(Result.WOLFRAM_ALPHA);
			ret.addParam(params.get(0));
			ret.addParam("electronics");
		} else {
			// otherwise set up the XML parser.
			try {
				// the query should be the first parameter
				String query = params.get(1);
				url = "http://api.wolframalpha.com/v2/query?input=" + query + "&format=plaintext&appid=" + appid;
			} catch(Throwable e){
				e.printStackTrace();
				ret = new Result(Result.INVALID);
			}
			// create the WolframAlphaHandler, as shown below.
			handler = new WolframAlphaHandler(params.get(0));
		}
	}

	Result execute(){
		// if ret is not null then there has been an error, return the invalid result.
		if(ret != null){
			System.out.println("returning wa result...");
			return ret;
		}
		try {
			// start parsing the XML returned.
			parser.parse(url, handler);
			// get the result after parsing.
			ret = handler.result;
		} catch(Throwable e){
			// in case of an error, return an invalid result.
			ret = new Result(Result.INVALID);
			e.printStackTrace();
		}
		return ret;
	}

	// static method for testing.
	public static void main(String[] args){
		WolframAlphaJob j = new WolframAlphaJob(new Job(Job.TEST, "china+export+commodities"));
		Result r = j.execute();
		System.out.println(r.params.get(1));
	}
}

// XML handler.
class WolframAlphaHandler extends DefaultHandler {
	// bools to indicate the current state.
	boolean store_text, at_result_pod, done;
	// the actual text result that WA gives.
	String res_text;
	// a result object which contains the text, if any.
	Result result;
	String url;

	WolframAlphaHandler(String url){
		this.url = url;
		store_text = at_result_pod = done = false;
		res_text = null;
		result = null;
	}

	// this method is called for every xml start tag.
	public void startElement(String namespaceURI, String localName, 
						String qName, Attributes atts) throws SAXException {
		// if there was an error with the query, return INVALID;
		if(localName.equals("queryresult")){
			String s = atts.getValue("success");
			String e = atts.getValue("error");
			if(s == null || e == null || s.equals("false") || e.equals("true")){
				result = new Result(Result.INVALID);
				done = true;
			}
		}
		// if this is the "results pod", set the at_result_pod boolean.
		if(localName.equals("pod")){
			String s = atts.getValue("title");
			if(s != null && s.equals("Result")){
				at_result_pod = true;
			}
		}
		// if we are at the result text, set store_text to true.
		if(at_result_pod && localName.equals("plaintext")){
			store_text = true;
		}
	}

	// this method is called with the characters between an xml start / end tag.
	public void characters(char[] ch, int start, int length) throws SAXException {
		// store the text, if the boolean is set.
		if(store_text){
			res_text = new String(ch, start, length);
		}
	}
	// this method is called for every xml end tag.
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		if(at_result_pod && localName.equals("pod")){
			at_result_pod = false;
		}
		if(localName.equals("plaintext")){
			store_text = false;
		}
		// create the result.
		if(!done && localName.equals("queryresult")){
			result = new Result(Result.WOLFRAM_ALPHA);
			result.addParam(url);
			if(res_text == null){
				res_text = "none";
			}
			result.addParam(res_text);
			done = true;
		}
	}
}
