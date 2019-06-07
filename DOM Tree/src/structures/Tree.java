package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {

	/**
	 * Root node
	 */
	TagNode root=null;

	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;
	/**
	 * Initializes this tree object with scanner for input HTML file
	 *
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}

	/**
	 * Builds the DOM tree from input HTML file, through scanner passed
	 * in to the constructor and stored in the sc field of this object.
	 *
	 * The root of the tree that is built is referenced by the root field of this object.
	 */
	public void build() {
		//Because we cannot return a TagNode from this method, we are forced to build a second method
		root = recursiveBuilder();
	}
	//This method builds the tree
	private TagNode recursiveBuilder(){
		//We want to stop as soon as the scanner is out of objects
		if(!sc.hasNext()) { return null; }
		//This TagNode will help traverse the list
		TagNode current;
		//temporarily stores the nextLine of the scanner
		String temp;
		//now we need to traverse through preTree and start entering stuff into our tree
		temp = sc.nextLine();
		if (temp.charAt(0) == '<') {
			//you want to exit the recursion once you reached a closing bracket
			if (temp.charAt(1) == '/') { return null; }
			//we have found a tag and under it lies everything else, if its not under, then its a sibling
			else { current = new TagNode(temp.substring(1, temp.length() - 1), recursiveBuilder(), recursiveBuilder()); }
		}
		//We have found a sentence/word which has siblings but no children
		else{ current = new TagNode(temp, null, recursiveBuilder()); }
		//we want to return the tree that has been built so far
		return current;
	}

	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 *
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {

		//we need to traverse the tree to find all matches of the old tag
		recursiveReplacer(oldTag, newTag, root);
	}
	//replaces a selected tag with a new tag
	private void recursiveReplacer(String oldT, String newT, TagNode root){
		//root has siblings
		while(root.sibling != null){
			//replacement
			if(root.tag.equals(oldT)){ root.tag = newT; }
			//if the root node has a child, go through that guy
			if(root.firstChild != null){ recursiveReplacer(oldT, newT, root.firstChild); }
			//traverse through the linked list
			root = root.sibling;
		}
		//now we look at the case where root has no siblings but does have children
		if(root.sibling == null){
			//replacement
			if(root.tag.equals(oldT)){ root.tag = newT; }
			//if the root node has a child, go through that guy
			if(root.firstChild != null){ recursiveReplacer(oldT, newT, root.firstChild); }
		}
		//now we look at the case where root has neither
		if(root.sibling == null && root.firstChild == null){ if(root.tag.equals(oldT)){ root.tag = newT; } }
	}

	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 *
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		//we need an additional arg so we use the recursive finder method
		recursiveFinder(row, root);
	}

	//recursively bolds everything in row r
	private void recursiveFinder(int r, TagNode root){
		//starting off with 1 because of instructions
		int i = 1;
		//Used to traverse the tds
		TagNode current;
		//root has siblings
		while(root.sibling != null){
			//adding the bold
			if(root.tag.equals("tr")) {
				//we found the correct row, now bold the stuff inside every column of the row
				if (i == r) {
					//Make current the first "td" then loop through all the others
					current = root.firstChild;
					//add the b tag under all td tags
					while (current != null) {
						//adds under the current td tag
						current.firstChild = new TagNode("b", current.firstChild, null);
						//increments current to the sibling
						current = current.sibling;
					} i++;
				}
				//we need to go to the next row which is its sibling
				else { i++; }
			}
			//if the root node has a child, go through that guy
			if(root.firstChild != null) { recursiveFinder(r, root.firstChild); }
			//traverse through the linked list
			root = root.sibling;
		}
		//now we look at the case where root has no siblings but does have children
		if(root.sibling == null){
			//adding the bold
			if (root.tag.equals("tr")) {
				//we found the correct row, now bold the stuff inside every column of the row
				if (i == r) {
					//Make current the first "td" then loop through all the others
					current = root.firstChild;
					//add the b tag under all td tags
					while (current != null) {
						//adds under the current td tag
						current.firstChild = new TagNode("b", current.firstChild, null);
						//increments current to the sibling
						current = current.sibling;
					}
				}
			}
			//if the root node has a child, go through that guy
			if (root.firstChild != null) { recursiveFinder(r, root.firstChild); }
		}
		//now we look at the case where root has neither
		if(root.sibling == null && root.firstChild == null){
			//adding the bold
			if(root.tag.equals("tr")) {
				//we found the correct row, now bold the stuff inside every column of the row
				if (i == r) {
					//Make current the first "td" then loop through all the others
					current = root.firstChild;
					//add the b tag under all td tags
					while (current != null) {
						//adds under the current td tag
						current.firstChild = new TagNode("b", current.firstChild, null);
						//increments current to the sibling
						current = current.sibling;
					}
				}
			}
		}
	}

	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and,
	 * in addition, all the li tags immediately under the removed tag are converted to p tags.
	 *
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {

		//the tag can be any one of the things listed above
		switch (tag){

			case "p":
				recursiveRemover1(tag, root);
			case "em":
				recursiveRemover1(tag, root);
			case "b":
				recursiveRemover1(tag, root);
			case "ol":
				recursiveRemover2(tag, root);
			case "ul":
				recursiveRemover2(tag, root);
		}
	}
	//this method is for simple cases
	private void recursiveRemover1(String tag, TagNode root){
		//variable to store the original siblings
		TagNode temp;
		//variable to go through to the end of the siblings and add original siblings
		TagNode current;
		//replacement
		if(root.tag.equals(tag)) {
			//first we need to store the siblings of the original root node
			temp = root.sibling;
			//next we need to overwrite the tag of the root node
			root.tag = root.firstChild.tag;
			//now we overwrite the siblings of the root node
			root.sibling = root.firstChild.sibling;
			//finally we overwrite its children as well
			root.firstChild = root.firstChild.firstChild;
			//we need to add back the original siblings
			current = root;
			//find the last sibling
			while(current.sibling != null){ current = current.sibling; }
			//add the original root siblings to the end of the new siblings
			current.sibling = temp;
		}
		//if the root node has a child, go through that guy
		if(root.firstChild != null){ recursiveRemover1(tag, root.firstChild); }
		//traverse through the linked list
		if(root.sibling != null){ recursiveRemover1(tag, root.sibling); }
	}
	//this method has the added functionality of removing li tags
	private void recursiveRemover2(String tag, TagNode root) {
		//variable to store the original siblings
		TagNode temp;
		//variable to go through to the end of the siblings and add original siblings
		TagNode current;
		//replacement
		if(root.tag.equals(tag)) {
			//first we need to store the siblings of the original root node
			temp = root.sibling;
			//next we need to overwrite the tag of the root node
			root.tag = root.firstChild.tag;
			//now we overwrite the siblings of the root node
			root.sibling = root.firstChild.sibling;
			//finally we overwrite its children as well
			root.firstChild = root.firstChild.firstChild;
			//we need to add back the original siblings
			current = root;
			//find the last sibling
			while(current.sibling != null){ current = current.sibling; }
			//add the original root siblings to the end of the new siblings
			current.sibling = temp;
			//replace all the li tags with p tags
			modifiedRecursiveReplacer("li", "p", root);
		}
		//if the root node has a child, go through that guy
		if(root.firstChild != null){ recursiveRemover2(tag, root.firstChild); }
		//traverse through the linked list
		if(root.sibling != null){ recursiveRemover2(tag, root.sibling); }
	}
	//this method replaces li tags with p tags
	private void modifiedRecursiveReplacer(String oldT, String newT, TagNode root){
		//root has siblings
		while(root.sibling != null){
			//replacement
			if(root.tag.equals(oldT)) { root.tag = newT; }
			//traverse through the linked list
			root = root.sibling;
		}
		//now we look at the case where root has no siblings but does have children
		if(root.sibling == null){ if(root.tag.equals(oldT)) { root.tag = newT; } }
	}

	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 *
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	public void addTag(String word, String tag) {

		recursiveAdder(word, tag, root, false);
	}
	//This method recursively calls itself until all instances of the given word have been tagged
	private void recursiveAdder(String word, String tag, TagNode root, boolean changed) {
		//check if the root node contains the word
		if(root.tag.toLowerCase().contains(word.toLowerCase())){
			//check if the root node exactly matches the given word
			if(root.tag.toLowerCase().equals(word.toLowerCase())){
				//make the root node the tag
				root.tag = tag;
				//make the root's child the og word
				root.firstChild = new TagNode(word, null, null);
			}
			//now check for the case that the word is contained within a sentence
			else { root = recursiveAdderHelper(tag, word, root); }
			//change changed
			changed = true;
		}

		//If the root node has a sibling, go through that as well
		if(root.sibling != null) {
			recursiveAdder(word, tag, root.sibling, changed);
			//check for unnecessary siblings
			if(root.sibling != null && !root.tag.equals(tag) && !root.sibling.tag.equals(tag) && changed){
				//merge the tags
				root.tag = root.tag + root.sibling.tag;
				//delete the duplicate
				root.sibling = root.sibling.sibling;
			}
		}
		//if the root node has a child, go through that guy and there hasn't been a change
		if (root.firstChild != null && !changed) { recursiveAdder(word, tag, root.firstChild, changed); }
	}
	//This method is given the sentence containing the word and returns the tagged tagnode
	private TagNode recursiveAdderHelper(String tag, String word, TagNode current){
		//just create a new TagNode which is fresh
		TagNode curr = current;
		//contains the words before the occurrence
		String before = curr.tag.substring(0, curr.tag.toLowerCase().indexOf(word.toLowerCase()));
		//contains the words after the occurrence
		String after = curr.tag.substring(curr.tag.toLowerCase().indexOf(word.toLowerCase()) + word.length());
		//contains the word without punctuation as found in the sentence
		String original = curr.tag.substring(curr.tag.toLowerCase().indexOf(word.toLowerCase()), curr.tag.toLowerCase().indexOf(word.toLowerCase()) + word.length());
		//check if the correct amount of space has been allotted
		if(before.length() == 0){
			//the word is at the beginning of the sentence
			if (after.charAt(0) == ' '){
				//curr should contain your desired tag
				curr.tag = tag;
				//now make the original word with punctuation the curr child
				curr.firstChild = new TagNode(original, null, null);
				//and now curr.sibling should be the after
				curr.sibling = new TagNode(after, null, null);
			} else if(after.charAt(0) == '.'||after.charAt(0) == ';'||after.charAt(0) == '?'||after.charAt(0) == '!'||after.charAt(0) == ','||after.charAt(0) == ';'){
				//There is no space after punctuation, it is at the end of a sentence
				if(after.length() == 1){
					//this meets the condition for having punctuation add to original string
					original += after.substring(0,1);
					//curr should contain your desired tag
					curr.tag = tag;
					//now make the original word with punctuation the curr child
					curr.firstChild = new TagNode(original, null, null);
				}
				//There is a space after the punctuation
				else if(after.charAt(1) == ' '){
					//this meets the condition for having punctuation add to original string
					original += after.substring(0,1);
					//get rid of punctuation in after
					after = after.substring(1);
					//curr.sibling should contain your desired tag
					curr.tag = tag;
					//now make the original word with punctuation the curr child
					curr.firstChild = new TagNode(original, null, null);
					//and now curr.sibling should be the after
					curr.sibling = new TagNode(after, null, null);
				}
			}
			//this means that there is an unapproved character at the start of after, but there is still a word afterwards
			else if(after.contains(word)){
				//gathers up the word and adds to before
				before = before + original + after.substring(0, after.indexOf(word));
				//gets rid of the excess up till the actual word
				after = after.substring(after.indexOf(word));
				//changes the tag of current with the useless stuff
				curr.tag = before;
				//makes the sibling the after
				curr.sibling = new TagNode(after, null, null);
			}
		}
		//There is a space before the word
		else if(before.charAt(before.length() - 1) == ' '){
			//the word is at the end of a sentence
			if(after.length() == 0){
				//this means that the word was spotted at the end of a sentence
				curr.tag = before;
				//curr.sibling should contain your desired tag
				curr.sibling = new TagNode(tag, null, null);
				//now make the original word with punctuation the curr child
				curr.sibling.firstChild = new TagNode(original, null, null);
			} else if (after.charAt(0) == ' '){
				//This means that the word has a space next to it
				curr.tag = before;
				//curr.sibling should contain your desired tag
				curr.sibling = new TagNode(tag, null, null);
				//now make the original word with punctuation the curr child
				curr.sibling.firstChild = new TagNode(original, null, null);
				//and now curr.sibling should be the after
				curr.sibling.sibling = new TagNode(after, null, null);
			} else if(after.charAt(0) == '.'||after.charAt(0) == ';'||after.charAt(0) == '?'||after.charAt(0) == '!'||after.charAt(0) == ','||after.charAt(0) == ';'){
				//check if end of sentence
				if(after.length() == 1){
					//this meets the condition for having punctuation add to original string
					original += after.substring(0,1);
					//replace your curr node with before
					curr.tag = before;
					//curr.sibling should contain your desired tag
					curr.sibling = new TagNode(tag, null, null);
					//now make the original word with punctuation the curr child
					curr.sibling.firstChild = new TagNode(original, null, null);
				}
				//if not end, checks for correct space
				else if(after.charAt(1) == ' '){
					//this meets the condition for having punctuation add to original string
					original += after.substring(0,1);
					//get rid of punctuation in after
					after = after.substring(1);
					//replace your curr node with before
					curr.tag = before;
					//curr.sibling should contain your desired tag
					curr.sibling = new TagNode(tag, null, null);
					//now make the original word with punctuation the curr child
					curr.sibling.firstChild = new TagNode(original, null, null);
					//and now curr.sibling should be the after
					curr.sibling.sibling = new TagNode(after, null, null);
				}
			}
			//this means that there is an unapproved character at the start of after, but there is still a word afterwards
			else if(after.contains(word)){
				//gathers up the word and adds to before
				before = before + original + after.substring(0, after.indexOf(word));
				//gets rid of the excess up till the actual word
				after = after.substring(after.indexOf(word));
				//changes the tag of current with the useless stuff
				curr.tag = before;
				//makes the sibling the after
				curr.sibling = new TagNode(after, null, null);
			}
		}
		//Returns the good TagNode
		return curr;
	}

	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	
	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}
	
	/**
	 * Prints the DOM tree. 
	 *
	 */
	public void print() {
		print(root, 1);
	}
	
	private void print(TagNode root, int level) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			for (int i=0; i < level-1; i++) {
				System.out.print("      ");
			};
			if (root != this.root) {
				System.out.print("|----");
			} else {
				System.out.print("     ");
			}
			System.out.println(ptr.tag);
			if (ptr.firstChild != null) {
				print(ptr.firstChild, level+1);
			}
		}
	}
}
