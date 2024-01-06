package hr.fer.oprpp1.hw05.crypto;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class UtilTest {
	
	@Test
	public void testHexToByteNullArgument() {
		assertThrows(NullPointerException.class,
				() -> Util.hextobyte(null));
	}
	
	@Test
	public void testHexToByteOddSizeString() {
		final String hex = "0fa34";
		assertThrows(IllegalArgumentException.class,
				() -> Util.hextobyte(hex));
	}
	
	@Test
	public void testHexToByteIllegalDigits() {
		final String hex = "0kA";
		assertThrows(IllegalArgumentException.class,
				() -> Util.hextobyte(hex));
	}
	
	@Test
	public void testHexToByteEmpty() {
		String hex = "";
		byte[] expected = {};
		byte[] actual = Util.hextobyte(hex);
		assertArrayEquals(expected, actual);
	}
	
	@Test
	public void testHexToByte() {
		String hex = "01Ae2Dff0a118b";
		byte[] expected = {1, -82, 45, -1, 10, 17, -117};
		byte[] actual = Util.hextobyte(hex);
		assertArrayEquals(expected, actual);
	}
	
	@Test
	public void testByteToHexNullArgument() {
		assertThrows(NullPointerException.class,
				() -> Util.bytetohex(null));
	}
	
	@Test
	public void testByteToHexEmptyArray() {
		byte[] bytes = {};
		String expected = "";
		String actual = Util.bytetohex(bytes);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testByteToHex() {
		final byte[] bytes = {1, -82, 45, -1, 10, 17, -117};
		String expected = "01ae2dff0a118b";
		String actual = Util.bytetohex(bytes);
		assertEquals(expected, actual);
	}
	
	
}
