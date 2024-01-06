# Custom Shell

Simple custom shell written in Java Homework assignment for course [Selected Topics in Software Development 1](https://www.fer.unizg.hr/predmet/oprpp1).

## Prerequisites

Make sure you have installed all of the following:
* Java - JDK >=17, works with with older versions but requires minor changes in [pom.xml](pom.xml). Make sure to set the `JAVA_HOME` environment variable pointing to your JDK installation directory and make sure to have `bin` directory added to `PATH` environment variable.
* Maven - set the `M2_HOME` environment variable pointing to your Maven installation. Add the `bin` directory to `PATH` environment variable.

## Build

After cloning the repository, simply execute the following command:
```shell
mvn compile
```

## Run

Run using Java:
```shell
java -cp target/classes hr.fer.zemris.java.hw05.shell.MyShell
```
## Usage

Get supported commands using `help`. Use the same command for usage instructions. For example:
```shell
> help ls
Usage: ls <directory_path>

Writes a directory listing in the following format:
<flags> <object_size> <creation_date/time> <object_name>

Column <flags> can contain the following:
        d : listed object is a directory
        r : listed object is readable
        w : listed object is writable
        x : listed object is executable
```

Example: list all objects in current directory:

```shell
> ls .
drwx       4096 2024-01-06 01:47:39 .git
-rwx     236822 2024-01-06 01:48:54 hw05.pdf
-rwx     177525 2024-01-06 01:48:54 hw05part2.pdf
-rwx        957 2024-01-06 01:49:04 pom.xml
-rwx        916 2024-01-06 01:47:44 README.md
drwx          0 2024-01-06 01:49:04 src
drwx       4096 2024-01-06 01:50:45 target
```
