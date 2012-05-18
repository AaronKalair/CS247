package CS247;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.*;

import java.util.*;
import java.io.*;

/* 
	A job for getting sentiment from Alchemy.
   it must be sent one parameter from the server:
   0 = the url to tell Alchemy to parse.
   
   It will return a result which has two params:
   0 = the url.
   1 = A String representation of a float between -1 and 1.
*/
class AlchemySentimentJob extends AlchemyJob {
		
	String url;
	SAXParser parser;
	AlchemySentimentHandler handler;
	Result ret = null;
	
	AlchemySentimentJob(Job copy){
		super(copy);
		
		try {
			url = "http://access.alchemyapi.com/calls/url/URLGetTextSentiment?url=" + params.get(0) + "&apikey=" + api_key;
			// setup XML parsing stuff.
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			factory.setValidating(true);
			parser = factory.newSAXParser();
		} catch(Throwable e){
			e.printStackTrace();
			ret = new Result(Result.INVALID);
		}
	}
	
	Result execute(){
		// if ret is not null then there has been an error, return the invalid result.
		if(ret != null) return ret;
		try {
			handler = new AlchemySentimentHandler(params.get(0));
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
		AlchemySentimentJob j = new AlchemySentimentJob(new Job(Job.TEST, "http://www.bbc.co.uk/news/business-18098657"));
		Result r = j.execute();
		System.out.println(r.params.get(1));
	}
}

// XML handler.
class AlchemySentimentHandler extends DefaultHandler {

	Result result;
	String url;
	String current_element;
	boolean done;
	
	AlchemySentimentHandler(String url){
		this.url = url;
		done = false;
		current_element = "none";
		result = null;
	}
	
	// this method is called for every xml start tag.
	public void startElement(String namespaceURI, String localName, 
						String qName, Attributes atts) throws SAXException {
		current_element = localName;
	}
	
	// this method is called with the characters between an xml start / end tag.
	public void characters(char[] ch, int start, int length) throws SAXException {
		// if the current element is the score, put it in a result.
		if(current_element.equals("score")){
			String text = new String(ch, start, length);
			result = new Result(Result.ALCHEMY_SENTIMENT);
			result.addParam(url);
			result.addParam(text);
			done = true;
		}
	}
	// this method is called for every xml end tag.
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		// it is the end of this element, so set this to none.
		current_element = "none";
		// if we're at the end of the file with no result, set the sentiment to 0.
		if(!done && localName.equals("results")){
			result = new Result(Result.ALCHEMY_SENTIMENT);
			result.addParam(url);
			result.addParam("0");
			done = true;
		}
	}
}

