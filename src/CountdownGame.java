//
// Kenneth Astudillo
// 2023
//

// Imports all the necessary classes in the .io, util packages for the project
import java.io.*;
import java.util.*;

public class CountdownGame {
	// Private because we need to encapsulate data and it can only be accessed and modified through class methods
	private List<String> dictionary = new ArrayList<>(); // Stores the dictionary of valid words
	private int playerOneScore = 0; // Sets player 1 score to 0
	private int playerTwoScore = 0; // Sets player 2 score to 0
	private Scanner scanner = new Scanner(System.in); // Define a single Scanner for input
	
	// Initialize the game by reading words from words.txt file
	public CountdownGame() {
		readWords("words.txt"); 
	}
	
	// Reads words from the "words.txt" file and populates the dictionary
	public void readWords(String fileName) { 
		try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
			String line;
			while ((line = reader.readLine()) != null) {
				dictionary.add(line.toUpperCase());
			}
		} catch (IOException e) { // Prints any errors with the file
			e.printStackTrace();
		}
	}
	
	// Generates a list of random letters based on the specified vowel count

	public List<Character> generateLetters(int vowelCount) {
		Random rand = new Random();
		List<Character> letters = new ArrayList<>();

		String consonants = "BCDFGHJKLMNPQRSTVWXYZ"; // Sets the consonants to String
		String vowels = "AEIOU"; // Sets the vowels to String

		// Add vowels
		for (int i = 0; i < vowelCount; i++) {
			letters.add(vowels.charAt(rand.nextInt(vowels.length()))); // Generate random vowels
		}

		// Add consonants
		for (int i = 0; i < 9 - vowelCount; i++) {
			letters.add(consonants.charAt(rand.nextInt(consonants.length()))); // Generate random consonants
		}

		// Shuffle the letters to randomize them
		for (int i = letters.size() - 1; i > 0; i--) {
			int j = rand.nextInt(i + 1);
			char temp = letters.get(i);
			letters.set(i, letters.get(j));
			letters.set(j, temp);
		}

		return letters;
	}
	
	// Validates if a given word is both in the dictionary and can be constructed from the provided letters

	public boolean isValidWord(String word, List<Character> letters) {
		word = word.toUpperCase(); // Convert the word toUpperCase to match the dictionary

		// Check if the word exists in the dictionary
		if (!dictionary.contains(word)) {
			return false;
		}

		List<Character> letterCopy = new ArrayList<>(letters);

		// Check if the word can be formed from the given letters
		for (char letter : word.toCharArray()) {
			if (!letterCopy.contains(letter)) {
				return false;
			}
			letterCopy.remove(Character.valueOf(letter));
		}

		return true;
	}
	
	// Plays a single round of the game, including user input and word validation
	

	public void playRound(int round) {
		System.out.println("Round " + round); // Prints the round number
		
		// Prompt the user for the number of vowels (3-5) and ensure a valid input
		int vowelCount;
		do {
			// Scanner scanner = new Scanner(System.in);
			System.out.print("How many vowels would you like (3-5)? ");
			vowelCount = scanner.nextInt();

			if (vowelCount < 3 || vowelCount > 5) {
				System.out.print("Invalid selection! ");
			}
		} while (vowelCount < 3 || vowelCount > 5);

		List<Character> letters = generateLetters(vowelCount);

		// Convert the letters to a single string
		StringBuilder lettersAsString = new StringBuilder();
		for (char letter : letters) {
			lettersAsString.append(letter);
		}

		System.out.println("The letters are: " + lettersAsString.toString()); // Prints the random selected number of vowels/consonants

		String word1, word2;

		word1 = getPlayerWord(1, letters);
		word2 = getPlayerWord(2, letters);

		boolean isValidWord1 = isValidWord(word1, letters);
		boolean isValidWord2 = isValidWord(word2, letters);

		int score1 = calculateScore(word1);
		int score2 = calculateScore(word2);

		// Display messages about word validity before "best available words are " message
		if (!isValidWord1) {
			if (dictionary.contains(word1)) {
				System.out.println(word1 + " cannot be made with the letters!");
			} else {
				System.out.println(word1 + " is not a word!");
			}
		}

		if (!isValidWord2) {
			if (dictionary.contains(word2)) {
				System.out.println(word2 + " cannot be made with the letters!");
			} else {
				System.out.println(word2 + " is not a word!");
			}
		}

		List<String> playerWords = new ArrayList<>();

		// Check if both players' words are valid
		if (isValidWord1) {
			playerWords.add(word1);
		}

		if (isValidWord2) {
			playerWords.add(word2);
		}

		List<String> longestWords = findLongestAvailableWords(playerWords, letters);

		System.out.println("The best available words are " + String.join(" ", longestWords));

		playerOneScore += isValidWord1 ? score1 : 0;
		playerTwoScore += isValidWord2 ? score2 : 0;

		System.out.println("Player 1: " + playerOneScore);
		System.out.println("Player 2: " + playerTwoScore);

		// Check if both players' words are invalid and proceed to the next round
		if (!isValidWord1 && !isValidWord2) {
			System.out.println();
			return;
		}

		System.out.println();
	}
	
	// Finds and returns the longest valid words that can be constructed from the given letters
	

	public List<String> findLongestAvailableWords(List<String> playerWords, List<Character> letters) {
		List<String> longestWords = new ArrayList<>();

		int maxLength = 0; // Initialize the maximum length

		// Iterate through the dictionary and check if each word can be made using the given letters
		for (String word : dictionary) {
			if (canMakeWord(word, letters)) {
				int wordLength = word.length();
				if (wordLength > maxLength) {
					// If a longer word is found, clear the list and add the new word
					maxLength = wordLength;
					longestWords.clear();
					longestWords.add(word);
				} else if (wordLength == maxLength) {
					// If a word of the same length is found, add it to the list
					longestWords.add(word);
				}
			}
		}

		return longestWords;
	}
	
	// Checks if a given word can be constructed using the provided letters
	

	public boolean canMakeWord(String word, List<Character> letters) { 
		word = word.toUpperCase(); // This line converts the input word toUpperCase letters

		List<Character> letterCopy = new ArrayList<>(letters);

		for (char letter : word.toCharArray()) { // For loop iterates through each character in the word
			if (!letterCopy.contains(letter)) { // If the character is not found in letterCopy, it means that the character cannot be used to form the word
				return false; // Returns false, indicating that the given word cannot be constructed using the provided letters
			}
			letterCopy.remove(Character.valueOf(letter)); // Using the character to construct the word, ensuring that each character can be used only once
		}

		return true;
	}
	
	

	// Gets players 1/2 input word
	private String getPlayerWord(int playerNumber, List<Character> letters) { // Takes players 1/2 word from the letters
		// Scanner scanner = new Scanner(System.in); 
		System.out.print("Player " + playerNumber + ", what is your word? "); // Gets the word input from players 1/2
		String word = scanner.next().toUpperCase(); // Puts the word toUpperCase to match the dictionary
		return word; // Returns the word
	}
	
	// Calculate the score for a given word. Words with 9 letters receive a score of 18, while others receive a score based on their length
	

	public int calculateScore(String word) {
		if (word.length() == 9) {
			return 18;
		}
		return word.length(); 
	}
	
	// Game rules
	

	public void playGame() {
		for (int round = 1; round <= 10; round++) { // Starts on round 1 and ends on round 10
			playRound(round); // Play multiple rounds of the game
		}

		// Determine the winner or if it's a tie
		if (playerOneScore > playerTwoScore) {
			System.out.print("Player 1 wins! "); // If player 1 wins it prints this
		} else if (playerTwoScore > playerOneScore) {
			System.out.print("Player 2 wins! "); // if player 2 wins it prints this
		} else {
			System.out.print("It's a tie! "); // If it's a tie it prints this
		}
		
		scanner.close(); // Closes the scanner when game is finished 
	}
	
	// Starts the game
	

	public static void main(String[] args) {
		CountdownGame game = new CountdownGame(); 
		game.playGame(); 
		}
	}
