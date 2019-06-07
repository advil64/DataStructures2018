package lse;

import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.*;
import java.applet.Applet;
import java.awt.Font;
import java.awt.Graphics;

public class runner {

    public static void main(String[] args){

        //constructs a search engine object
        LittleSearchEngine lse = new LittleSearchEngine();
        //constructs a scanner object
        Scanner input = new Scanner(System.in);
        //to be passed to the makeindex method
        String docsfile;
        //to be passed as well
        String noisefile;
        //Accept the words to be searched
        String word1; String word2;

        try {
            //prompt the user to enter docs file
            System.out.print("Please enter the docs file --> ");
            //accept the input
            docsfile = input.nextLine();
            //prompt the user to enter the noise file
            System.out.print("Please enter the noise file --> ");
            //accept the input
            noisefile = input.nextLine();
            //word 1
            System.out.print("Would you like to search for a word? --> ");
            //save the word
            word1 = input.nextLine();
            //another one
            System.out.print("Would you like to search for another word? --> ");
            //save again
            word2 = input.nextLine();
            //user should not enter anything else
            input.close();
            //set the output stream
            PrintStream o = new PrintStream(new File("Output.txt"));
            // Store current System.out before assigning a new value
            PrintStream console = System.out;
            // Assign o to output stream
            System.setOut(o);
            //enter the two files into makeindex
            lse.makeIndex(docsfile, noisefile);
            //check to see if top5search works
            ArrayList<String> matches = lse.top5search(word1, word2);
            //print the matches
            for(String y : matches){
                //print each one
                System.out.println("- " + y + " ");
            }
            //print the keyset
            for(String x : lse.keywordsIndex.keySet()) {
                System.out.println();
                System.out.println(x);
                ArrayList<Occurrence> occ = lse.keywordsIndex.get(x);
                //System.out.println();
                for(int i = 0; i < x.length(); i++){
                    System.out.print("▀");
                }
                System.out.println();
                for(Occurrence y : occ) {
                    System.out.print(y.document + " --> ");
                    System.out.print(y.frequency);
                    System.out.println();
                }
            }
        }
        //makeindex can throw an exception if the file is invalid
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
