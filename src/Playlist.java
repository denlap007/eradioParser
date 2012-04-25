package noThreads;

import static noThreads.ParseLevel1.*;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Playlist {
	private String fileName = new String("xml_ready_links.txt");
	
	public ArrayList<String> loadFromFile(String linksFile) throws IOException{
    	String inputLine;
		// Open file
        FileInputStream fstream = new FileInputStream(linksFile);
        // Get the object of DataInputStream
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        //Read File Line By Line
        ArrayList<String> links = new ArrayList<String>();
        while ((inputLine = br.readLine())!= null){
        			links.add(inputLine);
        }
        in.close();
        return links;
	}
	
	 public static void  writeXmlReadyLinks(String fileName, ArrayList<String> theLinks) throws IOException {
		 for(String link : theLinks) {
			 writeToFile(fileName, org.apache.commons.lang3.StringEscapeUtils.escapeXml(link));
		 }	 
	 }
	
	public void makeValidXmlLinks(ArrayList<String> links) throws IOException{

		writeXmlReadyLinks(fileName, links);
		}
		
	}

