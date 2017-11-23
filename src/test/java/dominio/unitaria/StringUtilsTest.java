package dominio.unitaria;

import static org.junit.Assert.*;

import org.junit.Test;

import utils.StringUtils;

public class StringUtilsTest {

	@Test
	public void isPalindromeTest() {
		String palindrome = "12121";
		assertTrue(StringUtils.isPalindrome(palindrome));
	}
	
	@Test
	public void isNotPalindromeTest() {
		String palindrome = "1222";
		assertFalse(StringUtils.isPalindrome(palindrome));
	}
	
	@Test
	public void getNumbersTest() {
		String str = "1A2B2C2+";
		assertTrue(StringUtils.getNumbersFromString(str).matches("\\d+"));
	}

}
