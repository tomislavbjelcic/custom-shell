package hr.fer.zemris.java.hw05.shell.commands;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import hr.fer.zemris.java.hw05.shell.Environment;
import hr.fer.zemris.java.hw05.shell.ShellStatus;
import hr.fer.zemris.java.hw05.shell.Util;

/**
 * Implementacija ljuskine naredbe <b>copy</b>.
 * 
 * @author Tomislav Bjelčić
 *
 */
public class CopyShellCommand extends AbstractShellCommand {
	
	{
		commandName = "copy";
		description = """
				Usage: copy <source_file_path> <destination_path>
				
				Copies specified source file to given destination path.
				
				If <destination_path> denotes an existing file, 
				shell will ask the user if it is allowed to overwrite the existing file.
				If <destination_path> denotes an existing directory, 
				shell will copy specified source file inside specified directory. Created file will
				have the same name as specified source file.""";
		initDescriptionLines();
	}
	
	private void copy(Path src, Path dest) throws IOException {
		try (InputStream is = Files.newInputStream(src);
				OutputStream os = Files.newOutputStream(dest)) {
			byte[] buf = new byte[4096];
			while (true) {
				int read = is.read(buf);
				if (read == -1)
					break;
				os.write(buf, 0, read);
			}
		}
	}
	
	@Override
	public ShellStatus executeCommand(Environment env, String arguments) {
		String[] args = Util.whitespaceSplit(arguments);
		if (args.length != 2) {
			env.writeln(commandName + ": there must be exactly two arguments.");
			return ShellStatus.CONTINUE;
		}
		
		String srcFilePathStr = null;
		String destPathStr = null;
		try {
			srcFilePathStr = Util.parseString(args[0]);
			destPathStr = Util.parseString(args[1]);
		} catch (IllegalArgumentException ex) {
			env.writeln(commandName + ": invalid input: " + ex.getMessage());
			return ShellStatus.CONTINUE;
		}
		
		Path srcFile = Paths.get(srcFilePathStr);
		String errSrc = Util.checkValidFilePath(srcFile);
		if (errSrc != null) {
			env.writeln(commandName + ": invalid path: " + errSrc);
			return ShellStatus.CONTINUE;
		}
		Path destPath = Paths.get(destPathStr);
		String errDest = Util.checkValidDirPath(destPath);
		if (errDest == null) { // predan je ispravan direktorij
			Path srcFileName = srcFile.getFileName();
			destPath = destPath.resolve(srcFileName);
		}
		// inace je datoteka
		boolean exists = Files.exists(destPath);
		if (exists) {
			env.write(destPath + " file already exists. Overwrite it? [y/n] ");
			String confirm = env.readLine();
			boolean overwrite = !confirm.isEmpty() 
					&& Character.toUpperCase(confirm.charAt(0)) == 'Y';
			if (!overwrite)
				return ShellStatus.CONTINUE;
		}
		
		env.writeln("Copying file " + srcFile + " to " + destPath + " ...");
		try {
			copy(srcFile, destPath);
		} catch (IOException e) {
			env.writeln("IO error occurred: " + e.getMessage());
			return ShellStatus.CONTINUE;
		}
		env.writeln("Done.");
		
		return ShellStatus.CONTINUE;
	}
	
}
