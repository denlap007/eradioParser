/**
	eradioParser: This program extracts the radio station 
	links along with their names, found on http://e-radio.gr, 
	and creates a playlist.
    
	Copyright (C) 2012  Lappas Dionysis
    
    This file is part of eradioParser.

    eradioParser is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    eradioParser is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
    
    You may contact the author at: dio@freelabs.net
 */
package noThreads;

import static noThreads.DefaultCaller.*;

import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ParseLevel0 {
	private ArrayList<Integer> codes = new ArrayList<Integer>();
	private ArrayList<String> aElements = new ArrayList<String>();

    public void getStationCodes(){
    	Document doc = null;
    	for(String aUrl : theUrls){
    		// parse the input html of URL into a DOM document
    		doc = parseUrl(aUrl, 0);
			 if(doc!=null){
				 // Select all the <a> elements with [href] attribute that start with 
				 // "javascript"
				 Elements links = doc.select("a[href^=javascript]");
				 for (Element aLink : links)
					 aElements.add(aLink.outerHtml());	
			 }
			 else
				 print("THE URL --> %s, CANNOT BE HANDLED", aUrl);
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
	
	public void setaElements(ArrayList<String> aElements) {
		this.aElements = aElements;
	}

	public void setCodes(ArrayList<Integer> codes) {
		this.codes = codes;
	}

	public ArrayList<Integer> getCodes() {
		return codes;
	}
}//end Class
