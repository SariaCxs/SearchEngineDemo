// HTMLParser Library $Name: v1_6 $ - A java-based parser for HTML
// http://sourceforge.org/projects/htmlparser
// Copyright (C) 2012 Pengfei Zhao
//
//
package application.spider;

import java.net.URL;
import java.util.Vector;
import org.htmlparser.beans.LinkBean;
import org.htmlparser.util.ParserException;

/**
 * LinkExtractor extracts all the links from the given webpage
 * and prints them on standard output.
 */


public class LinkExtractor
{
	
	public Vector<String> extractLinks(String link) throws ParserException

	{
		// extract links in url and return them
		// ADD YOUR CODES HERE
	    Vector<String> v_link = new Vector<String>();
	    LinkBean lb = new LinkBean();
	    lb.setURL(link);
		URL[] URL_array = new URL[0];
		try {
			URL_array = lb.getLinks();
			System.out.print(URL_array);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		for(int i=0; i<URL_array.length; i++){
	    	v_link.add(URL_array[i].toString());
	    }
		return v_link;
	}
	
    public static void main (String[] args) throws ParserException
    {
        String url = "http://www.cs.ust.hk/";
        LinkExtractor extractor = new LinkExtractor();
        extractor.extractLinks(url);
        
    }
}
