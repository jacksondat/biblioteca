package utils;

public class StringUtils {
	public static boolean isPalindrome(String str){
		boolean isPalindrome = true;
		
		for(int i=0; i<str.length()/2; i++){
			if(str.charAt(i) != str.charAt(str.length()-1-i)){
				isPalindrome = false;
				break;
			}
		}
		
		return isPalindrome;
	}

	public static String getNumbersFromString(String str) {
		return str.replaceAll("[^\\d]", "");
	}
}
