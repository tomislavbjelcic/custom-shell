package hr.fer.zemris.java.hw05.shell.commands;

import hr.fer.zemris.java.hw05.shell.Environment;
import hr.fer.zemris.java.hw05.shell.ShellStatus;

/**
 * Implementacija ljuskine naredbe <b>exit</b>.
 * 
 * @author Tomislav Bjelčić
 *
 */
public class ExitShellCommand extends AbstractShellCommand {
	
	{
		commandName = "exit";
		description = """
				Usage: exit
				
				Exits (quits) the shell.""";
		initDescriptionLines();
	}
	
	@Override
	public ShellStatus executeCommand(Environment env, String arguments) {
		return ShellStatus.TERMINATE;
	}
	
}