package hr.fer.zemris.java.hw05.shell;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Pomoćni razred sa korisnim javnim statičkim metodama koje koriste naredbe 
 * ljuske prilikom izvršavanja.
 * 
 * @author Tomislav Bjelčić
 *
 */
public class Util {

	/**
	 * Onemogući stvaranje instanci objekata ovog razreda jer nema smisla.
	 */
	private Util() {}
	
	/**
	 * Vraća String koji se (možda) nalazi u navodnicima, uzimajući u obzir sve 
	 * escape sekvence koje se pojavljuju (ukoliko navodnici postoje).
	 * 
	 * @param str String sa navodnicima i escape sekvencama.
	 * @return preuređeni String bez navodnika i escape sekvenca.
	 * @throws NullPointerException ako je predani String {@code null}.
	 * @throws IllegalArgumentException ako predani String ima više od jednog para navodnika. 
	 * Ako je jedan par navodnika, iznimka će se izazvati ukoliko {@code str} ne započinje ili ne 
	 * završava navodnicima (nakon što se izbace rubne praznine).
	 */
	public static String parseString(String str) {
		Objects.requireNonNull(str, "Given path String is null.");
		
		char quote = '"';
		char escape = '\\';
		int idxFirst = str.indexOf(quote);
		boolean hasAnyQuotes = idxFirst != -1;
		if (!hasAnyQuotes)
			return str;
		
		String prefix = str.substring(0, idxFirst);
		String regex = "\\s*";
		boolean matchPrefix = prefix.matches(regex);
		if (!matchPrefix)
			throw new IllegalArgumentException("String " + str + " either has to start with quotes or not have any.");
		
		int idxLast = idxFirst;
		while (true) {
			int idxNextQuote = str.indexOf(quote, idxLast+1);
			if (idxNextQuote != -1) {
				idxLast = idxNextQuote;
				char prev = str.charAt(idxNextQuote - 1);
				if (prev != escape)
					break;
			} else break;
		}
		if (idxLast == idxFirst)
			throw new IllegalArgumentException("String " + str + " has an unclosed quote.");
		
		int strLen = str.length();
		String rest = str.substring(idxLast+1);
		boolean matchSuffix = rest.matches(regex);
		if (!matchSuffix)
			throw new IllegalArgumentException("String " + str + ": after first closed quotes there must not be a non whitespace character.");
		
		String noQuotes = str.substring(idxFirst+1, idxLast);
		String removeEscapes = removeEscapes(noQuotes);
		return removeEscapes;
	}

	/**
	 * Uklanja escape sekvence iz predanog Stringa.
	 * 
	 * @param str
	 * @return
	 */
	private static String removeEscapes(String str) {
		int len = str.length();
		StringBuilder sb = new StringBuilder(len);
		char escape = '\\';
		char quote = '"';

		for (int i=0; i<len; i++) {
			char ch = str.charAt(i);

			if (ch == escape) {
				int next = i+1;
				if (next < len) {
					char nextChar = str.charAt(next);
					if (nextChar == quote || nextChar == escape) {
						sb.append(nextChar);
						i++;
						continue;
					}
				}
			}

			sb.append(ch);
		}
		return sb.toString();
	}

	/**
	 * Razdvaja predani String po bjelinama, uzimajući u obzir navodnike i 
	 * escape sekvence.<br>
	 * To jest, ako su bjeline u navodnicima, do razdvajanja neće doći.
	 * 
	 * @param str String koji se želi razdvojiti po bjelinama.
	 * @return polje razdvojenih Stringova po bjelinama.
	 */
	public static String[] whitespaceSplit(String str) {
		Objects.requireNonNull(str, "Given String is null.");
		List<String> splitted = new LinkedList<>();
		char quote = '"';
		char escape = '\\';
		boolean quoteStarted = false;
		boolean escapeActive = false;
		int strLen = str.length();
		int from = -1;

		for (int i=0; i<strLen; i++) {
			char ch = str.charAt(i);

			if (ch == quote && !escapeActive)
				quoteStarted = !quoteStarted;

			if (Character.isWhitespace(ch)) {
				if (from == -1 || quoteStarted)
					continue;
				String substr = str.substring(from, i);
				from = -1;
				splitted.add(substr);
			} else {
				if (from == -1)
					from = i;
			}
			
			escapeActive = (ch == escape) ? !escapeActive : false;
			
		}

		if (!quoteStarted && from != -1) {
			String last = str.substring(from);
			splitted.add(last);
		}

		else if (from != -1) {
			String regex = "\\s+";
			String[] rest = str.substring(from).split(regex);
			for (String s : rest)
				splitted.add(s);
		}

		return splitted.toArray(String[]::new);
	}
	
	/**
	 * Provjerava je li predana putanja {@code path} predstavlja putanju postojećeg 
	 * direktorija u datotečnom sustavu.
	 * 
	 * @param path putanja direktorija.
	 * @return poruku neispravnosti ukoliko postoji, inače (ako je predana 
	 * putanja ispravna), vraća {@code null}.
	 * @throws NullPointerException ako je predana putanja {@code null}.
	 */
	public static String checkValidDirPath(Path path) {
		return checkValidPath(path, "directory");
	}
	
