package hr.fer.zemris.java.hw05.shell.commands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import hr.fer.zemris.java.hw05.shell.Environment;
import hr.fer.zemris.java.hw05.shell.ShellStatus;
import hr.fer.zemris.java.hw05.shell.Util;

/**
 * Implementacija ljuskine naredbe <b>mkdir</b>.
 * 
 * @author Tomislav Bjelčić
 *
 */
public class MkdirShellCommand extends AbstractShellCommand {
	
	{
		commandName = "mkdir";
		description = """
				Usage: mkdir <directory_path>
				
				Creates the appropriate directory structure if it doesn't exist.""";
		initDescriptionLines();
	}
	
	@Override
	public ShellStatus executeCommand(Environment env, String arguments) {
		String[] args = Util.whitespaceSplit(arguments);
		if (args.length != 1) {
			env.writeln(commandName + ": there must be exactly one argument.");
			return ShellStatus.CONTINUE;
		}
		
		String dirStr = null;
		try {
			dirStr = Util.parseString(args[0]);
		} catch (IllegalArgumentException ex) {
			env.writeln(commandName + ": invalid input: " + ex.getMessage());
			return ShellStatus.CONTINUE;
		}
		Path dir = Paths.get(dirStr);
		boolean exists = Files.exists(dir);
		if (exists) {
			env.writeln(commandName + ": path " + dir + " already exists.");
			return ShellStatus.CONTINUE;
		}
		
		try {
			Files.createDirectories(dir);
		} catch (IOException e) {
			env.writeln("IO error occurred: " + e.getMessage());
			return ShellStatus.CONTINUE;
		}
		
		return ShellStatus.CONTINUE;
	}
	
}
