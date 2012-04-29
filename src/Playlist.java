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

import static noThreads.ParseLevel1.*;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Playlist {
	private String fileName = new String("xml_ready_links.txt");
	
	//method used on dev stage to quickly get the links from file and not wait for a run execution
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

