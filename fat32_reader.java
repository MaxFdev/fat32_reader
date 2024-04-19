import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class fat32_reader {

    // ! program startup

    private static RandomAccessFile fs;

    public static void main(String[] args) {
        // check for one arg
        if (args.length != 1) {
            throw new IllegalArgumentException("Requires 1 arg, ie: java fat32_reader <fat32.img>");
        }

        // get the file
        File fat32 = new File(args[0]);

        // check that the file exists
        if (!fat32.exists()) {
            throw new IllegalArgumentException("Arg: <" + args[0] + "> not valid file name!");
        }

        // set up the file access
        try {
            // try to get read only access
            fs = new RandomAccessFile(fat32, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // get the file system specs
        saveSpecs();
    }

    // ! file system specifications

    private static int BPB_BytesPerSec;
    private static int BPB_SecPerClus;
    private static int BPB_RsvdSecCnt;
    private static int BPB_NumFATS;
    private static int BPB_FATSz32;

    private static void saveSpecs() {
        try {
            fs.seek(11);
            BPB_BytesPerSec = Short.reverseBytes(fs.readShort());
            BPB_SecPerClus = fs.read();
            fs.seek(14);
            BPB_RsvdSecCnt = Short.reverseBytes(fs.readShort());
            BPB_NumFATS = fs.read();
            fs.seek(36);
            BPB_FATSz32 = Integer.reverseBytes(fs.readInt());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // start scanning input
        scanInput();
    }

    // ! take commands input

    private static void scanInput() {}
}