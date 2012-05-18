package CS247;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.*;

import java.util.*;
import java.io.*;

class XMLJob extends Job {

	protected static final SAXParserFactory factory = SAXParserFactory.newInstance();
	protected static SAXParser parser = null;
	Result ret = null;
	
	static {
		factory.setNamespaceAware(true);
		factory.setValidating(true);
		try {
			parser = factory.newSAXParser();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	XMLJob(Job copy){
		super(copy);
		parser.reset();
	}
}
