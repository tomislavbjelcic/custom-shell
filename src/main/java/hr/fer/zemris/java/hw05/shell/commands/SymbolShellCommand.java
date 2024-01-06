package hr.fer.zemris.java.hw05.shell.commands;

import java.util.Set;

import hr.fer.zemris.java.hw05.shell.Environment;
import hr.fer.zemris.java.hw05.shell.ShellStatus;

/**
 * Implementacija ljuskine naredbe <b>symbol</b>.
 * 
 * @author Tomislav Bjelčić
 *
 */
public class SymbolShellCommand extends AbstractShellCommand {
	{
		commandName = "symbol";
		description = """
				Usage: symbol <symbol_alias> <new_symbol>
				
				Prints current symbol with specified alias, or changes it if argument 
				<new_symbol> is specified.
				
				All possible symbol aliases are:
				PROMPT
				MORELINES
				MULTILINE.""";
		initDescriptionLines();
	}
	
	private Set<String> symbolAliases = 
				Set.of("PROMPT", "MORELINES", "MULTILINE");
	
	private void writeSymbol(Environment env, String alias) {
		Character sym = getSymbol(env, alias);
		env.writeln("Symbol for " + alias + " is '" + sym + "'");
	}
	
	private Character getSymbol(Environment env, String alias) {
		Character sym = switch(alias) {
			case "PROMPT" -> env.getPromptSymbol();
			case "MORELINES" -> env.getMorelinesSymbol();
			case "MULTILINE" -> env.getMultilineSymbol();
			default -> null;
		};
		return sym;
	}
	
	private void changeSymbol(Environment env, String alias, Character oldSym, Character newSym) {
		switch(alias) {
			case "PROMPT" -> env.setPromptSymbol(newSym);
			case "MORELINES" -> env.setMorelinesSymbol(newSym);
			case "MULTILINE" -> env.setMultilineSymbol(newSym);
		}
		String wr = String.format("Symbol for %s changed from '%s' to '%s'",
				alias, oldSym, newSym);
		env.writeln(wr);
	}
	
	@Override
	public ShellStatus executeCommand(Environment env, String arguments) {
		if (arguments.isEmpty()) {
			env.writeln(commandName + ": there has to be at least one argument.");
			return ShellStatus.CONTINUE;
		}
		String regex = "\\s+";
		String[] splitted = arguments.split(regex);
		int len = splitted.length;
		if (len > 2) {
			env.writeln(commandName + ": too many arguments.");
			return ShellStatus.CONTINUE;
		}
		String alias = splitted[0];
		if (!symbolAliases.contains(alias)) {
			env.writeln(commandName + ": unknown symbol alias: " + alias);
			return ShellStatus.CONTINUE;
		}
		if (len == 1) {
			writeSymbol(env, alias);
		} else {
			String newSymStr = splitted[1];
			if (newSymStr.length() > 1) {
				env.writeln(commandName + ": second argument should be a single character symbol.");
				return ShellStatus.CONTINUE;
			}
			Character newSym = newSymStr.charAt(0); 
			Character oldSym = getSymbol(env, alias);
			changeSymbol(env, alias, oldSym, newSym);
		}
		
		return ShellStatus.CONTINUE;
	}

}
