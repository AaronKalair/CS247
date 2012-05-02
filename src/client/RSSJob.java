package CS247;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.*;

import java.util.*;
import java.io.*;

class RSSJob extends Job {

	String url;
	SAXParser parser;
	RSSHandler handler;
	
	RSSJob(Job j){
		super(j);
		
		url = params.get(0);
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			factory.setValidating(true);
			parser = factory.newSAXParser();
		} catch(Throwable e){
			e.printStackTrace();
		}
		handler = new RSSHandler();
	}
	
	Result execute(){
		try {
			parser.parse(url, handler);
		} catch(Throwable e){
			e.printStackTrace();
		}
		return handler.result;
	}
	
	public static void main(String[] args){
		RSSJob r = new RSSJob(new Job((byte)0, "http://feeds.bbci.co.uk/news/rss.xml"));
		r.execute();
	}
}

class RSSHandler extends DefaultHandler {
	Result result;
	
	private String current_element;
	private RSSInfo item;
	private ArrayList<RSSInfo> rss_items;
	private int item_number;
	private boolean done;
	
	RSSHandler(){
		item_number = -1;
		rss_items = new ArrayList<RSSInfo>();
		current_element = "none";
		done = false;
	}

	public void startElement(String namespaceURI, String localName, 
						String qName, Attributes atts) throws SAXException {
		if(localName.equals("item")){
			item_number++;
			item = new RSSInfo();
		}
		if(item_number < 0) return;
		current_element = localName;
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException {
		if(current_element.equals("title")){
			item.title += (new String(ch, start, length)).replaceAll("(\\r|\\n)", "");
			//System.out.println("T: " + item.title);
		}
		if(current_element.equals("link")){
			item.link += (new String(ch, start, length)).replaceAll("(\\r|\\n)", "");
			//System.out.println("L: " + item.link);
		}
		if(current_element.equals("description")){
			item.desc += (new String(ch, start, length)).replaceAll("(\\r|\\n)", "");
			//System.out.println("D: " + item.description);
		}
	}
	
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		if(localName.equals("item")){
			if(item.title.equals("")) item.title = "untitled";
			if(item.link.equals("")) item.link = "unavailable";
			if(item.desc.equals("")) item.desc = "unavailable";
			
			rss_items.add(item);
		}
		if(localName.equals("channel") && !done){
			done = true;
			result = new Result(Result.RSS);
			for(RSSInfo i : rss_items){
				result.addParam(i.title);
				result.addParam(i.link);
				result.addParam(i.desc);
			}
		}
		current_element = "none";
	}
}

class RSSInfo {
	String title;
	String link;
	String desc;
	RSSInfo(){
		title = link = desc = "";
	}
}
