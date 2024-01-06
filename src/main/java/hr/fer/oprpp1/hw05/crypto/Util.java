package hr.fer.oprpp1.hw05.crypto;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Scanner;

/**
 * Pomoćni razred sa korisnim metodama.
 * 
 * @author Tomislav Bjelčić
 *
 */
public class Util {
	
	/**
	 * Zabrani stvaranje instanci objekata ovog razreda jer nema smisla.
	 */
	private Util() {}
	
	/**
	 * Pretvara predani niz znakova koje predstavljaju heksadekadske znamenke u polje okteta (bajtova), 
	 * gdje je svaki oktet predstavljen sa dvije heksadekadske znamenke.
	 * 
	 * 
	 * @param hex niz heksadekadskih znamenaka
	 * @return polje okteta koje su predstavljene sa nizom {@code hex}.
	 * @throws NullPointerException ako je predani niz {@code null}.
	 * @throws IllegalArgumentException ako predani niz ima neparan broj znakova ili ako 
	 * sadrži znakove koje ne predstavljaju heksadekadske znamenke.
	 */
	public static byte[] hextobyte(String hex) {
		Objects.requireNonNull(hex, "Predani heksadekadski niz je null.");
		
		checkHexString(hex);
		
		int count = hex.length() / 2;
		byte[] output = new byte[count];
		for (int i=0; i<count; i++) {
			int off = i*2;
			String byteStr = hex.substring(off, off + 2);
			int firstDigit = hexDigitValue(byteStr.charAt(0));
			int secondDigit = hexDigitValue(byteStr.charAt(1));
			int val = firstDigit * 16 + secondDigit;
			output[i] = (byte) val;
		}
		return output;
	}
	
	/**
	 * Pretvara predano polje okteta (bajtova) u znakovni niz heksadekadskih znamenaka.<br>
	 * 
	 * @param bytes polje okteta
	 * @return znakovni niz heksadekadskih znamenaka.
	 * @throws NullPointerException ako je predano polje okteta {@code null}.
	 */
	public static String bytetohex(byte[] bytes) {
		Objects.requireNonNull(bytes, "Predano polje okteta je null.");
		
		int len = bytes.length;
		StringBuilder sb = new StringBuilder(len * 2);
		
		int intMask = 0x00_00_00_ff;
		int leftMask = 0x00_00_00_f0;
		int rightMask = 0x00_00_00_0f;
		for (byte b : bytes) {
			int i = ((int) b) & intMask;
			int left = (i & leftMask) >>> 4;
			int right = (i & rightMask);
			sb.append(hexDigit(left))
				.append(hexDigit(right));
		}
		
		return sb.toString();
	}
	
	/**
	 * Provjerava je li heksadekadski niz znakova ispravan.<br>
	 * Niz je ispravan ako sadrži paran broj znakova i svi znakovi predstavljaju heksadekadske znamenke.
	 * 
	 * @param hex
	 */
	private static void checkHexString(String hex) {
		int len = hex.length();
		if ((len & 1) != 0)
			throw new IllegalArgumentException("Heksadekadski niz " + hex + " sadrži neparan broj znakova.");
		
		String regex = "(\\p{XDigit})*";
		boolean match = hex.matches(regex);
		if (!match)
			throw new IllegalArgumentException(
					"Heksadekadski niz " + hex + " se sastoji od znakova koje nisu ispravne heksadekadske znamenke.");
	}
	
	
	/**
	 * Čita jedan redak sa standardnog ulaza.
	 * 
	 * @return pročitani redak.
	 */
	@SuppressWarnings("resource")
	public static String readLineFromStandardInput() {
		return new Scanner(System.in).nextLine(); // da se System.in ne zatvori
	}
	
	/**
	 * Provjerava predstavlja li predani String {@code pathStr} putanju postojeće 
	 * datoteke.
	 * 
	 * @param pathStr putanja datoteke.
	 * @throws IllegalArgumentException ako objekt sa putanjom {@code pathStr} ne postoji 
	 * ili ne predstavlja datoteku.
	 */
	public static void checkIfValidFilePathString(String pathStr) {
		Objects.requireNonNull(pathStr);
		
		Path p = Paths.get(pathStr);
		Path pAbs = p.toAbsolutePath().normalize();

		boolean exists = Files.exists(p);
		if (!exists)
			throw new IllegalArgumentException("File with path " + pAbs + " doesn't exist.");

		boolean isFile = Files.isRegularFile(p);
		if (!isFile)
			throw new IllegalArgumentException("Path " + pAbs + " is not a file.");
	}
	
	/**
	 * Pretvara heksadekadsku znamenku {@code digit} u njenu vrijednost.
	 * 
	 * @param digit
	 * @return vrijednost znamenke {@code digit}.
	 */
	private static int hexDigitValue(char digit) {
		boolean isDigit = Character.isDigit(digit);
		if (isDigit)
			return (int) (digit - '0');
		char uppercase = Character.toUpperCase(digit);
		return (int) (uppercase - 'A' + 10);
	}
	
	/**
	 * Pretvara vrijednost {@code val} u znak koji predstavlja heksadekadsku znamenku.
	 * 
	 * @param val
	 * @return hex znamenka.
	 */
	private static char hexDigit(int val) {
		return val < 10 ? (char) ('0' + val) : (char) ('a' + val - 10);
	}
	
}
