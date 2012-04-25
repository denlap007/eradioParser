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
	private String links2FileName= new String("theLinks_2.txt");
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
					e.printStackTrace();
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
    	ParseLevel1.writeLinksToFile("eradio_links.txt", eradioLinks);
    	ParseLevel1.writeLinksToFile("eradio_BAD_links.txt", eradio_BAD_Links);
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
	    HttpURLConnection huc =  (HttpURLConnection)  newUrl.openConnection(); 
	    huc.setRequestMethod("HEAD"); 
	    huc.setRequestProperty("User-Agent", userAgent);
	    huc.setReadTimeout(2000);
	    huc.connect(); 
	    try {
			return huc.getResponseCode();
		} catch (java.net.SocketException e) {
			if(e.getMessage().equalsIgnoreCase("Unexpected end of file from server")){
				return 0; // link still valid so return a small positive int that isn't a http status code
			}
			else
				return 1000; //error, return a large int that isn't included in any http status code
		}catch (java.net.SocketTimeoutException e){
			if(e.getMessage().equalsIgnoreCase("Read timed out")){
				if(conAttempts!=MAX_CONNECTION_ATTEMPTS)
					return getResposeCode(theUrl, conAttempts+1);
				else
					return 1000; //ERROR return a large int that isn't included in any http status code
			}
			else
				return 1000;
		}catch (IOException e){
			e.printStackTrace();
			return 1000;	//error, return a large int that isn't included in any http status code		
		}
    }
    
    
    /**
     * 
     * @param theUrl
     * @param conAttempts
     * @return
     * @throws IOException
     */
    public boolean validUrl(String theUrl, int conAttempts) throws IOException{
		long total_time=0;
		long startTime = System.currentTimeMillis();
        URL link = new URL(theUrl);
	    HttpURLConnection huc =  (HttpURLConnection)  link.openConnection(); 
	    huc.setRequestProperty("User-Agent", userAgent);
	    huc.setConnectTimeout(5000);
	    huc.setReadTimeout(2000);
	    try {
			huc.connect();
		} catch (java.net.ConnectException e) {
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
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
            	long endTime = System.currentTimeMillis();
            	total_time = total_time + (endTime-startTime);
        		System.out.println("Total elapsed time is :"+ total_time+"\n"); 
        		return true;
            }     		
        	else {//edw erxetai an den prolavei na diavasei h an einai null to content
        		long endTime = System.currentTimeMillis();
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
    		long endTime = System.currentTimeMillis();
        	total_time = total_time + (endTime-startTime);
    		System.out.println("Total elapsed time is :"+ total_time+"\n"); 
    		return false;    		
    	}
    }
}
