package hr.fer.zemris.java.hw05.shell.commands;

import hr.fer.zemris.java.hw05.shell.Environment;
import hr.fer.zemris.java.hw05.shell.ShellCommand;
import hr.fer.zemris.java.hw05.shell.ShellStatus;
import hr.fer.zemris.java.hw05.shell.Util;

/**
 * Implementacija ljuskine naredbe <b>help</b>.
 * 
 * @author Tomislav Bjelčić
 *
 */
public class HelpShellCommand extends AbstractShellCommand {
	
	{
		commandName = "help";
		description = """
				Usage: help <command>
				
				Prints the description for specified command.""";
		initDescriptionLines();
	}
	
	private void listAllCommands(Environment env) {
		var map = env.commands();
		String prefix = "\t";
		map.forEach((k, v) -> env.writeln(prefix + k));
	}
	
	@Override
	public ShellStatus executeCommand(Environment env, String arguments) {
		String[] args = Util.whitespaceSplit(arguments);
		int argCount = args.length;
		if (argCount > 1) {
			env.writeln(commandName + ": too many arguments.");
			return ShellStatus.CONTINUE;
		}
		
		if (argCount == 0) {
			env.writeln("All supported commands:");
			listAllCommands(env);
		} else { // tocno 1 argument
			String input = args[0];
			ShellCommand cmd = env.commands().get(input);
			if (cmd == null) {
				env.writeln(commandName + ": command not found.");
				return ShellStatus.CONTINUE;
			}
			cmd.getCommandDescription().forEach(env::writeln);
		}
		return ShellStatus.CONTINUE;
	}
	
}
