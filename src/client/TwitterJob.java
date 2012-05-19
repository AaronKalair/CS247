package CS247;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.*;

import java.util.*;
import java.io.*;
import java.net.URLEncoder;

/* 
	A job to parse Twitter statuses.
	It requies one param from the server: the user's name.
	And it returns a result with all the text and links.
*/
class TwitterJob extends XMLJob {

	String url;
	TwitterHandler handler;
	
	TwitterJob(Job j){
		super(j);
		
		try {
			url = "http://api.twitter.com/1/statuses/user_timeline.xml?include_entities=true&screen_name=" + params.get(0) + "&count=20";
		} catch(Throwable e){
			e.printStackTrace();
			ret = new Result(Result.INVALID);
		}
		// create the TwitterHandler, as shown below.
		handler = new TwitterHandler(type == Job.TEST ? true : false);
	}
	
	Result execute(){
		// if ret is not null then there has been an error, return the invalid result.
		if(ret != null) return ret;
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
		TwitterJob r = new TwitterJob(new Job(Job.TEST, "bbcnews"));
		r.execute();
	}
}

class TwitterHandler extends DefaultHandler {
	// result that is filled after parsing.
	Result result;
	// the current xml element name.
	private String current_element;
	private TwitterInfo item;
	private ArrayList<TwitterInfo> twitter_items;
	private int item_number;
	private boolean done;
	// bool to print debug info, will be true if the static test method is used.
	private boolean debug;
	
	TwitterHandler(boolean debug){
		this.debug = debug;
		item_number = -1;
		twitter_items = new ArrayList<TwitterInfo>();
		current_element = "none";
		done = false;
	}
	// this method is called for every xml start tag.
	public void startElement(String namespaceURI, String localName, 
						String qName, Attributes atts) throws SAXException {
		if(localName.equals("status")){
			item_number++;
			item = new TwitterInfo();
		}
		if(item_number < 0) return;
		current_element = localName;
	}
	
	// this method is called with the characters between an xml start / end tag.
	public void characters(char[] ch, int start, int length) throws SAXException {
		// add text and urls.
		if(current_element.equals("text")){
			item.text += new String(ch, start, length);
		}
		if(current_element.equals("expanded_url")){
			item.link += new String(ch, start, length);
		}
	}
	// this method is called for every xml end tag.
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		// if we get a statuses tag, the document is over.
		if(localName.equals("status")){
			twitter_items.add(item);
		}
		if(localName.equals("statuses") && !done){
			done = true;
			result = new Result(Result.TWITTER);
			for(TwitterInfo i : twitter_items){
				// skip tweets with no link.
				if(i.text.equals("") || i.link.equals("")) continue;
				if(debug) System.out.printf("T: %s\nL: %s\n", i.text, i.link);
				result.addParam(i.text);
				try {
					i.link = URLEncoder.encode(i.link, "UTF-8");
				} catch(Exception e){
					i.link = "unavailable";
				}
				result.addParam(i.link);
			}
		}
		current_element = "none";
	}
}
// class to hold the strings we are interested in.
class TwitterInfo {
	String text;
	String link;
	TwitterInfo(){
		text = link =  "";
	}
}
