package noThreads;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public final class ProcessCla {
	static String filePath;
	static ArrayList<String> theUrls;
	
	public ProcessCla(){
		filePath = null;
		theUrls = new ArrayList<String>();	
	}

	
	public void processFile(String filePath) throws IOException {
    	String inputLine;
        // Open file
        FileInputStream fstream = new FileInputStream(filePath);
        // Get the object of DataInputStream
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        //Read File Line By Line
        while ((inputLine = br.readLine())!= null){
        	theUrls.add(inputLine);
        }
        
        System.out.print("\n*** "+theUrls.size() +" urls loaded successfully from file! "+"***\n");
		in.close();
	}
	
	public void processStrings(String[] args) {
		for(String anArg :args) {
	        try {
				@SuppressWarnings("unused")
				URL link = new URL(anArg);
			} catch (MalformedURLException e) {
				System.out.print("ERROR: "+anArg+" is not a valid url!\n");
				continue;
			}
			theUrls.add(anArg);   	
		}	
		System.out.print("\n*** "+theUrls.size() +" urls loaded successfully from terminal! "+"***\n");		
	}
	
	/**
	 * Setters and Getters
	 * 
	 */
	public String getFilePath() {
		return filePath;
	}


	public void setFilePath(String filePath) {
		ProcessCla.filePath = filePath;
	}


	public ArrayList<String> getLinks() {
		return theUrls;
	}


	public void addLinks(String link) {
		ProcessCla.theUrls.add(link);
	}
}
