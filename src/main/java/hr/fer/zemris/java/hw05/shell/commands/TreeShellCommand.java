package hr.fer.zemris.java.hw05.shell.commands;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import hr.fer.zemris.java.hw05.shell.Environment;
import hr.fer.zemris.java.hw05.shell.ShellStatus;
import hr.fer.zemris.java.hw05.shell.Util;

/**
 * Implementacija ljuskine naredbe <b>tree</b>.
 * 
 * @author Tomislav Bjelčić
 *
 */
public class TreeShellCommand extends AbstractShellCommand {
	
	{
		commandName = "tree";
		description = """
				Usage: tree <directory_path>
				
				Prints a directory tree starting from the specified directory as it's root.""";
		initDescriptionLines();
	}
	
	private static class MyVisitor extends SimpleFileVisitor<Path> {
		
		Environment env;
		int level = 0;
		static final String PREFIX = "  ";
		
		MyVisitor(Environment env) {
			this.env = env;
		}
		
		void line(Path path) {
			String name = path.getFileName().toString();
			String line = PREFIX.repeat(level).concat(name);
			env.writeln(line);
		}

		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
			line(dir);
			level++;
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			line(file);
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
			level--;
			return FileVisitResult.CONTINUE;
		}
		
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
		String err = Util.checkValidDirPath(dir);
		if (err != null) {
			env.writeln(commandName + ": invalid path: " + err);
			return ShellStatus.CONTINUE;
		}
		
		FileVisitor<Path> visitor = new MyVisitor(env);
		try {
			Files.walkFileTree(dir, visitor);
		} catch (IOException e) {
			env.writeln("IO error occurred: " + e.getMessage());
			return ShellStatus.CONTINUE;
		}
		return ShellStatus.CONTINUE;
	}
	
}
