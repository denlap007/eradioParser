package noThreads;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup; 
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static noThreads.ProcessCla.*;

public class ParseLevel0 {
	static ArrayList<Integer> codes = new ArrayList<Integer>();
	private ArrayList<String> aElements = new ArrayList<String>();

    public void getCodes() throws IOException{
    	for(String aUrl : theUrls){

    		// parse the input html of URL into a DOM document
    		Document doc = Jsoup.connect(aUrl).get();	
    		
    		// Select all the <a> elements with [href] attribute that start with 
    		// "javascript"
    		Elements links = doc.select("a[href^=javascript]");
        	for (Element aLink : links)
        		aElements.add(aLink.outerHtml());
    	}
    	
    	//Extract the radio codes from <a> elements
		int start=0;
		int end=0;
		int code=0;
		String number;
    	for(String aUrl : aElements){
			start = aUrl.lastIndexOf("(");
			end = aUrl.lastIndexOf(")");
			if ((start!=-1 && end !=-1) && (end-start>1)){
				number = aUrl.substring(start+1, end);
				code = Integer.parseInt(number);
				codes.add(code);
			}
    	}
    	debug(codes);
    	System.out.print("\nNumber of Codes: " + codes.size()+"\n");
    }
    
    public void debug(ArrayList<Integer> aList){
    	for(int a : aList)
    		System.out.println(a); 	
    }
    
    public void debug2(ArrayList<String> aList){
    	for(String a : aList)
    		System.out.println(a);	
    }


	/**
	 * @return the aElements
	 */
	public ArrayList<String> getaElements() {
		return aElements;
	}

	/**
	 * @param theurl the theUrl to set
	 */
	public void addaElements(String theUrl) {
		this.aElements.add(theUrl);
	}

}
