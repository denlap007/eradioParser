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

import static noThreads.ParseLevel1.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class ParseLevel2 {
	static ArrayList<String> stationLinks2 = new ArrayList<String>();
	static ArrayList<String> eradioLinks = new ArrayList<String>();
	private ArrayList<String> eradio_BAD_Links = new ArrayList<String>();
	static String links2FileName= new String("theLinks_2.txt");
	static String eradioLinksFileName = new String("eradio_links.txt");
	private String eradioBadLinksFileName = new String("eradio_BAD_links.txt");
	private String userAgent = new String("Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)");

	/**
	 * 
	 * @param theLinks
	 * @throws IOException
	 */
    public void getSecondLinks(ArrayList<String> theLinks) throws IOException {
    	String temp=null;
    	Document doc;
    	boolean flag;
    	for(String sLink : theLinks) {
    		if(sLink.endsWith(".asx")==true || sLink.endsWith(".swf")==true){
    			stationLinks2.add(sLink);
    			print("Written to file: %s", sLink);
    		}
    		else{
    			doc = parseUrl(sLink, 0);
    			if (doc!=null){
    				Elements media = doc.select("[src]");
    				print("Fetching %s -->  ", sLink);

    				flag=false;
    				for (Element src : media){
    					if (src.tagName().equals("embed")==true){
    						flag=true;
    						temp =src.attr("abs:src");
    						stationLinks2.add(temp);
    						break;//link found, load next url
    					}
    				}//end nested for
    				if (flag==false){//the code has no embed tag
    					stationLinks2.add(sLink);
    				}
    			}
    		}	
    	}//end outer for
    	writeLinksToFile(links2FileName, stationLinks2);
    	print("Written %s to file, second links.", stationLinks2.size());
    }//end method
    
    /**
     * 
     * @param theLinksFinal
     * @param theTitles
     * @throws IOException
     */
    public void getFinalLinks(ArrayList<String> theLinksFinal, ArrayList<String> theTitles) throws IOException {
    	String title, sLink, inputLine, link;
    	int start, end;
    	boolean threw = false;
    	URL address = null;
    	BufferedReader in = null;
    	
    	for(int i=0; i<theLinksFinal.size(); i++) {
    		threw = false;
    		sLink = theLinksFinal.get(i);
    		title = theTitles.get(i);
    		if(sLink.endsWith(".asx")==true) {
    			try {
    				address = new URL(sLink);
					in = new BufferedReader(
							new InputStreamReader(
								address.openStream()));
					} 
    			catch (IOException e) {
    				print(e.getMessage()+"\n");
					print("INVALID LINK --> %s. Event Handled", sLink);
					threw=true;
					}
    			if (threw==false) {
               		start=0;
            		end=0;  
            		while ((inputLine = in.readLine()) != null) { 
                		start = inputLine.indexOf("http");
            			end = inputLine.lastIndexOf('\"');   
            			if (start!=-1 && end !=-1){
            				link = inputLine.substring(start, end);
            				System.out.println(link);
            				if(validUrl(link, 0)==true){
            					eradioLinks.add(title);
            					eradioLinks.add(link);
            				}
            				else{
            					eradio_BAD_Links.add(title);
            					eradio_BAD_Links.add("_______BAD_LINK_______"+link);
            				}
            				break;//if in a .asx you find a link break;
            			}
            		}   				
    			}
    			else {
    				eradio_BAD_Links.add(title);
					eradio_BAD_Links.add("_______BAD_LINK_______" +sLink);    				
    			}
    		}
    		else{
    			System.out.println(sLink);
    			if(validUrl(sLink, 0)==true) {
    				eradioLinks.add(title);
    				eradioLinks.add(sLink);
    			}	
    			else {
    				eradio_BAD_Links.add(title);
					eradio_BAD_Links.add("_______BAD_LINK_______" +sLink);
    			}
    		}
    		if(in!=null)
    			in.close();
    	}
    	ParseLevel1.writeLinksToFile(eradioLinksFileName, eradioLinks);
    	ParseLevel1.writeLinksToFile(eradioBadLinksFileName, eradio_BAD_Links);
    }
    
    /**
     * 
     * @param theUrl
     * @param conAttempts
     * @return
     * @throws IOException
     */
    public int getResposeCode(String theUrl, int conAttempts) throws IOException{// throws IOException 
	    URL newUrl = new URL(theUrl); 
	    //These codes are returned to indicate either fault or not.
	    int ERROR_CODE = 1000, OK_CODE = 0;
	    
	    HttpURLConnection huc =  (HttpURLConnection)  newUrl.openConnection(); 
	    huc.setRequestMethod("HEAD"); 
	    huc.setRequestProperty("User-Agent", userAgent);
	    huc.setReadTimeout(2000);
	    huc.connect(); 
	    try {
			return huc.getResponseCode();
		} catch (java.net.SocketException e) {
			if(e.getMessage().equalsIgnoreCase("Unexpected end of file from server")){
				return OK_CODE; // link still valid so return a small positive int that isn't a http status code
			}
			else
				return ERROR_CODE; //error, return a large int that isn't included in any http status code
		}catch (java.net.SocketTimeoutException e){
			if(e.getMessage().equalsIgnoreCase("Read timed out")){
				if(conAttempts!=MAX_CONNECTION_ATTEMPTS)
					return getResposeCode(theUrl, conAttempts+1);
				else
					return ERROR_CODE; //ERROR return a large int that isn't included in any http status code
			}
			else
				return ERROR_CODE;
		}catch (IOException e){
			e.printStackTrace();
			return ERROR_CODE;	//error, return a large int that isn't included in any http status code		
		}
    }
    
    /**
     * @param theUrl
     * @param conAttempts
     * @return
     * @throws IOException
     */
    public boolean validUrl(String theUrl, int conAttempts) throws IOException{
    	long total_time=0, endTime=0;
    	long startTime = System.currentTimeMillis();
    	URL link = new URL(theUrl);
    	int CONNECT_TIMEOUT = 5000, READ_TIMEOUT = 2000;
    	
    	HttpURLConnection huc =  (HttpURLConnection)  link.openConnection(); 
    	huc.setRequestProperty("User-Agent", userAgent);
    	huc.setConnectTimeout(CONNECT_TIMEOUT);
    	huc.setReadTimeout(READ_TIMEOUT);
    	try {
    		huc.connect();
    	} catch (java.net.ConnectException e) {
    		print(e.getMessage()+"\n");
    		if(e.getMessage().equalsIgnoreCase("Connection timed out")){
    			if(conAttempts!=MAX_CONNECTION_ATTEMPTS){
    				System.out.println("Recurrencing validUrl method...");
    				return validUrl(theUrl, conAttempts+1);
    			}
    			else
    				return false;
    		}
    		else
    			return false;
    	} catch(java.net.SocketTimeoutException e){
    		print(e.getMessage()+"\n");
    		if(e.getMessage().equalsIgnoreCase("connect timed out")){
    			if(conAttempts!=MAX_CONNECTION_ATTEMPTS){
    				System.out.println("Recurrencing validUrl method...");
    				return validUrl(theUrl, conAttempts+1);
    			}
    			else
    				return false;
    		}
    		else
    			return false;
    	}catch(IOException e){
    		print(e.getMessage()+"\n");
    		return false;
    	}
    	UrlValidator urlValidator = new UrlValidator();
    	if(urlValidator.isValid(theUrl)==true){
    		System.out.println("valid url form");
    		if(huc.getContentType()!=null) {
    			System.out.println("Content: "+huc.getContentType());
    			if(huc.getContentType().equals("text/html") || huc.getContentType().equals("unknown/unknown")){
    				if( getResposeCode(theUrl, 0) >= java.net.HttpURLConnection.HTTP_BAD_REQUEST ){
    					System.out.println("Server Response Code: "+ getResposeCode(theUrl, 0));
    					return false;
    				}               		
    			}
    			System.out.println(huc.getContentType());
    			endTime = System.currentTimeMillis();
    			total_time = total_time + (endTime-startTime);
    			System.out.println("Total elapsed time is :"+ total_time+"\n"); 
    			return true;
    		}     		
    		else {//edw erxetai an den prolavei na diavasei h an einai null to content
    			endTime = System.currentTimeMillis();
    			total_time = total_time + (endTime-startTime);
    			System.out.println("Total elapsed time is :"+ total_time+"\n"); 
    			if(conAttempts!=MAX_CONNECTION_ATTEMPTS){
    				System.out.println("Recurrencing validUrl method...");
    				return validUrl(theUrl, conAttempts+1);
    			}
    			else
    				return false;
    		}	
    	}
    	else {
    		endTime = System.currentTimeMillis();
    		total_time = total_time + (endTime-startTime);
    		System.out.println("Total elapsed time is :"+ total_time+"\n"); 
    		return false;    		
    	}
    }
}
