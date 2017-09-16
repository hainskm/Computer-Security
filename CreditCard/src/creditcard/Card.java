package creditcard;

import java.io.*;
import java.util.regex.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Brendan Blanchard and Kevin Hainsworth
 */
public class Card {
    private File file;
    private FileInputStream in;
    private BufferedInputStream buffer;
    private int numBytes;
	private static int validCards = 0;
    private byte array[];
    private String regEx1 ="%B(\\d{13,19})\\^([A-z]*\\/[A-z]+)(\\^[0-9])*(\\d{7,})\\?";
    private String regEx2 = ";(\\d{13,19})=(\\d{2})(\\d{2})(\\d{3})(\\d{4})(\\d{3})(\\d*)\\?";
    private static ArrayList<String> track1Hits = new ArrayList<String>();
    private static ArrayList<String> track2Hits = new ArrayList<String>();
    private String s;
    private static ArrayList<String> savedNum = new ArrayList<String>();
	private static ArrayList<String> savedName= new ArrayList<String>();
	private static ArrayList<String> savedDate= new ArrayList<String>();
	private static ArrayList<String> savedPin= new ArrayList<String>();
	private static ArrayList<String> savedCVV= new ArrayList<String>();
	private static String path;
    
    public Card()
    {
        //%B[0-9]\d{13,19}\^([A-z]*\/[A-z]*)\w{2,26}\^[0-9]*\d{7,}
    	
        file = new File(path);
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Card.class.getName()).log(Level.SEVERE, null, ex);
        }
        buffer = new BufferedInputStream(in);
        try {
            numBytes = buffer.available();
        } catch (IOException ex) {
            Logger.getLogger(Card.class.getName()).log(Level.SEVERE, null, ex);
        }
        array = new byte[numBytes];
        try {
            in.read(array,0, numBytes);
        } catch (IOException ex) {
            Logger.getLogger(Card.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            s = new String(array,"UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Card.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        
    }
    public void Matcher()
    {
      Pattern p1 =Pattern.compile(regEx1);
      Pattern p2 =Pattern.compile(regEx2);
              
      Matcher m1 = p1.matcher(s);
      Matcher m2 =p2.matcher(s);
      
      
      while(m1.find())
      {
    	  String track1 = m1.group();
    	  track1Hits.add(track1);
          //System.out.println(track1);    
      }
      
      while(m2.find())
      {
    	  String track2 = m2.group();
    	  track2Hits.add(track2);
          //System.out.println(track2);         
      }
      //Removes duplicate track1 info 
      if(track1Hits.size() >1){
    	  for(int i = 0; i<track1Hits.size(); i++){
    		  for(int j = i+1; j< track1Hits.size();j++){
    			  if(j < track1Hits.size()){
    				  if(track1Hits.get(i).equals(track1Hits.get(j))){
    					  track1Hits.remove(j);
    				  }
    			  }
    			  
    		  }    	 
    	  }
      }
      //Removes duplicate track2 info
      if(track2Hits.size() >1){
    	  for(int i = 0; i<track2Hits.size(); i++){
    		  for(int j = i+1; j< track2Hits.size();j++){
    			  if(j < track2Hits.size()){
    				  if(track2Hits.get(i).equals(track2Hits.get(j))){    				  
    					  track2Hits.remove(j);
    					  j--;
    				  }
    			  }
    			  
    		  }
    	    	 
    	  }
      }
      //first for loop: pulls out relevant card information for first track1 
      for(int i = 0; i<track1Hits.size();i++){
    	  String act = "",name = null,add;
          //regex to pull out card number
          String regexAcct1 = "(\\d{13,19})\\^";
          Pattern account1 = Pattern.compile(regexAcct1);
          Matcher m3 = account1.matcher(track1Hits.get(i));
     
          while(m3.find()){
        	  act = m3.group();
        	  //removes sentinels/field separator
        	  act = act.substring(0, act.length()-1);
          }
          //regex to pull out cardholder's name
          String regexName1 = "([A-z]*\\/[A-z]*)";
          Pattern name1 = Pattern.compile(regexName1);
          Matcher m4 = name1.matcher(track1Hits.get(i));
          
          while(m4.find()){
        	  name = m4.group();
        	//removes sentinels/field separator
        	  name = name.substring(1, name.length()-1);
          }
          
          //nested for loop to go through track2 info and pull out info like with track1
          //However, afterpulling info, compare to current track1 info to see if they match
          for(int j = 0; j < track2Hits.size(); j++){
        	  String act2 = null,addit = null;
              //regex to pull out card number
              String regexAcct2 = ";(\\d{13,19})=";
              Pattern account2 = Pattern.compile(regexAcct2);
              Matcher m6 = account2.matcher(track2Hits.get(j));             
              
              while(m6.find()){
            	  act2 = m6.group();
            	//removes sentinels/field separator
            	  act2 = act2.substring(1, act2.length()-1);
              }
              //regex to pull out all additional info like exp. date, cvv, pin etc.
              String regexAdditional2 = "=(\\d{2})(\\d{2})(\\d{3})(\\d{4})(\\d{3})(\\d*)";
              Pattern add2 = Pattern.compile(regexAdditional2);
              Matcher m8 = add2.matcher(track2Hits.get(j));
              while(m8.find()){
            	  addit = m8.group();
            	//removes sentinels/field separator
            	  addit = addit.substring(1, addit.length());;
              }
              //check if track numbers match, if they do, save card information
              if(act.equals(act2)){
            	  validCards++;
            	  savedNum.add(act);
            	  name = name.replace('/', ' ');	  
            	  savedName.add(name);
            	  savedDate.add(""+addit.charAt(2)+addit.charAt(3)+"/20"+addit.charAt(0)+addit.charAt(1));
            	  savedPin.add(""+addit.charAt(7)+addit.charAt(8)+addit.charAt(9)+addit.charAt(10));
            	  savedCVV.add(""+addit.charAt(11)+addit.charAt(12)+addit.charAt(13));
              }
              
          }
      }
      
      
              
    }
    
    private static Card card;
    public static void main(String[] args) {
    	if(args.length == 0){
    		System.out.println("No file provided.");
    		System.exit(0);
    	}
    	else{
    		path = args[0];
    	}
        card = new Card();
        card.Matcher();
        /*for(int i = 0; i<track1Hits.size();i++){
        	//System.out.println(track1Hits.get(i));
        }
        for(int i = 0; i<track2Hits.size();i++){
        	//System.out.println(track2Hits.get(i));
        }*/
        //print out number of valid cards and their information if there were any.
        if(validCards == 0){
        	System.out.println("There are no valid credit cards in the memory data.");
        }
        else{
        	System.out.println("There is " + validCards + " piece of credit card information in the memory data!");
            for(int i = 0; i<validCards;i++){
            	int n = i+1;
            	System.out.println("Information of credit card " + (n) + ":");
                System.out.println("Cardholder's Name: "+ savedName.get(i));
                System.out.println("Card Number: " + savedNum.get(i));
                System.out.println("Expiration Date: " + savedDate.get(i));
                System.out.println("Encrypted Pin: " + savedPin.get(i));
                System.out.println("CVV Number: " + savedCVV.get(i));
            }
        }
 
    }
}


