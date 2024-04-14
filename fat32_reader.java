import java.io.File;
import java.util.Scanner;

public class fat32_reader {

    // ! program startup

    private static File fat32;

    public static void main(String[] args) {
        // check for one arg
        if (args.length != 1) {
            throw new IllegalArgumentException("Requires 1 arg, ie: java fat32_reader <fat32.img>");
        }

        // get the file
        fat32 = new File(args[0]);

        // check that the file exists
        if (!fat32.exists()) {
            throw new IllegalArgumentException("Arg: <" + args[0] + "> not valid file name!");
        }

        // get the file system specs
        saveSpecs();

        // start scanning input
        scanInput();
    }

    // ! file system specifications

    private static byte[] a;

    private static void saveSpecs() {}

    private static void scanInput() {}
}