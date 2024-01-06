package hr.fer.oprpp1.hw05.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Program koji može računati zaštitne sume, kriptirati i dekriptirati datoteke.
 * 
 * <p>Iz komandne linije se primaju argumenti: <br>
 * 1. komanda, koja može biti jedna od sljedećih: 
 * <b>checksha, encrypt, crypt</b><br>
 * 2. putanja do izvorne datoteke<br>
 * 3. ako se radi o kriptiranju ili dekriptiranju, odredišna putanja.
 * 
 * @author Tomislav Bjelčić
 *
 */
public class Crypto {

	/**
	 * Pretpostavljena veličina spremnika okteta koje se koriste pri čitanju i pisanju datoteka.
	 */
	private static final int BUFFER_SIZE = 4096;

	/**
	 * Računa zaštitnu sumu datoteke sa putanjom {@code file} algoritmom SHA-256.
	 * 
	 * @param file putanja do datoteke
	 * @return polje okteta koji predstavljaju 256-bitnu zaštitnu sumu.
	 */
	private static byte[] calculateDigest(Path file) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException ex) {}

		byte[] digest = null;
		try(InputStream is = Files.newInputStream(file)) {
			byte[] buf = new byte[BUFFER_SIZE];
			while (true) {
				int bytesRead = is.read(buf);
				if (bytesRead == -1)
					break;
				md.update(buf, 0, bytesRead);
			}
			digest = md.digest();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return digest;
	}

	/**
	 * Potprogram zadužen za interakciju s korisnikom ukoliko se radi o komandi <b>checksha</b>.
	 * 
	 * @param pathStr argument koji je unio korisnik i predstavlja putanju do datoteke čija zaštitna suma se računa.
	 */
	private static void checksha(String pathStr) {
		try {
			Util.checkIfValidFilePathString(pathStr);
		} catch (IllegalArgumentException ex) {
			System.out.println("Error: " + ex.getMessage());
			return;
		}
		Path p = Paths.get(pathStr);

		String fileName = p.getFileName().toString();
		System.out.print("Please provide expected sha-256 digest for " + fileName + ":\n> ");
		String input = Util.readLineFromStandardInput();
		byte[] expectedDigest = null;
		try {
			expectedDigest = Util.hextobyte(input);
		} catch (IllegalArgumentException ex) {
			System.out.println("Invalid input: " + ex.getMessage());
			return;
		}

		byte[] calculatedDigest = calculateDigest(p);
		System.out.print("Digesting completed. ");
		boolean match = Arrays.equals(expectedDigest, calculatedDigest);
		if (match) {
			System.out.println("Digest of " + fileName + " matches expected digest.");
		} else {
			String digestStr = Util.bytetohex(calculatedDigest);
			System.out.println("Digest of " + fileName + " does not match the expected digest.\n"
					+ "Digest was: " + digestStr);
		}
	}

	/**
	 * Potprogram zadužen za interakciju s korisnikom ukoliko se radi o komandama 
	 * <b>encrypt</b> ili <b>decrypt</b>, ovisno o argumentu {@code mode}.
	 * 
	 * @param srcStr korisnikov unos koji predstavlja putanju izvorne datoteke.
	 * @param destStr korisnikov unos koji predstavlja putanju odredišne datoteke.
	 * @param mode oznaka koja govori treba li obaviti kriptiranje ili dekriptiranje.
	 */
	private static void cipherOperation(String srcStr, String destStr, int mode) {
		try {
			Util.checkIfValidFilePathString(srcStr);
		} catch (IllegalArgumentException ex) {
			System.out.println("Error: " + ex.getMessage());
			return;
		}
		
		Path src = Paths.get(srcStr);
		Path dest = Paths.get(destStr);
		
		System.out.print("Please provide password as hex-encoded text (16 bytes, i.e. 32 hex-digits):\n> ");

		String password = Util.readLineFromStandardInput();
		int expectedLen = 32;
		if (password.length() != expectedLen) {
			System.out.println("Invalid input: provided password should be as long as 32 characters.");
			return;
		}
		byte[] pwBytes = null;
		try {
			pwBytes = Util.hextobyte(password);
		} catch (IllegalArgumentException ex) {
			System.out.println("Invalid input: " + ex.getMessage());
			return;
		}

		System.out.print("Please provide initialization vector as hex-encoded text (32 hex-digits):\n> ");

		String iv = Util.readLineFromStandardInput();
		if (iv.length() != expectedLen) {
			System.out.println("Invalid input: provided initialization vector should be as long as 32 characters.");
			return;
		}
		byte[] ivBytes = null;
		try {
			ivBytes = Util.hextobyte(password);
		} catch (IllegalArgumentException ex) {
			System.out.println("Invalid input: " + ex.getMessage());
			return;
		}

		SecretKeySpec keySpec = new SecretKeySpec(pwBytes, "AES");
		AlgorithmParameterSpec paramSpec = new IvParameterSpec(ivBytes);
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			cipher.init(mode, keySpec, paramSpec);
		} catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		byte[] inbuf = new byte[BUFFER_SIZE];
		byte[] outbuf = new byte[BUFFER_SIZE];
		try (InputStream in = Files.newInputStream(src);
				OutputStream os = Files.newOutputStream(dest)) {
			boolean continueReadWrite = true;
			while (continueReadWrite) {
				int bytesRead = in.read(inbuf);
				int bytesStored = -1;
				
				if (bytesRead == -1) {
					try {
						bytesStored = cipher.doFinal(outbuf, 0);
					} catch (IllegalBlockSizeException | ShortBufferException | BadPaddingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					continueReadWrite = false;
				} else {
					try {
						bytesStored = cipher.update(inbuf, 0, bytesRead, outbuf, 0);
					} catch (ShortBufferException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				os.write(outbuf, 0, bytesStored);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String srcFileName = src.getFileName().toString();
		String destFileName = dest.getFileName().toString();
		String op = mode == Cipher.ENCRYPT_MODE ? "Encryption" : "Decryption";
		System.out.println(op + " completed. "
				+ "Generated file " + destFileName + " based on file " + srcFileName + ".");
	}
	
	private static void encrypt(String src, String dest) {
		cipherOperation(src, dest, Cipher.ENCRYPT_MODE);
	}
	
	private static void decrypt(String src, String dest) {
		cipherOperation(src, dest, Cipher.DECRYPT_MODE);
	}

	/**
	 * Glavni program koji provjerava o kojoj se komandi radi, i poziva potprogram koji obavlja tu komandu.
	 * 
	 * @param args argumenti glavnog programa.
	 */
	public static void main(String[] args) {
		int argLen = args.length;
		if (argLen == 0) {
			System.out.println("Crypto is a command line argument program, but there are no arguments given.");
			return;
		}
		
		String operation = args[0];
		switch(operation) {
			case "checksha" -> {
				if (argLen != 2) {
					System.out.println("Operation " + operation + " expects only one additional argument.");
					return;
				}
				checksha(args[1]);
			}
			case "encrypt" -> {
				if (argLen != 3) {
					System.out.println("Operation " + operation + " expects two additional arguments.");
					return;
				}
				encrypt(args[1], args[2]);
			}
			case "decrypt" -> {
				if (argLen != 3) {
					System.out.println("Operation " + operation + " expects two additional arguments.");
					return;
				}
				decrypt(args[1], args[2]);
			}
			default -> System.out.println("Unknown operation " + operation);
		}
		

	}


}
