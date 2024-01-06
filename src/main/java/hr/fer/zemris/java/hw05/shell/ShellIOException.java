package hr.fer.zemris.java.hw05.shell;

/**
 * Predstavlja U/I pogrešku ljuske {@code MyShell} koja se izaziva ukoliko dođe do 
 * U/I pogreške njenog okruženja {@code Environment}.
 * 
 * @author Tomislav Bjelčić
 *
 */
public class ShellIOException extends RuntimeException {
	
	/**
	 * Stvara novu pogrešku ljuske bez opisa.
	 */
	public ShellIOException() {
		super();
	}
	
}
