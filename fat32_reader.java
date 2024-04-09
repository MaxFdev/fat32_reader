import java.io.File;
import java.util.Scanner;

public class fat32_reader {

    private static boolean fileExists = true;

    public static void main(String[] args) {
        // check for one arg
        if (args.length != 1) {
            throw new IllegalArgumentException("Requires 1 arg, ie: java fat32_reader <fat32.img>");
        }

        // make a file reader
        fat32_reader fsReader = new fat32_reader(args[0]);

        // check that the file exists
        if (!fileExists) {
            throw new IllegalArgumentException("Arg: <" + args[0] + "> not valid file name!");
        }

        // start the file reader
        fsReader.start();
    }

    private final File fat32;

    private fat32_reader(String fileName) {
        // open the file
        this.fat32 = new File(fileName);

        // check that the file exists
        if (!this.fat32.exists()) {
            fat32_reader.fileExists = false; // TODO does this work?
        }

        // call to get the file system specifications
        scanSpecs();
    }

    private void scanSpecs() {}

    private void start() {}
}