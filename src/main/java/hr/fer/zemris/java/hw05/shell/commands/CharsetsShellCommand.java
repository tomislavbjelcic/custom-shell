package hr.fer.zemris.java.hw05.shell.commands;

import java.nio.charset.Charset;
import java.util.SortedMap;

import hr.fer.zemris.java.hw05.shell.Environment;
import hr.fer.zemris.java.hw05.shell.ShellStatus;

/**
 * Implementacija ljuskine naredbe <b>charsets</b>.
 * 
 * @author Tomislav Bjelčić
 * 
 */
public class CharsetsShellCommand extends AbstractShellCommand {
	
	{
		commandName = "charsets";
		description = """
				Usage: charsets
				
				Prints all available character encodings (charsets).""";
		initDescriptionLines();
	}
	
	@Override
	public ShellStatus executeCommand(Environment env, String arguments) {
		if (!arguments.isEmpty()) {
			env.writeln(commandName + ": there must not be any additional arguments.");
			return ShellStatus.CONTINUE;
		}
		
		SortedMap<String, Charset> charsetsMap = Charset.availableCharsets();
		env.writeln("List of all available charsets:");
		charsetsMap.forEach((name, charset) -> env.writeln("\t" + name));
		return ShellStatus.CONTINUE;
	}
	
}
