package hr.fer.zemris.java.hw05.shell.commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import hr.fer.zemris.java.hw05.shell.Environment;
import hr.fer.zemris.java.hw05.shell.ShellStatus;
import hr.fer.zemris.java.hw05.shell.Util;

/**
 * Implementacija ljuskine naredbe <b>cat</b>.
 * 
 * @author Tomislav Bjelčić
 *
 */
public class CatShellCommand extends AbstractShellCommand {
	
	{
		commandName = "cat";
		description = """
				Usage: cat <file_path> <character_encoding>
				
				Prints specified file to standard output, using specified character encoding.
				Character encoding parameter is optional. If not specified, default platform character encoding is used.
				To get all available character encodings, use command "charsets".""";
		initDescriptionLines();
	}
	
	private void cat(Environment env, Path file, Charset cs) throws IOException {
		try (BufferedReader br = Files.newBufferedReader(file, cs)) {
			char[] cbuf = new char[4096];
			while (true) {
				int read = br.read(cbuf);
				if (read == -1)
					break;
				String str = new String(cbuf, 0, read);
				env.write(str);
			}
		}
		env.writeln("");
	}
	
	@Override
	public ShellStatus executeCommand(Environment env, String arguments) {
		String[] args = Util.whitespaceSplit(arguments);
		int argCount = args.length;
		if (argCount < 1) {
			env.writeln(commandName + ": there has to be at least one argument.");
			return ShellStatus.CONTINUE;
		}
		if (argCount > 2) {
			env.writeln(commandName + ": too many arguments.");
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
		
		Charset cs = null;
		if (argCount == 2) {
			String charsetString = Util.parseString(args[1]);
			boolean supported = false;
			try {
				supported = Charset.isSupported(charsetString);
			} catch (IllegalCharsetNameException ex) {
				env.writeln(commandName + ": illegal charset name: " + charsetString);
				return ShellStatus.CONTINUE;
			}
			if (!supported) {
				env.writeln(commandName + ": unsupported charset: " + charsetString);
				return ShellStatus.CONTINUE;
			}
			cs = Charset.forName(charsetString);	
		} else
			cs = Charset.defaultCharset();
		
		try {
			cat(env, file, cs);
		} catch (IOException ex) {
			env.writeln("Error occurred while opening a file: " + ex.getMessage());
			return ShellStatus.CONTINUE;
		}
		return ShellStatus.CONTINUE;
	}
	
}
