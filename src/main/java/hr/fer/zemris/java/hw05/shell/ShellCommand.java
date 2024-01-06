package hr.fer.zemris.java.hw05.shell;

import java.util.List;

/**
 * Predstavlja naredbu ljuske.
 * 
 * @author Tomislav Bjelčić
 *
 */
public interface ShellCommand {
	
	/**
	 * Izvršava ovu naredbu.
	 * 
	 * @param env okruženje koje koristi ljuska u kojoj se izvršava ova naredba.
	 * @param arguments korisnikov unos nakon unosa imena naredbe.
	 * @return vrijednost tipa {@code ShellStatus} nakon što se naredba izvrši.
	 */
	ShellStatus executeCommand(Environment env, String arguments);
	
	/**
	 * Dohvaća ime ove naredbe.
	 * 
	 * @return ime naredbe.
	 */
	String getCommandName();
	
	/**
	 * Dohvaća opis naredbe, kao i upute za korištenje u obliku liste redaka.
	 * 
	 * @return lista redaka opisa ove naredbe.
	 */
	List<String> getCommandDescription();
	
}
