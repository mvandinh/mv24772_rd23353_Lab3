/* WORD LADDER Main.java
 * EE422C Project 3 submission by
 * Minh Van-Dinh
 * mv24772
 * 16475
 * Raphael De Los Santos
 * rd23353
 * 16480
 * Slip days used: <0>
 * Git URL: https://github.com/mvandinh/mv24772_rd23353_Lab3
 * Fall 2016
 */


package assignment3;
import java.util.*;
import java.io.*;

public class Main {
	// static variables and constants only here.
	private static ArrayList<String> wordLadder = new ArrayList<String>();	// array list of strings that contains all words used to create the word ladder from first to last
	private static ArrayList<String> explored = new ArrayList<String>();	// contains all words that have already been compared
	private static Set<String> dictionary;	// dictionary that contains all of the words to be compared to
	private static String[] dict;	// array of lower case dictionary words that are used in the comparison
	private static String first;	// first word to be compared from
	private static String last;		// last word to be completely matched if a word ladder exists

	/**
	 * runs the entirety of the word ladder program
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Scanner kb;	// input Scanner for commands
		PrintStream ps;	// output file
		// If arguments are specified, read/write from/to files instead of Std IO.
		if (args.length != 0) {
			kb = new Scanner(new File(args[0]));
			ps = new PrintStream(new File(args[1]));
			System.setOut(ps);			// redirect output to ps
		} else {
			kb = new Scanner(System.in);// default from Stdin
			ps = System.out;			// default to Stdout
		}
		initialize();
		while(true) {
			parse(kb);
			if (wordLadder.isEmpty() != true) {
				getWordLadderDFS(first, last);
				printLadder(wordLadder);
			}
		}
	}
	/**
	 * initializes static variables and constants
	 */
	public static void initialize() {
		// initialize your static variables or constants here.
		// We will call this method before running our JUNIT tests.  So call it
		// only once at the start of main.
		dictionary = makeDictionary();
		dict = dictionary.toArray(new String[dictionary.size()]);
		for (int i = 0; i < dictionary.size(); i++) {
			dict[i] = dict[i].toLowerCase();
		}
		wordLadder.clear();
		explored.clear();
	}

	/**
	 * accepts input and correctly parses the two five-letter words
	 * @param keyboard Scanner connected to System.in
	 * @return ArrayList of 2 Strings containing start word and end word.
	 * If command is /quit, return empty ArrayList.
	 */
	public static ArrayList<String> parse(Scanner keyboard) {
		boolean flag = false;
		first = "";
		last = "";
		String input = keyboard.nextLine();
		input = input.replaceAll("\\s+","");	// removes all white space characters
		input = input.replaceAll("\\t+","");
		input = input.replaceAll("\\n+","");
		input = input.replaceAll("\\r+","");

		if (input.equals("/quit")) {
			System.exit(0);		// exit program if input is "/quit"
		}

		String[] input_string = input.split("");	// splits input into first and last word Strings
		for(int indx = 0; indx < input_string.length; indx ++){
			if(!flag){
				first = first + input_string[indx].toLowerCase();
				if(dictionary.contains(first) || dictionary.contains(first.toUpperCase())){
					flag = true;
				}
			} else{
				last = last + input_string[indx].toLowerCase();
			}
		}
		wordLadder.clear();
		wordLadder.add(first);	// add words to the word ladder to be used as the starting and ending points
		wordLadder.add(last);
		return wordLadder;
	}

	/**
	 * performs a DFS search in an attempt to find a word ladder between start and end
	 * @param start - origin word to be expanded from
	 * @param end - word to build a ladder to
	 * @return word ladder if it exists, and an empty list if it doesn't
	 */
	public static ArrayList<String> getWordLadderDFS(String start, String end) {
		wordLadder.clear();
		ArrayList<String> neighbors = findNeighbors(start);
		boolean found = findDFS(start, end, neighbors);		// call recursive function findDFS
		if(!found){
			wordLadder.clear();
			return wordLadder;	// return empty list if no word ladder exists
		}
		Collections.reverse(wordLadder);	// reverse word ladder to get proper order
		wordLadder.add(0, first);
		return wordLadder;
	}
	/**
	 * performs a BFS search in an attempt to find a word ladder between start and end
	 * @param start - origin word to be expanded from
	 * @param end - word to build a ladder to
	 * @return word ladder if it exists, and an empty list if it doesn't
	 */
    public static ArrayList<String> getWordLadderBFS(String start, String end) {
    	ArrayList<String> queue = new ArrayList<String>();		// BFS queue to be iterated through
		ArrayList<Node> parentNodes = new ArrayList<Node>();	// keeps track of parent-child relationships
		ArrayList<String> neighbors;	// neighbors are words with 4 letters in common
		String head = start;	// head is the word at the front of the queue
		queue.add(head);
		Node child = new Node();	// child is used in parentNodes to keep track of parents
		child.parent = null;
		child.word = head;
		parentNodes.add(child);
		while (!queue.isEmpty()) {
			head = queue.remove(0);
			if (head.equals(end)) {	// if the word matches the end word
				wordLadder.remove(wordLadder.size() - 1);
				for (int i = 0; i < parentNodes.size(); i++) {	// finds the correct node that contains head as the child
					if (parentNodes.get(i).word.equals(head)) {
						child = parentNodes.get(i);
						break;
					}
				}
				while (child.parent != null) {
					wordLadder.add(1, child.word);
					for (int i = 0; i < parentNodes.size(); i++) {
						if (parentNodes.get(i).word.equals(child.parent)) {	// finds all connected words and adds them into the word ladder
							child = parentNodes.get(i);
							break;
						}
					}
				}
				return wordLadder;
			}
			else if (explored.contains(head));	// if the word has already been compared
			else {
				explored.add(head);		// add the current head to the list of words that have already been compared
				parentNodes.remove(head);
				neighbors = findNeighbors(head);
				for (String k: neighbors) {			// iterate through all of the neighbors of the current head
					if (!explored.contains(k)) {	// if the word has not yet been compared, add it to the queue
						queue.add(k);
						Node nextChild = new Node();	// create a new node to be added to parentNodes for history keeping
						nextChild.parent = head;
						nextChild.word = k;
						parentNodes.add(nextChild);
					}
				}
				neighbors.clear();
			}
		}
		wordLadder.clear();	// return empty list if no word ladder exists
		return wordLadder;
	}

