package hr.fer.zemris.java.hw05.shell.commands;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import hr.fer.zemris.java.hw05.shell.Environment;
import hr.fer.zemris.java.hw05.shell.ShellStatus;
import hr.fer.zemris.java.hw05.shell.Util;

/**
 * Implementacija ljuskine naredbe <b>hexdump</b>.
 * 
 * @author Tomislav Bjelčić
 *
 */
public class HexDumpShellCommand extends AbstractShellCommand {
	
	{
		commandName = "hexdump";
		description = """
				Usage: hexdump <file_path>
				
				Prints specified file's byte content to standard output.
				
				Each row of produced output is in following format:
				<byte_offset> : <hex_bytes> | <character_interpretation> 
				
				Each row of produced output contains at most 16 bytes (<hex_bytes>).
				Each byte is represented as two hexadecimal digits, according to their 
				binary digit content. Most significant 4 bits are first hex digit, and least significant 4 bits are second hex digit.
				<character_interpretation> contains decoded bytes into characters.
				If byte value is out of range [32, 127], it is decoded as '.'""";
		initDescriptionLines();
	}
	
	/**
	 * Razred predstavlja svaki redak ispisa naredbe hexdump.
	 * 
	 * @author Tomislav Bjelčić
	 *
	 */
	private static class HexDumpUnit {
		static final int MAX_BYTE_COUNT = 0x10;
		int memOffset;
		byte[] bytes;
		
		HexDumpUnit(int memOffset, byte... bytes) {
			if (bytes.length > MAX_BYTE_COUNT)
				throw new IllegalArgumentException(); // nece se desiti
			this.memOffset = memOffset;
			this.bytes = bytes;
		}
		
		@Override
		public String toString() {
			String offString = Util.intToHex(memOffset, Integer.SIZE);
			int halfway = (MAX_BYTE_COUNT-1) / 2;
			char sep = '|';
			char sp = ' ';
			String doubleSp = "  ";
			StringBuilder sbHex = new StringBuilder();
			StringBuilder sbChars = new StringBuilder();
			Function<Byte, Character> mapper = b -> {
				int codepoint =
					((b >= (byte) 32) && (b <= (byte) 127)) ?
							(int) b :
							(int) '.';
				return (char) codepoint;
			};
			for (int i=0; i<MAX_BYTE_COUNT; i++) {
				String h = null;
				if (i < bytes.length) {
					byte b = bytes[i];
					h = Util.byteToHex(b, Byte.SIZE);
					char charMap = mapper.apply(b);
					sbChars.append(charMap);
				} else
					h = doubleSp;
				
				char separate = i == halfway ? sep : sp;
				sbHex.append(h).append(separate);
				
			}
			return offString + ": "
					+ sbHex.toString() + "| "
					+ sbChars.toString();
		}
	}
	
	/**
	 * Razred koji putem metoda update i doFinal proizvodi instance razreda 
	 * HexDumpUnit onim tempom kako mu pozivima stižu bajtovi.
	 * 
	 * @author Tomislav Bjelčić
	 *
	 */
	private static class HexDumpUnitFactory {
		
		
		int produced = 0;
		int startMemOffset;
		byte[] buffer = new byte[HexDumpUnit.MAX_BYTE_COUNT];
		int used = 0;
		
		int remaining() {
			return buffer.length - used;
		}
		
		boolean isFull() {
			return used == buffer.length;
		}
		
		
		HexDumpUnitFactory(int startMemOffset) {
			this.startMemOffset = startMemOffset;
		}
		
		List<HexDumpUnit> update(byte[] bytes, int off, int count) {
			int to = off + count;
			List<HexDumpUnit> l = new LinkedList<>();
			
			for (int i=off; i<to; i++) {
				buffer[used] = bytes[i];
				used++;
				if (isFull()) {
					int memOff = startMemOffset + buffer.length * produced;
					byte[] copy = Arrays.copyOf(buffer, used);
					HexDumpUnit unit = new HexDumpUnit(memOff, copy);
					
					produced++;
					used = 0;
					l.add(unit);
				}
			}
			return l;
		}
		
		List<HexDumpUnit> doFinal() {
			List<HexDumpUnit> l = new LinkedList<>();
			if (used == 0)
				return l;
			int memOff = startMemOffset + buffer.length * produced;
			produced++;
			byte[] copy = Arrays.copyOf(buffer, used);
			HexDumpUnit unit = new HexDumpUnit(memOff, copy);
			l.add(unit);
			return l;
		}
	}
	
	private void hexDump(Environment env, Path file) throws IOException {
		HexDumpUnitFactory factory = new HexDumpUnitFactory(0x0000000);
		try (InputStream is = Files.newInputStream(file)) {
			byte[] buf = new byte[4096];
			boolean continueRead = true;
			while (continueRead) {
				int read = is.read(buf);
				List<HexDumpUnit> units = null;
				if (read == -1) {
					continueRead = false;
					units = factory.doFinal();
				} else {
					units = factory.update(buf, 0, read);
				}
				units.forEach(unit -> env.writeln(unit.toString()));
			}
		}
	}
	
	@Override
	public ShellStatus executeCommand(Environment env, String arguments) {
		String[] args = Util.whitespaceSplit(arguments);
		if (args.length != 1) {
			env.writeln(commandName + ": there must be exactly one argument.");
			return ShellStatus.CONTINUE;
		}
		
		String filePathStr = null;
		try {
			filePathStr = Util.parseString(args[0]);
		} catch (IllegalArgumentException ex) {
			env.writeln(commandName + ": invalid input: " + ex.getMessage());
			return ShellStatus.CONTINUE;
		}
		Path file = Paths.get(filePathStr);
		String err = Util.checkValidFilePath(file);
		if (err != null) {
			env.writeln(commandName + ": invalid path: " + err);
			return ShellStatus.CONTINUE;
		}
		
		try {
			hexDump(env, file);
		} catch (IOException e) {
			env.writeln("Error occurred while opening a file: " + e.getMessage());
			return ShellStatus.CONTINUE;
		}
		return ShellStatus.CONTINUE;
	}
	
}
