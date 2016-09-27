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
	
	public static ArrayList<String> wordLadder = new ArrayList<String>();
	public static String start;
	public static String end;
	
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
		do {
			parse(kb);
			if (wordLadder.isEmpty() != true) {
				printLadder(wordLadder);
			}
		} while (wordLadder.isEmpty() != true);
	}
	
	public static void initialize() {
		Set<String> dictionary = makeDictionary();
		
	}
	
	/**
	 * @param keyboard Scanner connected to System.in
	 * @return ArrayList of 2 Strings containing start word and end word. 
	 * If command is /quit, return empty ArrayList. 
	 */
	public static ArrayList<String> parse(Scanner keyboard) {
		System.out.print("Please enter two 5-letter words separated by a space for the word ladder: ");
		String input = keyboard.nextLine();
		System.out.println();
		if (input.equals("/quit")) {
			wordLadder.clear();
			return wordLadder;
		}
		else {
			wordLadder.clear();
			wordLadder.add(input.substring(0, 5));
			wordLadder.add(input.substring(input.length() - 5));
			return wordLadder;
		}
	}
	
	public static ArrayList<String> getWordLadderDFS(String start, String end) {
		
	}
	
    public static ArrayList<String> getWordLadderBFS(String start, String end) {
	
	}
    
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
	
	public static void printLadder(ArrayList<String> ladder) {
		for (int i = 0; i < ladder.size(); i++) {
			System.out.println(ladder.get(i));
		}
	}
}
