package hr.fer.zemris.java.hw05.shell;

import java.util.Collections;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;

import hr.fer.zemris.java.hw05.shell.commands.CatShellCommand;
import hr.fer.zemris.java.hw05.shell.commands.CharsetsShellCommand;
import hr.fer.zemris.java.hw05.shell.commands.CopyShellCommand;
import hr.fer.zemris.java.hw05.shell.commands.ExitShellCommand;
import hr.fer.zemris.java.hw05.shell.commands.HelpShellCommand;
import hr.fer.zemris.java.hw05.shell.commands.HexDumpShellCommand;
import hr.fer.zemris.java.hw05.shell.commands.LsShellCommand;
import hr.fer.zemris.java.hw05.shell.commands.MkdirShellCommand;
import hr.fer.zemris.java.hw05.shell.commands.SymbolShellCommand;
import hr.fer.zemris.java.hw05.shell.commands.TreeShellCommand;

/**
 * Predstavlja ljusku sa okruženjem {@code Environment} koja je sposobna izvoditi skup određenih naredbi.
 * 
 * @author Tomislav Bjelčić
 *
 */
public class MyShell {
	
	/**
	 * Predstavlja implementaciju okruženja kojeg koristi ljuska {@code MyShell}.<br>
	 * Kao ulaz ovo okruženje koristi standardni ulaz sa tipkovnice, a kao izlaz koristi 
	 * standardni izlaz na konzolu.
	 * 
	 * @author Tomislav Bjelčić
	 *
	 */
	private static class ShellEnvironment implements Environment {
		
		/**
		 * Mapa svih podržanih naredbi.
		 */
		private SortedMap<String, ShellCommand> commands;
		/**
		 * Znak početka svakog unosa naredbe kroz više redaka.
		 */
		private Character multilineSymbol;
		/**
		 * Znak početka unosa naredbe.
		 */
		private Character promptSymbol;
		/**
		 * Znak nastavka unosa naredbe u sljedeći redak.
		 */
		private Character morelinesSymbol;
		
		public ShellEnvironment() {
			initCommands();
			initSymbols();
		}
		
		/**
		 * Popunjava mapu svih komandi i od nje stvara nepromjenjivu mapu.
		 */
		private void initCommands() {
			SortedMap<String, ShellCommand> m = new TreeMap<>();
			// dodati komande
			m.put("exit", new ExitShellCommand());
			m.put("symbol", new SymbolShellCommand());
			m.put("charsets", new CharsetsShellCommand());
			m.put("cat", new CatShellCommand());
			m.put("ls", new LsShellCommand());
			m.put("tree", new TreeShellCommand());
			m.put("hexdump", new HexDumpShellCommand());
			m.put("copy", new CopyShellCommand());
			m.put("mkdir", new MkdirShellCommand());
			m.put("help", new HelpShellCommand());
			commands = Collections.unmodifiableSortedMap(m);
		}
		
		/**
		 * Inicijalizira posebne znakove na pretpostavljene vrijednosti.
		 */
		private void initSymbols() {
			promptSymbol = '>';
			morelinesSymbol = '\\';
			multilineSymbol = '|';
		}
		
		@Override
		public String readLine() throws ShellIOException {
			try {
				@SuppressWarnings("resource")
				Scanner sc = new Scanner(System.in);
				return sc.nextLine();
			} catch (Exception e) {
				throw new ShellIOException();
			}
		}

		@Override
		public void write(String text) throws ShellIOException {
			try {
				System.out.print(text);
			} catch (Exception e) {
				throw new ShellIOException();
			}
		}

		@Override
		public void writeln(String text) throws ShellIOException {
			try {
				System.out.println(text);
			} catch (Exception e) {
				throw new ShellIOException();
			}
		}

		@Override
		public SortedMap<String, ShellCommand> commands() {
			return commands;
		}

		@Override
		public Character getMultilineSymbol() {
			return multilineSymbol;
		}

		@Override
		public void setMultilineSymbol(Character symbol) {
			multilineSymbol = symbol;
		}

		@Override
		public Character getPromptSymbol() {
			return promptSymbol;
		}

		@Override
		public void setPromptSymbol(Character symbol) {
			promptSymbol = symbol;
		}

		@Override
		public Character getMorelinesSymbol() {
			return morelinesSymbol;
		}

		@Override
		public void setMorelinesSymbol(Character symbol) {
			morelinesSymbol = symbol;
		}
		
	}
	
	/**
	 * Okruženje koje koristi ova ljuska.
	 */
	private Environment env = new ShellEnvironment();
	/**
	 * Poruka dobrodošlice koja se prikazuje prilikom pokretanja ljuske.
	 */
	private static final String GREETING_MESSAGE = "Welcome to MyShell v 1.0";
	
	/**
	 * Pokreće ljusku.
	 * 
	 * @throws ShellIOException ako se dogodi U/I pogreška okruženja {@code env}.
	 */
	public void run() {
		ShellStatus status = ShellStatus.CONTINUE;
		try {
			env.writeln(GREETING_MESSAGE);
		} catch (ShellIOException ex) {
			status = ShellStatus.TERMINATE;
		}
		
		while (status != ShellStatus.TERMINATE) {
			try {
				Character promptSym = env.getPromptSymbol();
				Character morelinesSym = env.getMorelinesSymbol();
				Character multilineSym = env.getMultilineSymbol();
				
				env.write(promptSym + " ");
				StringBuilder whole = new StringBuilder();
				while(true) {
					String line = env.readLine();
					if (line.isBlank())
						break;
					int len = line.length();
					char last = line.charAt(len - 1);
					if (last == morelinesSym.charValue()) {
						whole.append(line, 0, len-1);
						env.write(multilineSym + " ");
					} else {
						whole.append(line);
						break;
					}
				}
				String input = whole.toString().strip();
				int inputLen = input.length();
				if (input.isEmpty())
					continue;
				int firstWhitespaceIndex = 0;
				for (; firstWhitespaceIndex<inputLen; firstWhitespaceIndex++) {
					char ch = input.charAt(firstWhitespaceIndex);
					if (Character.isWhitespace(ch))
						break;
				}
				
				String cmdName = input.substring(0, firstWhitespaceIndex);
				String arguments = input.substring(firstWhitespaceIndex).strip();
				ShellCommand command = env.commands().get(cmdName);
				if (command == null) {
					env.writeln(cmdName + ": command not found.");
					continue;
				}
				status = command.executeCommand(env, arguments);
			} catch (ShellIOException ex) {
				status = ShellStatus.TERMINATE;
			}
		}
	}
	
	/**
	 * Glavni program koji samo stvara i pokreće ljusku.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		MyShell shell = new MyShell();
		shell.run();
	}
	
}
