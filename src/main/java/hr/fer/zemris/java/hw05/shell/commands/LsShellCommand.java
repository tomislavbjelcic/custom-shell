package hr.fer.zemris.java.hw05.shell.commands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Stream;

import hr.fer.zemris.java.hw05.shell.Environment;
import hr.fer.zemris.java.hw05.shell.ShellStatus;
import hr.fer.zemris.java.hw05.shell.Util;

/**
 * Implementacija ljuskine naredbe <b>ls</b>.
 * 
 * @author Tomislav Bjelčić
 *
 */
public class LsShellCommand extends AbstractShellCommand {
	
	{
		commandName = "ls";
		description = """
				Usage: ls <directory_path>
				
				Writes a directory listing in the following format:
				<flags> <object_size> <creation_date/time> <object_name>
				
				Column <flags> can contain the following:
				\td : listed object is a directory
				\tr : listed object is readable
				\tw : listed object is writable
				\tx : listed object is executable""";
		initDescriptionLines();
	}
	
	private static class LsRecord {
		Character m = '-';
		boolean d; Character cd;
		boolean r; Character cr;
		boolean w; Character cw;
		boolean x; Character cx;
		long size;
		String creationTimeStr;
		String name;
		
		void chars() {
			cd = d ? 'd' : m;
			cr = r ? 'r' : m;
			cw = w ? 'w' : m;
			cx = x ? 'x' : m;
		}
		
		LsRecord(boolean d, boolean r, boolean w, boolean x,
				long size, String creationTimeStr, String name) {
			this.d = d;
			this.r = r;
			this.w = w;
			this.x = x;
			this.size = size;
			this.creationTimeStr = creationTimeStr;
			this.name = name;
			chars();
		}
		
		@Override
		public String toString() {
			String drwx = String.format("%s%s%s%s", cd, cr, cw, cx);
			String sizeStr = String.format("%10d", size);
			String ret = String.format("%s %s %s %s",
					drwx, sizeStr, creationTimeStr, name);
			return ret;
		}
	}
	
	private void ls(Environment env, Path dir) throws IOException {
		try (Stream<Path> stream = Files.list(dir)) {
			stream.map(path -> {
				BasicFileAttributeView faView = Files.getFileAttributeView(
						path, BasicFileAttributeView.class, LinkOption.NOFOLLOW_LINKS
						);
				BasicFileAttributes attributes = null;
				try {
					attributes = faView.readAttributes();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				boolean d = attributes.isDirectory();
				boolean r = Files.isReadable(path);
				boolean w = Files.isWritable(path);
				boolean x = Files.isExecutable(path);
				long size = attributes.size();
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				FileTime fileTime = attributes.creationTime();
				String formattedDateTime = sdf.format(new Date(fileTime.toMillis()));
				String name = path.getFileName().toString();
				LsRecord record = new LsRecord(d, r, w, x, size,
						formattedDateTime, name);
				return record.toString();
			}).forEach(env::writeln);
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
		
		try {
			ls(env, dir);
		} catch (IOException e) {
			env.writeln("IO error occurred: " + e.getMessage());
			return ShellStatus.CONTINUE;
		}
		return ShellStatus.CONTINUE;
	}
	
}