    /**
     * takes the dictionary and makes it a set to be used for comparing words (NOTE: all upper case)
     * @return Set of Strings for a dictionary
     */
	public static Set<String>  makeDictionary () {
		Set<String> words = new HashSet<String>();
		Scanner infile = null;
		try {
			infile = new Scanner (new File("five_letter_words.txt"));
		} catch (FileNotFoundException e) {
			System.out.println("Dictionary File not Found!");
			e.printStackTrace();
			System.exit(1);
		}
		while (infile.hasNext()) {
			words.add(infile.next().toUpperCase());
		}
		return words;
	}

	/**
	 * prints the appropriate message depending on the length of the word ladder and whether a word ladder exists
	 * @param ladder - the word ladder, empty if no word ladder exits
	 */
	public static void printLadder(ArrayList<String> ladder) {
		if (ladder.isEmpty()) {		// no word ladder exists
			System.out.println("no word ladder can be found between " + first + " and " + last);
		}
		else {	// word ladder exists
			System.out.println("A " + (ladder.size() - 2) + "-rung ladder exists between " + first + " and " + last);
			for (int i = 0; i < ladder.size(); i++) {
				System.out.println(ladder.get(i));
			}
		}
		explored.clear();

	}

	// Other private static methods here

	/**
	 * finds all neighbors of the current word (words that have 4 letters in common)
	 * @param node - word to be compared to
	 * @return array list of all neighbors
	 */
	private static ArrayList<String> findNeighbors(String node){
		ArrayList<String> neighbor = new ArrayList<String>();
		for(int i = 0; i < dictionary.size(); i ++){
			if(almostEquals(node, dict[i])){
				if (!explored.contains(dict[i])) {
					neighbor.add(dict[i]);		// adds word to list of neighbors
				}
			}
		}
		neighbor = reOrder(neighbor);
		return neighbor;
	}

	/**
	 * compares node and next to see if they have 4 letters in common
	 * @param node - word to be compared
	 * @param next - word in dictionary to compared to
	 * @return true if words have 4 letters in common
	 */
	private static boolean almostEquals(String node, String next) {
		if (node.length() != next.length())
			return false;
		int same = 0;
		for (int i = 0; i < node.length(); i++) {
			if (node.charAt(i) == next.charAt(i))	// compares each character and position of both words
				same++;
		}
		return (same == (node.length() - 1));
	}

	private static ArrayList<String> reOrder(ArrayList<String> neighbor){
		int[] same = new int[last.length() + 1];
		int equal;
		for(int i = 0; i < neighbor.size(); i++){
			equal = 0;
			for(int j = 0; j < last.length(); j++) {
				if (neighbor.get(i).charAt(j) == last.charAt(j)){
					equal++;
				}
			}
			same[equal] = same[equal]+ 1;
			if(equal == last.length()){
				neighbor.add(0, neighbor.get(i));
				neighbor.remove(i + 1);
			}
			else if(equal == last.length() - 1 && last.length() - 1 != 0){
				neighbor.add(same[last.length()], neighbor.get(i));
				neighbor.remove(i + 1);
			}
			else if(equal == last.length() - 2 && last.length() - 2 != 0){
				neighbor.add(same[last.length()] + same[last.length() - 1], neighbor.get(i));
				neighbor.remove(i + 1);
			}
			else if(equal == last.length() - 3 && last.length() - 3 != 0){
				neighbor.add(same[last.length()] + same[last.length() - 1] + same[last.length() - 2], neighbor.get(i));
				neighbor.remove(i + 1);
			}
			else if(equal == last.length() -4 && last.length() -4 != 0){
				neighbor.add(same[last.length()] + same[last.length() - 1] + same[last.length() - 2] + same[last.length() - 3], neighbor.get(i));
				neighbor.remove(i + 1);
			}
		}
		return neighbor;
	}

	/**
	 * recursive function that performs a DFS search
	 * @param node - current word to be compared
	 * @param toFind - word to be completely matched
	 * @param neighbors - list of all words that have 4 letters in common with node
	 * @return true if the word has been found
	 */
	private static boolean findDFS(String node, String toFind, ArrayList<String> neighbors){
		if(node.equals(null)) {
			return false;	// end of current branch
		}
		explored.add(node);
		if (node.equals(toFind)) {
			return true;	// toFind has been completely matched
		}
		else{
			for(String k : neighbors){
				if (explored.contains(k)) {	// skip words that have already been compared
					return false;
				}
				boolean found = findDFS(k, toFind, findNeighbors(k));
				if (found) {
					wordLadder.add(k);	// add word to word ladder if it is a connection
					return true;
				}
			}
		}
		return false;

	}
}
