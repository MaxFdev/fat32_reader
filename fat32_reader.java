import java.io.File;
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

            // get the file system specs
            saveSpecs();

            // close the file system
            fs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ! file system specifications

    private static int BPB_BytesPerSec;
    private static int BPB_SecPerClus;
    private static int BPB_RsvdSecCnt;
    private static int BPB_NumFATS;
    private static int BPB_FATSz32;

    private static void saveSpecs() {
        // try to seek and find the fs specs
        try {
            fs.seek(11);
            BPB_BytesPerSec = Short.reverseBytes(fs.readShort());
            BPB_SecPerClus = fs.read();
            fs.seek(14);
            BPB_RsvdSecCnt = Short.reverseBytes(fs.readShort());
            BPB_NumFATS = fs.read();
            fs.seek(36);
            BPB_FATSz32 = Integer.reverseBytes(fs.readInt());

            // navigate to root cluster
            navigateRoot();

            // start scanning input
            scanInput();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ! navigate to root cluster

    private static String workingDirectory;
    private static long currentCluster;

    private static void navigateRoot() {
        // set the working directory to root
        workingDirectory = "/";

        // TODO finish navigating to root cluster (set the current cluster)
    }

    // ! take commands input

    private static void scanInput() {
        Scanner scanner = new Scanner(System.in);
        boolean run = true;
        String input;

        while (run) {
            System.out.print(workingDirectory + "] ");
            input = scanner.nextLine();

            if (input.equals("stop")) {
                run = false;
            } else if (input.equals("info")) {
                printInfo();
            } else if (input.equals("ls")) {
                listDirectory();
            } else if (input.startsWith("stat ")) {
                // TODO does the substring work?
                getStats(input.substring(input.indexOf(" ") + 1));
            } else if (input.startsWith("size ")) {
                getSize(input.substring(input.indexOf(" ") + 1));
            } else if (input.startsWith("cd ")) {
                changeDirectory(input.substring(input.indexOf(" ") + 1));
            } else if (input.startsWith("read ")) {
                readFile(input.substring(input.indexOf(" ") + 1));
            } else {
                // TODO what to print for errors
                System.out.println("Invalid command!");
            }
        }

        // close the scanner before exiting
        scanner.close();
    }

    // ! print info

    private static void printInfo() {
        System.out.println("BPB_BytesPerSec is 0x" + Integer.toHexString(BPB_BytesPerSec) + ", "
                + BPB_BytesPerSec);
        System.out.println("BPB_SecPerClus is 0x" + Integer.toHexString(BPB_SecPerClus) + ", "
                + BPB_SecPerClus);
        System.out.println("BPB_RsvdSecCnt is 0x" + Integer.toHexString(BPB_RsvdSecCnt) + ", "
                + BPB_RsvdSecCnt);
        System.out.println(
                "BPB_NumFATS is 0x" + Integer.toHexString(BPB_NumFATS) + ", " + BPB_NumFATS);
        System.out.println(
                "BPB_FATSz32 is 0x" + Integer.toHexString(BPB_FATSz32) + ", " + BPB_FATSz32);
    }

    // ! list directory

    private static void listDirectory() {
        // TODO list directory
    }

    // ! get file or directory stats

    private static void getStats(String name) {
        // TODO get stats
    }

    // ! get file size

    private static void getSize(String name) {
        // TODO get size
    }

    // ! change directory

    private static void changeDirectory(String name) {
        // TODO change directory
    }

    // ! read file

    private static void readFile(String name) {
        // TODO read file
    }
}