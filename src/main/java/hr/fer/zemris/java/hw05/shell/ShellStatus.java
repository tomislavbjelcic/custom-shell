package hr.fer.zemris.java.hw05.shell;

/**
 * Predstavlja povratnu vrijednost izvršenja neke naredbe ljuske {@code MyShell}.<br>
 * Ovisno o povratnoj vrijednosti, ljuska odlučuje treba li nastaviti ili zaustaviti 
 * sa radom.
 * 
 * @author Tomislav Bjelčić
 *
 */
public enum ShellStatus {
	/**
	 * Nastavi sa radom.
	 */
	CONTINUE,
	/**
	 * Zaustavi rad ljuske.
	 */
	TERMINATE
}
