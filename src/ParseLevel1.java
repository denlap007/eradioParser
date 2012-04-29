/**
	eradioParser: This program extracts the radio station 
	links along with their names, found on http://e-radio.gr, 
	so that anyone may easily create a playlist.
    
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

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ParseLevel1 {
	static ArrayList<String> stationLinks1 = new ArrayList<String>();
	static ArrayList<String> unProsessedLinks = new ArrayList<String>();
	static final int MAX_CONNECTION_ATTEMPTS  = 5;
	static ArrayList<String> titles = new ArrayList<String>();
	static String linksFileName = new String("theLinks_1.txt");
	static String unProcLinksFileName = new String("Unprocessed_links_probably_BAD.txt");
	static String titlesFileNme = new String("titles.txt");

	/**
	 * Print method
	 * @param msg
	 * @param args
	 */
	public static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }
	
	/**
	 * 
	 * @param theUrl
	 * @param conAttempts
	 * @return
	 */
	public static Document parseUrl(String theUrl, int conAttempts){
	        boolean threw = false;
	        Document doc = null;
	        if(conAttempts==MAX_CONNECTION_ATTEMPTS)
	        	return null;
	        else{
		        try {
		            doc = Jsoup.connect(theUrl).get();
		        } catch (IOException e) {
		        	print("%s  THREW EXCEPTION BUT handled", theUrl);
		            threw = true;
		        }   
		        if(threw==true){
		        	doc = parseUrl(theUrl, conAttempts+1);
		        }	        
		        return doc;	
	        }
	}
	
	
	/**
	 * 
	 * @param theCodes
	 * @throws IOException
	 */
	 public void getFirstLinks(ArrayList<Integer> theCodes) throws IOException{
		 boolean linkFound =false;
	        String temp = null;
	    	String theUrl = "http://e-radio.gr/player/player.asp?sID=";
	    	Document doc;

			for(int code : theCodes){	
				linkFound=false;
	        	theUrl = "http://e-radio.gr/player/player.asp?sID="+code;

				doc = parseUrl(theUrl, 0);
				if (doc!=null){
			        Elements media = doc.select("[src]");
			        print("Fetching %s -->  ", theUrl);
			            
			        for (Element src : media){
			        	if (src.tagName().equals("iframe")==true){
			        		temp = src.attr("abs:src");
			        		if(temp.contains("playerX")==true){	
			        			linkFound=true;
			        			temp = temp.replace(" ", "%");
			        			stationLinks1.add(temp);
								break;//link found no need to check another src on this url
			        		}
			        	}
			        	else if (src.tagName().equals("embed")==true){
			        		linkFound=true;
			        		temp =  src.attr("abs:src");
			        		temp = temp.replace(" ", "%");
			        		stationLinks1.add(temp);
			        		break;//link found no need to check another src on this url
			        	}  		
			        }//end nested for 
			        if(linkFound==false) {
						print("Unprocessed, no iframe - embed tag for url: %s", theUrl);
						unProsessedLinks.add(theUrl);
						continue;		
			        }
				}
				else{
					print("Unprocessed, no connection for url: %s", theUrl);
					unProsessedLinks.add(theUrl);
					continue;								
				}
	        }//end outer for		
			print("Unprosessed Links: %s",  unProsessedLinks.size());
			print("Processed Links: %s", stationLinks1.size());
			//write all the links to the disk
			writeLinksToFile(linksFileName, stationLinks1);
			writeLinksToFile(unProcLinksFileName, unProsessedLinks);
	 }//end method 	
	 
	 
	 /**
	  * 
	  * @param fileName
	  * @param contents
	  * @throws IOException
	  */
	 public static void writeToFile(String fileName, String contents) throws IOException{
	        OutputStreamWriter writer = new OutputStreamWriter(
	                  new FileOutputStream(fileName, true), "UTF-8");
	            BufferedWriter fbw = new BufferedWriter(writer);
	            fbw.write(contents);
	            fbw.newLine();
	            fbw.close();
	 }
	 

	 /**
	  * 
	  * @throws IOException
	  */
	 public void getTitles() throws IOException{
		 int start=0;
		 int end=0;
		 String title;
		 
		 for(String stationLink : stationLinks1){
			 if(stationLink.endsWith(".asx")==true){
				 start = stationLink.lastIndexOf("/")+1;
				 end = stationLink.lastIndexOf(".");
				 title = stationLink.substring(start, end);
				 titles.add(title);
				 writeToFile("titles.txt", title);				 
			 }
			 else{
				 start = stationLink.indexOf("title=")+6;
				 end = stationLink.indexOf("&pt");
				 if (start!=(6-1) && end!=(-1-2)){
					 title = stationLink.substring(start, end);
					 title = title.replace("%", " ");
					 System.out.println("Station Title: "+ title);
					 titles.add(title);
					 writeToFile("titles.txt", title);
				 }
				 else{
					 titles.add("NO TITLE");
					 writeToFile("titles.txt", "NO TITLE");
				 }				 
			 }		 
		 }//end for
		 print("Size of Links: %s", stationLinks1.size());
		 print("Size of Titles: %s", titles.size());
	 }//end method	
	 
	 
	 /**
	  * 
	  * @param fileName
	  * @param theLinks
	  * @throws IOException
	  */
	 public static void  writeLinksToFile(String fileName, ArrayList<String> theLinks) throws IOException {
		 for(String link : theLinks) {
			 writeToFile(fileName, link);
		 }	 
	 }
}//end of Class
