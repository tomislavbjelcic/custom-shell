package hr.fer.zemris.java.hw05.shell;

import java.util.SortedMap;

/**
 * Predstavlja okolinu koju ljuska {@code MyShell} koristi za operacije poput dohvaćanja 
 * dostupnih naredbi, čitanje unosa i ispisa rezultata, itd.
 * 
 * @author Tomislav Bjelčić
 *
 */
public interface Environment {
	
	/**
	 * Čita redak unosa.
	 * 
	 * @return redak unosa.
	 * @throws ShellIOException ako se prilikom čitanja dogodi U/I pogreška.
	 */
	String readLine() throws ShellIOException;
	
	/**
	 * Ispisuje predani String na izlaz ove okoline.
	 * 
	 * @param text String koji se ispisuje.
	 * @throws ShellIOException ako prilikom pisanja na izlaz se dogodi U/I pogreška.
	 */
	void write(String text) throws ShellIOException;
	
	/**
	 * Ispisuje novu liniju sa predanim Stringom na izlaz ove okoline.
	 * 
	 * @param text String koji se ispisuje.
	 * @throws ShellIOException ako prilikom pisanja na izlaz se dogodi U/I pogreška.
	 */
	void writeln(String text) throws ShellIOException;
	
	/**
	 * Dohvaća preslikavanja (ime naredbe -> naredba) za sve podržane naredbe kao nepromjenjivu, sortiranu mapu.
	 * 
	 * @return mapa svih naredbi.
	 */
	SortedMap<String, ShellCommand> commands();
	
	/**
	 * Dohvaća znak koji se ispisuje na početku svakog retka ukoliko se unos naredbe proteže kroz više redaka.
	 * 
	 * @return
	 */
	Character getMultilineSymbol();
	
	/**
	 * Dohvaća znak koji se ispisuje na početku svakog retka ukoliko se unos naredbe proteže kroz više redaka na znak {@code symbol}.
	 * 
	 * @param symbol
	 */
	void setMultilineSymbol(Character symbol);
	
	/**
	 * Dohvaća znak koji se ispisuje prije unosa naredbe.
	 * 
	 * @return znak koji označava početak unosa naredbe.
	 */
	Character getPromptSymbol();
	
	/**
	 * Postavlja znak koji označava početak unosa naredbe na znak {@code symbol}.
	 * 
	 * @param symbol novi znak unosa naredbe.
	 */
	void setPromptSymbol(Character symbol);
	
	/**
	 * Dohvaća znak kojeg na kraju svakog retka unosa mora unijeti korisnik ako želi nastaviti pisati naredbu u sljedeći redak.
	 * 
	 * @return znak koji označava nastavak unosa u sljedeći redak.
	 */
	Character getMorelinesSymbol();
	
	/**
	 * Postavlja znak nastavka unosa u sljedeći redak na znak {@code symbol}.
	 * 
	 * @param symbol novi znak nastavka unosa u sljedeći redak.
	 */
	void setMorelinesSymbol(Character symbol);
	
}
