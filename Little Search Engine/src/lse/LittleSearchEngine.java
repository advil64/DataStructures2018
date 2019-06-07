package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {

	    String toRead = "";
	    //finds the textfile from the given pathname
        File file = new File(docFile);
        //tries to read the file
        try {
            //scans the textfile line by line
            Scanner sc = new Scanner(file);
            // adds all lines to the toread string
            while (sc.hasNextLine()) { toRead = toRead.concat(sc.nextLine() + " "); }
            //no more input
            sc.close();
        }
        //catches any exceptions and throws an exception of its own
        catch (FileNotFoundException e) {throw new FileNotFoundException();}
		//returns the keywords encountered on the docFile
		HashMap<String, Occurrence> keywords = new HashMap<>();
		//create an array with every word
        String[] words = toRead.split(" ");
        //go through the array of words
        for(String x : words){
            //calls getkeyword method
            String key = getKeyword(x);
            //check if the keyword is valid
            if(key != null){
                //if the word already exists, you want to update its frequency
                if(keywords.containsKey(key)){keywords.get(key).frequency++;}
                //if it does not exist, you want to put the word in there
                else{keywords.put(key, new Occurrence(docFile, 1));}
            }
        }
		//returns the updated map
		return keywords;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
	    //iterate the keyset
        Set<String> keys = kws.keySet();
        //construct the iterator
        Iterator<String> it = keys.iterator();
        //loop through the iterator
        while(it.hasNext()){
            ArrayList<Occurrence> toadd = new ArrayList<>();
            //access the keys individually
            String key = it.next();
            //check if the main table contains the key
            if(keywordsIndex.containsKey(key)){toadd = keywordsIndex.get(key);}
            //add the new guy in
            toadd.add(kws.get(key));
            //make sure to sort the guy
            insertLastOccurrence(toadd);
            //if there exists a previous key it gets replaced
            keywordsIndex.put(key, toadd);
        }
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * NO OTHER CHARACTER SHOULD COUNT AS PUNCTUATION
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be stripped
	 * So "word!!" will become "word", and "word?!?!" will also become "word"
	 * 
	 * See assignment description for examples
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
	   if(word.equals("")){return null;}
	    //is the string valid
	    boolean check = false;
	    //string to be returned with no punctuation and lowercase chars
	    String toReturn = "";
	    //acceptable punctuation
        Character[] acc = {'.', ',', '?', ':', ';', '!'};
        //convert to list
        List<Character> acceptable = Arrays.asList(acc);
        //loop through the characters
        for(int i = 0; i < word.length(); i++){
            //current character
            Character curr = word.charAt(i);
            //next character
            Character nex;
            if(i == word.length() - 1){nex = null;}
            else {nex = word.charAt(i+1);}
            //character is valid if the character is lowercase
            if(Character.isLowerCase(curr) && !acceptable.contains(curr)){toReturn = toReturn.concat(curr.toString()); check = true;}
            //if the character is uppercase, then you
            else if(Character.isUpperCase(curr) && !acceptable.contains(curr)){toReturn = toReturn.concat(curr.toString().toLowerCase()); check = true;}
            //check if the character is punctuation
            else if(acceptable.contains(curr)){
                //if next is null of next is also a punctuation:
                if(nex == null || acceptable.contains(nex)){check = true;}
                //the char is unidetified
                else{
                    //the next character is bad
                    check = false; break;
                }
            }
            //unidentified character
            else{check = false; break;}
        }
        if(toReturn.equalsIgnoreCase("")){return null;}
        //if there is no punctuation in the middle, return the string
        if(check && !noiseWords.contains(toReturn)){return toReturn;}
        //else return null
        return null;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		//need to return the binary search trace
		ArrayList<Integer> search = new ArrayList<>();
		//there's nothing to order if there's only one thing in the list
		if (occs.size() < 2) {return search;}
		//what we want to insert
        Occurrence toinsert = occs.remove(occs.size()-1);
        //begging of the arraylist
		int low = 0;
		//end of the arraylist
		int high = occs.size() - 1;
		//middle of the arraylist
        int mid = (high + low) / 2;
        //special case when there's only one other thing in the arraylist
        if(occs.size() == 1){
            //add the beggening to search
            search.add(0);
            //add toinsert to front if greater
            if(toinsert.frequency > occs.get(occs.size() - 1).frequency){ occs.add(0, toinsert); }
            //else add it back behind
            else{ occs.add(toinsert); }
            //return yo search
            return search;
        }

        //terminate when low is less than or equal to high
        while(low <= high){
            //the mid always changes to half of the low plus high
            mid = (high + low) / 2;
            //we need to keep track of the mids
            search.add(mid);
            //if the frequencies are equal, we've got our spot
            if(occs.get(mid).frequency == toinsert.frequency){break;}
            //resize low
            else if(occs.get(mid).frequency > toinsert.frequency){low = mid+1;}
            //resize high
            else {high = mid-1;}
        }
        //special case for whn high dips below low
        if (high < low) occs.add(low, toinsert);
        //normal case
        else{occs.add(mid, toinsert);}
        //return the mids
		return search;
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. 
	 * 
	 * Note that a matching document will only appear once in the result. 
	 * 
	 * Ties in frequency values are broken in favor of the first keyword. 
	 * That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same 
	 * frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * See assignment description for examples
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, 
	 *         returns null or empty array list.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
        //matches arraylist
        ArrayList<String> matches = new ArrayList<>();
        //arralist of occurrences
        ArrayList<Occurrence> occ = new ArrayList<>();
        //check if the arraylist contains both
	    if(keywordsIndex.containsKey(kw1) && keywordsIndex.containsKey(kw2)) {
	    	//save the Occurrences in an array
	        ArrayList<Occurrence> kw1arr = keywordsIndex.get(kw1);
	        //save the other Occurrences as well
	        ArrayList<Occurrence> kw2arr = keywordsIndex.get(kw2);
	        //document of the first one
	        String doc1;
	        //document of the second one
	        String doc2;
	        //position of the first arraylist
	        int x = 0;
	        //position of the second arraylist
	        int y = 0;
            //frequency of kw1 words
	        int freq1;
	        //frequency of kw2 words
	        int freq2;
	        //occurrances of kw1 words
	        Occurrence occ1;
	        //occurrances of kw2 words
	        Occurrence occ2;
            //for each occurrence in kw2 traverse through them
            while(x < kw1arr.size() && y < kw2arr.size()){
                //get the occurrance
                occ1 = kw1arr.get(x);
                //get the second occurrance
                occ2 = kw2arr.get(y);
                //get the first doc
                doc1 = kw1arr.get(x).document;
                //get the second doc
                doc2 = kw2arr.get(y).document;
                //get the first frequency
                freq1 = kw1arr.get(x).frequency;
                //get the second frequency
                freq2 = kw2arr.get(y).frequency;
                //check if the two docs are equal
                if(doc1.equals(doc2)){
                    //then check the greater one and keep only that one
                    if(freq1 >= freq2){ occ.add(occ1); }
                    //keep the second if the first is less
                    else{occ.add(occ2);}
                    //rearrange the array in descending order
                    insertLastOccurrence(occ);
                    //increment both
                    x++; y++;
                }
                //if not equals then just add the occurrences
                else{
                    if(freq1 >= freq2) {
                        //add the first
                        occ.add(occ1);
                        //increment counters
                        x++;
                    }
                    else{
                        //add the second
                        occ.add(occ2);
                        //increment
                        y++;
                    }
                }
            }
            //it is possible that we stopped short of a particular arraylist
            if(x == kw1arr.size() && y < kw2arr.size()){
                //loop through the entries that were not added
                while(y < kw2arr.size()){
                    //pick up the occurrances
                    occ2 = kw2arr.get(y);
                    //add the occurance
                    occ.add(occ2);
                    //increment
                    y++;
                }
            }
            //do the same if the other array has not fully been entered
            else if(y == kw2arr.size() && x < kw1arr.size()){
                //loop through
                while(x < kw1arr.size()){
                    //pick up the occurrence
                    occ1 = kw1arr.get(x);
                    //add the occurrence
                    occ.add(occ1);
                    //increment
                    x++;
                }
            }
            //counter for the while loop
            int i = 0;
            //now loop through and fill the first 5 matches
            while(i < 5 && i < occ.size()) {
                //check if occ contains a duplicate
                if(!matches.contains(occ.get(i).document)) {
                    //add the documents in descending order
                    matches.add(occ.get(i).document);
                    //increment
                    i++;
                }
                //if matches already contains it
                else{ occ.remove(i);}
            }
        }
        //if one doesn't contain it
        else if(keywordsIndex.containsKey(kw1) && !keywordsIndex.containsKey(kw2)){
	        //set occ to kw1
	        occ = keywordsIndex.get(kw1);
	        //initialize the counter
            int i = 0;
            //now loop through and fill the first 5 matches
            while(i < 5 && i < occ.size()) {
                //check if occ contains a duplicate
                if(!matches.contains(occ.get(i).document)) {
                    //add the documents in descending order
                    matches.add(occ.get(i).document);
                    //increment
                    i++;
                }
                //if matches already contains it
                else{ occ.remove(i);}
            }
        }
        //if the other doesn't contain it
        else if(!keywordsIndex.containsKey(kw1) && keywordsIndex.containsKey(kw2)){
            //set occ to kw1
            occ = keywordsIndex.get(kw2);
            //initialize the counter
            int i = 0;
            //now loop through and fill the first 5 matches
            while(i < 5 && i < occ.size()) {
                //check if occ contains a duplicate
                if(!matches.contains(occ.get(i).document)) {
                    //add the documents in descending order
                    matches.add(occ.get(i).document);
                    //increment
                    i++;
                }
                //if matches already contains it
                else{ occ.remove(i);}
            }
        }
        //return the filled list
        return matches;
	}
}