	/**
	 * Provjerava je li predana putanja {@code path} predstavlja putanju postojeće 
	 * datoteke u datotečnom sustavu.
	 * 
	 * @param path putanja datoteke.
	 * @return poruku neispravnosti ukoliko postoji, inače (ako je predana 
	 * putanja ispravna), vraća {@code null}.
	 * @throws NullPointerException ako je predana putanja {@code null}.
	 */
	public static String checkValidFilePath(Path path) {
		return checkValidPath(path, "file");
	}
	
	/**
	 * Provjerava je li predana putanja {@code path} predstavlja putanju postojećeg 
	 * objekta u datotečnom sustavu i je li on tipa {@code type}.
	 * 
	 * @param path putanja objekta.
	 * @param type tip objekta. Metoda razlikuje samo datoteke i direktorije.
	 * @return poruku neispravnosti ukoliko postoji, inače (ako je predana 
	 * putanja ispravna), vraća {@code null}.
	 * @throws NullPointerException ako je predana putanja {@code null}.
	 */
	private static String checkValidPath(Path path, String type) {
		Objects.requireNonNull(path, "Given path is null.");

		String pathStr = path.toString();
		String absPathStr = path.toAbsolutePath().normalize().toString();
		String err = null;
		boolean isType = type.equals("directory") ? Files.isDirectory(path)
								: Files.isRegularFile(path);
		if (!Files.exists(path))
			err = type + " path " + absPathStr + " does not exist.";
		else if (!isType)
			err = "path " + pathStr + " is not a " + type + ".";
		return err;
	}
	
	/**
	 * Vraća String heksadekadskih znamenaka koje predstavljaju 32-bitni cijeli broj 
	 * {@code n} (dvojni komplement), uzimajući u obzir samo najnižih
	 * {@code binaryDigitCount} bitova (bitova najmanje težine). 
	 * 
	 * @param n 32-bitni cijeli broj u formatu dvojnog komplementa.
	 * @param binaryDigitCount broj bitova najmanje težine koji se uzimaju u obzir.
	 * @return String heksadekadskih znamenaka. Broj znakova takvog Stringa je jednaka najmanjem 
	 * broju heksadekadskih znamenki potrebnih da bi se prikazalo {@code binaryDigitCount} bitova.
	 * @throws IllegalArgumentException ako je argument {@code binaryDigitCount} manji od 1 
	 * ili veći od 32.
	 */
	public static String intToHex(int n, int binaryDigitCount) {
		int maxDigitCount = Integer.SIZE;
		if (binaryDigitCount > maxDigitCount)
			throw new IllegalArgumentException("Primitive integers cannot have "
					+ "more than " + maxDigitCount + " binary digits");
		if (binaryDigitCount < 1)
			throw new IllegalArgumentException("Binary digit count cannot be less than 1.");
		
		int hexDigitCount = (binaryDigitCount-1) / 4 + 1;
		int digitMask = 0xf;
		int mask = -1 >>> (maxDigitCount - binaryDigitCount);
		int ncopy = n & mask;
		
		StringBuilder sb = new StringBuilder(hexDigitCount);
		for (int i=0; i<hexDigitCount; i++) {
			int d = ncopy & digitMask;
			char digit = hexDigit(d);
			sb.append(digit);
			ncopy >>>= 4;
		}
		return sb.reverse().toString();
	}
	
	/**
	 * Vraća String heksadekadskih znamenaka koje predstavljaju 8-bitni cijeli broj 
	 * {@code n} (dvojni komplement), uzimajući u obzir samo najnižih
	 * {@code binaryDigitCount} bitova (bitova najmanje težine). 
	 * 
	 * @param n 8-bitni cijeli broj u formatu dvojnog komplementa.
	 * @param binaryDigitCount broj bitova najmanje težine koji se uzimaju u obzir.
	 * @return String heksadekadskih znamenaka. Broj znakova takvog Stringa je jednaka najmanjem 
	 * broju heksadekadskih znamenki potrebnih da bi se prikazalo {@code binaryDigitCount} bitova.
	 * @throws IllegalArgumentException ako je argument {@code binaryDigitCount} manji od 1 
	 * ili veći od 8.
	 */
	public static String byteToHex(byte n, int binaryDigitCount) {
		int maxDigitCount = Byte.SIZE;
		if (binaryDigitCount > maxDigitCount)
			throw new IllegalArgumentException("Primitive bytes cannot have "
					+ "more than " + maxDigitCount + " binary digits");
		return intToHex((int) n, binaryDigitCount);
	}
	
	/**
	 * Pretvara vrijednost heksadekadske znamenke u znak koji predstavlja tu znamenku.
	 * 
	 * @param val
	 * @return
	 */
	private static char hexDigit(int val) {
		return val < 10 ? (char) ('0' + val) : (char) ('A' + val - 10);
	}
	
}
