package noThreads;

import static noThreads.ParseLevel1.*;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * @DIFFERENCES_WITH_ParseLevel2
 * extra in this class:
 * --> loadFromFile() method
 * --> class SaxHandler
 * --> readAsx(String asxFile) method
 * --> validUrl(String theUrl) old implementation, in comments
 */
public class ParseLevel2_bup {
	static ArrayList<String> stationLinks2 = new ArrayList<String>();
	static ArrayList<String> eradioLinks = new ArrayList<String>();
	private ArrayList<String> eradio_BAD_Links = new ArrayList<String>();
	private String links2FileName= new String("theLinks_2.txt");
	private String userAgent = new String("Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)");

	/**
	 * 
	 * @throws IOException
	 */
	public void loadFromFile() throws IOException{
    	String inputLine;
		// Open file
        FileInputStream fstream = new FileInputStream(linksFileName);
        // Get the object of DataInputStream
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        //Read File Line By Line
        while ((inputLine = br.readLine())!= null){
        	stationLinks1.add(inputLine);
        }
        in.close();
	}
	
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
     * @Description: Custom handler for SAX parser
     *
     */
    private final class SaxHandler extends DefaultHandler {
    	private String linkFromAsx= new String();
    	private boolean linkFromAsxFound = false;
        
        public void startElement(String uri, String localName,
                String qName, Attributes attrs) throws SAXException {

            // Process only the first REF element
            if (qName.equalsIgnoreCase("REF")==true && isLinkFromAsxFound()==false) {
            	setLinkFromAsxFound(true);
            	setLinkFromAsx(attrs.getValue("HREF"));
            }
        }
        
		/**
		 * @return the linkFromAsx
		 */
		public String getLinkFromAsx() {
			return linkFromAsx;
		}

		/**
		 * @param linkFromAsx the linkFromAsx to set
		 */
		public void setLinkFromAsx(String linkFromAsx) {
			this.linkFromAsx = linkFromAsx;
		}

		/**
		 * @return the linkFromAsxFound
		 */
		public boolean isLinkFromAsxFound() {
			return linkFromAsxFound;
		}

		/**
		 * @param linkFromAsxFound the linkFromAsxFound to set
		 */
		public void setLinkFromAsxFound(boolean linkFromAsxFound) {
			this.linkFromAsxFound = linkFromAsxFound;
		}
    }
    
    /**
     * @DEscription: Read an .asx file using SAX parser and return the first link
     * 
     * @param asxFile
     * @return
     */
    public String readAsx(String asxFile) {
    	SaxHandler handler = null;
		  try {
	            // creates and returns new instance of SAX-implementation:
	            SAXParserFactory factory = SAXParserFactory.newInstance();
	            
	            // create SAX-parser...
	            SAXParser parser = factory.newSAXParser();
	            // .. define our handler:
	            handler = new SaxHandler();
	            
	            // and parse:
	            parser.parse(asxFile, handler);
	            
	        } catch (Exception ex) {
	            ex.printStackTrace(System.out);
	        }  	
    	return handler.getLinkFromAsx();
    }
    
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
    public boolean validUrl(String theUrl, int conAttempts) throws IOException{// throws IOException {
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

	
    
    
/*    public boolean validUrl(String theUrl) throws IOException {
		long total_time=0, startTime=0, endTime=0;
		
		startTime = System.currentTimeMillis();
        URL link = new URL(theUrl);
        UrlValidator urlValidator = new UrlValidator();
        java.net.URLConnection linkCon = link.openConnection();
    	if(urlValidator.isValid(theUrl)==true){
            if(linkCon.getContentType()!=null) {
            	if(linkCon.getContentType().equals("text/html") || linkCon.getContentType().equals("unknown/unknown")){
                	if( getResposeCode(theUrl) >= java.net.HttpURLConnection.HTTP_BAD_REQUEST ){
            			System.out.println("Server Response Code: "+ getResposeCode(theUrl));
            			return false;
            		}
            	}

            	endTime = System.currentTimeMillis();
            	total_time = total_time + (endTime-startTime);
        		System.out.println("Total elapsed time is :"+ total_time+"\n"); 
        		return true;
            }     		
        	else {
        		endTime = System.currentTimeMillis();
            	total_time = total_time + (endTime-startTime);
        		System.out.println("Total elapsed time is :"+ total_time+"\n"); 
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
    */
    
    
    
    
    
    
    
    

}
