package noThreads;
/**
 * This program accepts command line arguments. 
 * The command line arguments supported are: 
 * 1. File Location. Location of file on disk i.e. C://a.txt or /Usr/home/a.txt
 * 2. String(s). The Strings are of the form of a Url. i.e. http://www.e-radio.gr/locations/athens.asp
 **/

import java.io.IOException;

public class theMain {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException  {
		//variables that hold time in msec, in order to calculate
		//how much time lasts a program execution
		long total_time=0, startTime=0, endTime=0;
		startTime = System.currentTimeMillis();
		
		/*
		 * Create an object of Class ProcessCla in order to process
		 * command line arguments if necessary. This Object is
		 * also necessary for the menu to work, as it accesses 
		 * the static fields of ProcessCla Class.
		 */
		ProcessCla claObject = new ProcessCla();;
		
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
		 * and get the firts links of the radio stations. From the extracted links
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
		System.out.println("Total elapsed time is :"+ total_time+"\n"); 
	
		System.out.println("Parsed in total: "+
				ParseLevel1.stationLinks1.size()+
				" station links. Valid links: +" +
				ParseLevel2.eradioLinks.size()/2+
				"\nProgram Exiting...");
		
		


	}

}
