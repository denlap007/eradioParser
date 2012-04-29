/**
	eradioParser: This program extracts the radio station 
	links along with their names, found on http://e-radio.gr, 
	so that anyone may easily create a playlist.
    
    Copyright (C) 2012  Lappas Dionysis
    
    This file is part of eradioParser.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
    
    You may contact the author at: dio@freelabs.net
 */


package noThreads;


/**
 * This program accepts command line arguments. 
 * The command line arguments supported are: 
 * 1. File Location. Location of file on disk i.e. C://a.txt or /Usr/home/a.txt
 * 2. String(s). The Strings are of the form of a Url. i.e. http://www.e-radio.gr/locations/athens.asp
 **/

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class theMain {

	/**
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException, InterruptedException{
		//variables that hold time in msec, in order to calculate
		//how much time lasts a program execution
		long total_time=0, startTime=0, endTime=0;
		startTime = System.currentTimeMillis();
		
		/*
		 * Print GPL message
		 */
		System.out.println("EradioParser Copyright (C) 2012 Lappas Dionysis \n"+
				"This program comes with ABSOLUTELY NO WARRANTY. This is free software, and you are\n" +
				"welcome to redistribute it under certain conditions; details: http://www.gnu.org/licenses/gpl.txt \n");

		
		/*
		 * This structure will hold all the file names used on this project.
		 * Disk files are used to store the output and check data validity 
		 * during different steps of program's execution (debugging). 
		 * WILL BE REMOVED IN THE FUTURE.
		 */
		
		ArrayList<String> diskFiles = new ArrayList<String>();
		
		/*
		 * Create an object of Class ProcessCla in order to process
		 * command line arguments if necessary. This Object is
		 * also necessary for the menu to work, as it (the Menu) accesses 
		 * the static fields of ProcessCla Class.
		 */
		ProcessCla claObject = new ProcessCla();
		
		/*
		 * process command line arguments
		 * IF no command line argument inserted load the MENU
		 * else If there exists only one (1) argument then
		 * --if it ends with .txt it's the filaPath of a file on disk 
		 * --else consider it String
		 * else there exist more arguments, Strings (string links)
		 * Save the arguments to variables
		 */
		if(args.length==0) {
			Menu menuObject = new Menu();
			menuObject.createMenu();
		}
		else if(args.length==1){
			claObject = new ProcessCla();
			if(args[0].endsWith(".txt")) {
				claObject.processFile(args[0]);
			}
			else {
				claObject.processStrings(args);	
			}

		}
		else{
			claObject.processStrings(args);		
		}
		
		/*
		 * Create an object of Class ParseLevel0 in order to get 
		 * the codes of the radio stations
		 */
		ParseLevel0 pl0 = new ParseLevel0();
		pl0.getCodes();	
		
		/*
		 * Create an object of Class ParseLevel1. Parse the codes to GetFirstLinks method
		 * and get the first links of the radio stations. From the extracted links
		 * get the tiles with the getTitles method.
		 */
		
		ParseLevel1 pl1 = new ParseLevel1();
		pl1.getFirstLinks(ParseLevel0.codes);
		pl1.getTitles();
		
		ParseLevel2 pl2 = new ParseLevel2();
		pl2.getSecondLinks(ParseLevel1.stationLinks1);
		pl2.getFinalLinks(ParseLevel2.stationLinks2, ParseLevel1.titles);
		
		Playlist p = new Playlist();
		p.makeValidXmlLinks(ParseLevel2.eradioLinks);
		
		endTime = System.currentTimeMillis();
		total_time = total_time + (endTime-startTime);
		System.out.println("Total elapsed time is :"+ total_time+" msec\n"); 
		
		//Cleanup, delete unnecessary files
		diskFiles.add(ParseLevel1.linksFileName);
		diskFiles.add(ParseLevel1.titlesFileNme);
		diskFiles.add(ParseLevel2.eradioLinksFileName);
		diskFiles.add(ParseLevel2.links2FileName);
		for(String name : diskFiles){
			File a = new File(name);
			a.delete();
		}

		System.out.println("--> Parsed in total: "+
				ParseLevel1.stationLinks1.size()+
				" station links. Valid links: " +
				ParseLevel2.eradioLinks.size()/2+
				"/"+
				ParseLevel1.stationLinks1.size()+
				"\nProgram Exiting...");
	}
}
