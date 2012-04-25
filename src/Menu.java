package noThreads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringEscapeUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Menu { 
	private int choice=-1;
	private final String URL= new String("http://e-radio.gr");

	public void createMenu() throws IOException, InterruptedException {
		Document doc = null;
		BufferedReader br = null;
		
		System.out.println("\t\t-->MENU Options <--" +
				"\n1. Extract the whole <e-radio.gr> station playlist. " +
				"\n2. View the available stations by Category and then get a playlist." +
				"\n3. View the available stations by Location and then get a playlist." +
				"\n4. Exit."+
				"\n\n"+
				"Please make a choice (1-4): ");
		 br = new BufferedReader(new InputStreamReader(System.in));

	     try {
	    	 choice = Integer.parseInt(br.readLine());
	     } catch (IOException e) {
	    	 System.out.println("Error!");
	    	 System.exit(1);
	     }
		switch (choice) {
		case(1):	//GET all the e-radio category links (in order to get all the links)
			doc = Jsoup.connect(URL).get();
			Elements links = doc.select("div[class=menuOptions]").select("a[href*=/categories/]");
			
			for(Element link : links)
				ProcessCla.theUrls.add(link.attr("abs:href"));
			 System.out.println("...Processing <All e-radio> station links");
			 break;
			
		case(2):	//Get CATEGORIES
			doc = Jsoup.connect(URL).get();
			Elements categoryLinks = doc.select("div[class=menuOptions]").select("a[href*=/categories/]");
			
			System.out.println("E-radio stations available categories: " +
					"\n");
			for(int i=0; i< categoryLinks.size();i++){
				System.out.println(i+1+
						".  "+
						StringEscapeUtils.unescapeHtml4(categoryLinks.get(i).html()));
			}
			System.out.println("\n" +
					"Please make a choise (1-"+
					categoryLinks.size()+
					"): ");
			
			 br = new BufferedReader(new InputStreamReader(System.in));
		     try {
		    	 choice = Integer.parseInt(br.readLine());
		     } catch (IOException e) {
		    	 System.out.println("Error!");
		    	 System.exit(1);
		     }
		     if(choice<=categoryLinks.size() && choice>=1){
		    	 ProcessCla.theUrls.add(categoryLinks.get(choice-1).attr("abs:href"));
		    	 System.out.println(categoryLinks.get(choice-1).attr("abs:href"));
		    	 System.out.println("...Processing the <"+
		    			 StringEscapeUtils.unescapeHtml4(categoryLinks.get(choice-1).html())+
		    			 "> category");
		     }
		     else
		    	 System.out.println("Wrong selection...");
		     
		     break;
		case(3)://Get LOCATIONS
			doc = Jsoup.connect(URL).get();
			Elements locationLinks = doc.select("div[class=menuOptions]").select("a[href*=/locations/]");
			
			System.out.println("E-radio stations available locations: " +
					"\n");
			for(int i=0; i< locationLinks.size();i++){
				System.out.println(i+1+
						".  "+
						StringEscapeUtils.unescapeHtml4(locationLinks.get(i).html()));
			}
			System.out.println("\n" +
					"Please make a choise (1-"+
					locationLinks.size()+
					"): ");
			
			 br = new BufferedReader(new InputStreamReader(System.in));
		     try {
		    	 choice = Integer.parseInt(br.readLine());
		     } catch (IOException e) {
		    	 System.out.println("Error!");
		    	 System.exit(1);
		     }
		     if(choice<=locationLinks.size() && choice>=1){
		    	 ProcessCla.theUrls.add(locationLinks.get(choice-1).attr("abs:href"));
		    	 System.out.println(locationLinks.get(choice-1).attr("abs:href"));
		    	 System.out.println("...Processing <"+
		    			 StringEscapeUtils.unescapeHtml4(locationLinks.get(choice-1).html())+
		    			 "> locatino");
		     }
		     else{
		    	System.out.println("Wrong selection!");
				System.out.println("Exiting program...");
				System.exit(1); 
		     }

		     break;
		     
		case(4):
			System.out.println("Exiting program...");
			System.exit(1);
			break;

		default:
			System.out.println("Invalid choice! Exiting...");
			System.exit(1);
			break;

		}
	}//end method
}//end Class
