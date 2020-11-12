package library;

import java.io.File;

public class LibraryManager {

    public static void main(String[] args) {

        if (args.length != 1) {
            System.err.println("Usage: java Library <filename>");
            System.exit(1);
        }

        if(!new File(args[0]).exists()){
            System.err.println("File " + args[0] + " not found.");
            System.exit(1);
        }

        Library library = new Library(args[0]);

    }
}
