package hr.fer.zemris.java.hw05.shell.commands;

import java.util.List;
import java.util.stream.Collectors;

import hr.fer.zemris.java.hw05.shell.ShellCommand;

/**
 * Apstraktni razred koji nudi osnovne atribute i implementacije metoda sučelja 
 * {@link ShellCommand} koji modelira naredbu ljuske {@code MyShell}.
 * 
 * @author Tomislav Bjelčić
 *
 */
public abstract class AbstractShellCommand implements ShellCommand {
	
	/**
	 * Ime naredbe.
	 */
	protected String commandName;
	/**
	 * Opis naredbe.
	 */
	protected String description;
	/**
	 * Lista redaka opisa naredbe.
	 */
	protected List<String> descriptionLines;
	
	/**
	 * Metoda koja iz opisa naredbe {@code description} stvara listu redaka opisa naredbe, 
	 * odnosno inicijalizira {@code descriptionLines} kao nepromjenjivu listu.<br>
	 * Namijenjena je podrazredima koji će ju moći sigurno pozvati tek kada postave 
	 * atribut {@code description}.
	 */
	protected void initDescriptionLines() {
		descriptionLines = description.lines()
						.collect(Collectors.toUnmodifiableList());
	}
	
	@Override
	public String getCommandName() {
		return commandName;
	}
	
	@Override
	public List<String> getCommandDescription() {
		return descriptionLines;
	}
	
	
}
