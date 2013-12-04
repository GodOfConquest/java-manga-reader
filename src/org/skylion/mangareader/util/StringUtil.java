package org.skylion.mangareader.util;

/**
 * Utility class that houses commonly used methods for String parsing
 * @author Skylion
 *
 */
public class StringUtil {

	private StringUtil(){};//Prevents instantiation
	
	/**
	 * Checks if it contains a number
	 * @param input The String you want to check
	 * @return True if it does, false otherwise
	 */
	public static boolean containsNum(String input){
		for(char c: input.toCharArray()){
			if(Character.isDigit(c)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if the number is parsable into a int or double (contains only num)
	 * @param input
	 * @return
	 */
	public static boolean isNum(String input){
		for(char c: input.toCharArray()){
			if(!Character.isDigit(c) || c!='.'){//Special case for decimals
				return false;
			}
		}
		return true;
	}
	
	public static boolean isValidCharacter(char input){
		char[] validChar ="abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-._~:/?#[]@!$&'()*+,;=;"
				.toCharArray();
		for(char c: validChar){
			if(c == input){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Removes trailing white spaces from a String
	 * @param input The String you want to modify
	 * @return
	 */
	public static String removeTrailingWhiteSpaces(String input){
		while(input.length()>=1 && input.charAt(input.length()-1)==' '){//Removes trailing whitespaces
			input = input.substring(0,input.length()-1);
		}
		return input;
	}
	
	public static String removeTrailingPunctuation(String input){
		char c;
		while(!Character.isAlphabetic(c = input.charAt(input.length()-1)) && !Character.isDigit(c) 
				&& input.length()>=1){
			input = input.substring(0,input.length()-1);
		}
		return input;
	}
	
	public static String[] formatChapterNames(String[] input){
		for(int i = 0; i<input.length; i++){
			input[i] = formatChapterNames(input[i]);
		}
		return input;
	}
	
	public static String formatChapterNames(String input){
		input = removeTrailingWhiteSpaces(input);
		input = input.substring(input.lastIndexOf(' ')+1);
		return "Chapter: " + input;
	}
}
