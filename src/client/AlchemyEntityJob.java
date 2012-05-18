package CS247;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.*;

import java.util.*;
import java.io.*;

/* 
	A job for getting Entities from Alchemy.
   it must be sent two parameters from the server:
   0 = the url to tell Alchemy to parse.
   1 = the category that you want to get the highest ranked entity from.
   
   It will return a result which has two params:
   0 = the url.
   1 = The highest ranked entity of the given category or "none" if there were none.
*/
class AlchemyEntityJob extends AlchemyJob {
		
	String url;
	SAXParser parser;
	AlchemyEntityHandler handler;
	Result ret = null;
	
	AlchemyEntityJob(Job copy){
		super(copy);
		
		try {
			url = "http://access.alchemyapi.com/calls/url/URLGetRankedNamedEntities?url=" + params.get(0) + "&apikey=" + api_key;
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
		String category = "";
		try {
			category = params.get(1);
			handler = new AlchemyEntityHandler(params.get(0), category);
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
		AlchemyEntityJob j = new AlchemyEntityJob(new Job(Job.TEST, "http://www.bbc.co.uk/news/business-18098657"));
		j.addParam("Country");
		Result r = j.execute();
		System.out.println(r.params.get(1));
	}
}

// XML handler.
class AlchemyEntityHandler extends DefaultHandler {

	Result result;
	String url;
	String category;
	String current_element;
	boolean store_text = false;
	int done = 0;
	
	AlchemyEntityHandler(String url, String category){
		this.url = url;
		this.category = category;
	}
	
	// this method is called for every xml start tag.
	public void startElement(String namespaceURI, String localName, 
						String qName, Attributes atts) throws SAXException {
		current_element = localName;
	}
	
	// this method is called with the characters between an xml start / end tag.
	public void characters(char[] ch, int start, int length) throws SAXException {
		// if the type of this element is the same as the category we're looking for, set store_text to true.
		if(current_element.equals("type")){
			String text = new String(ch, start, length);
			if(category.equals(text)){
				store_text = true;
			}
		}
		// store the <text> component.
		if(done == 0 && store_text && current_element.equals("text")){
			String text = new String(ch, start, length);
			result = new Result(Result.ALCHEMY_ENTITY);
			result.addParam(url);
			result.addParam(text);
			done = 1;
		}
		// if there is a <name> element, then store this instead (this is the disambiguated version).
		if(done == 1 && store_text && current_element.equals("name")){
			String text = new String(ch, start, length);
			result.params.add(1, text);
			done = 2;
		}
	}
	// this method is called for every xml end tag.
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		// don't store anything more once an </entity> tag has been found.
		if(localName.equals("entity")){
			store_text = false;
		}
		// it is the end of this element, so set this to none.
		current_element = "none";
		// if we're at the end of the file with no results, set the result param to none.
		if(done == 0 && localName.equals("results")){
			result = new Result(Result.ALCHEMY_ENTITY);
			result.addParam(url);
			result.addParam("none");
			done = 2;
		}
	}
}

