import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            // seek and read data
            fs.seek(11);
            // TODO switch to custom byte reading method
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

    private static int BPB_RootClus;
    private static int dataRegOffset;
    private static long FATstart;
    private static long root;
    private static long currentLocationByte;
    private static String workingDirectory;

    private static void navigateRoot() {
        // try to seek and find the root cluster
        try {
            // seek and read data
            fs.seek(44);
            BPB_RootClus = Integer.reverseBytes(fs.readInt());

            // set the working directory to root
            workingDirectory = "/";

            // navigating to root cluster (set the current cluster)
            dataRegOffset = (BPB_RsvdSecCnt + BPB_NumFATS * BPB_FATSz32) * BPB_BytesPerSec;
            currentLocationByte = (BPB_RootClus - 2) * (BPB_SecPerClus * BPB_BytesPerSec)
                    + dataRegOffset;

            // set the FAT start
            FATstart = BPB_RsvdSecCnt * BPB_BytesPerSec;

            // set the root
            root = currentLocationByte;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ! take commands input

    private static void scanInput() {
        // set up the scanner
        Scanner scanner = new Scanner(System.in);
        boolean run = true;
        String input;

        while (run) {
            // print the working directory
            System.out.print(workingDirectory + "] ");
            input = scanner.nextLine();

            // set the input to uppercase
            input = input.toUpperCase();

            if (input.equals("STOP")) {
                run = false;
            } else if (input.equals("INFO")) {
                printInfo();
            } else if (input.equals("LS")) {
                listDirectory();
            } else if (input.startsWith("STAT ")) {
                // TODO does the substring work?
                getStats(input.substring(input.indexOf(" ") + 1));
            } else if (input.startsWith("SIZE ")) {
                getSize(input.substring(input.indexOf(" ") + 1));
            } else if (input.startsWith("CD ")) {
                changeDirectory(input.substring(input.indexOf(" ") + 1));
            } else if (input.startsWith("READ ")) {
                readFile(input.substring(input.indexOf(" ") + 1));
            } else if (!input.isBlank()) {
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
        // get the entries
        List<String> entries = getEntries();

        // print the entries
        for (int i = 0; i < entries.size(); i++) {
            // print the entry
            System.out.print(entries.get(i));

            // print a space if not the last entry
            if (i < entries.size() - 1) {
                System.out.print(" ");
            } else {
                System.out.println();
            }
        }
    }

    // ! get file or directory stats

    private static void getStats(String name) {
        // TODO get stats
    }

    // ! get file size

    private static void getSize(String name) {
        // check if the name is valid
        if (name.equals(".") || name.equals("..")) {
            System.out.println("Error: " + name + " is not a file");
            return;
        }

        // get all the entries in the current directory
        List<String> entries = getEntries();

        // check if the name is in the entries
        if (!entries.contains(name)) {
            System.out.println("Error: " + name + " is not a file");
            return;
        }

        // get the size of the file
        int size = getSizeOfFile(name);

        // print the size
        System.out.println("Size of " + name + " is " + size + " bytes");
    }

    // ! change directory

    private static void changeDirectory(String dir) {
        // check for current directory
        if (dir.equals(".")) {
            return;
        }

        // check for parent directory from root
        if (dir.equals("..") && currentLocationByte == root) {
            return;
        }

        // check if the directory is in the current directory
        List<String> entries = getEntries();
        if (!entries.contains(dir)) {
            System.out.println("Error: " + dir + " is not a directory");
            return;
        }

        // get the entry of the directory
        long entryOfDir = entryMap.get(dir);

        // check if the entry is a directory
        if (!isDir(entryOfDir)) {
            System.out.println("Error: " + dir + " is not a directory");
            return;
        }

        // get the first cluster of the directory
        long firstCluster = getFirstCluster(entryOfDir);

        // navigate to the cluster
        navigateToCluster(firstCluster);

        // set the working directory
        setWorkingDirectory(dir);
    }

    // ! read file

    private static void readFile(String args) {
        // TODO read file
    }

    // ! helper methods

    private static Map<String, Long> entryMap;

    private static List<String> getEntries() {
        // make a map for all the entry locations
        entryMap = new HashMap<String, Long>();

        // make a list for all the entries
        List<String> entries = new ArrayList<String>();

        // add the current and parent directory if in root
        if (currentLocationByte == root) {
            entries.add(".");
            entries.add("..");
        }

        // read the entries
        try {
            // find the entry limit based on cluster size
            int entryLimit = BPB_BytesPerSec * BPB_SecPerClus / 32;
            
            // keep track of the cluster being traversed
            long currentCluster = currentLocationByte;
            
            // keep track if there are more clusters
            boolean moreClusters = true;

            // check all the clusters
            while (moreClusters) {
                // get the starting byte
                fs.seek(currentCluster);
                int startByte = fs.read();
    
                // get the attribute byte
                fs.seek(currentCluster + 11);
                int attributeByte = fs.read();

                // keep track of the current entry being looked at
                int entry = 0;

                // check if the entry limit is reached or if there is a 0 byte
                while (startByte != 0 && entry < entryLimit) {
                    if (!(startByte == 65 && attributeByte == 15) && startByte != 229
                            && attributeByte != 8) {
                        // build the entry name
                        StringBuilder entryName = new StringBuilder();

                        // read the name
                        for (int i = 0; i < 8; i++) {
                            fs.seek(currentCluster + 32 * entry + i);
                            char nameChar = (char) fs.read();
                            if (isPrintableChar(nameChar) && nameChar != ' ') {
                                entryName.append(nameChar);
                            }
                        }

                        // read the extension
                        fs.seek(currentCluster + 32 * entry + 8);
                        char extensionChar = (char) fs.read();

                        // check if the extension is valid
                        if (isPrintableChar(extensionChar) && extensionChar != ' ') {
                            entryName.append(".");
                            entryName.append(extensionChar);

                            // finish the extension
                            for (int i = 1; i < 3; i++) {
                                extensionChar = (char) fs.read();
                                if (isPrintableChar(extensionChar) && extensionChar != ' ') {
                                    entryName.append(extensionChar);
                                }
                            }
                        }

                        // add the entry to the map
                        entryMap.put(entryName.toString(), currentCluster + 32 * entry);

                        // add the entry to the list
                        entries.add(entryName.toString());
                    }

                    // increment the entry
                    entry++;

                    // get the next starting byte
                    fs.seek(currentCluster + 32 * entry);
                    startByte = fs.read();

                    // get the next attribute byte
                    fs.seek(currentCluster + 32 * entry + 11);
                    attributeByte = fs.read();
                }

                // check if there are more clusters
                long FATEntry = checkFATForNext(currentCluster);

                if (FATEntry >= 0x0FFFFFF8) {
                    moreClusters = false;
                } else {
                    // get the next cluster byte
                    currentCluster = getByteAddressOfNextCluster(FATEntry);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // sort the entries for printing
        Collections.sort(entries);

        return entries;
    }

    private static long checkFATForNext(long byteLocation) {
        // get the cluster number
        long clusterNumber = getClusterNumber(byteLocation);

        // get the FAT entry
        long FATEntry = 0;

        try {
            // get the FAT entry
            fs.seek(FATstart + clusterNumber * 4);
            FATEntry = Integer.reverseBytes(fs.readInt());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return FATEntry;
    }

    private static long getByteAddressOfNextCluster(long FATEntry) {
        // convert the FAT entry to a byte address
        long nextClusterByte = (FATEntry - 2) * (BPB_SecPerClus * BPB_BytesPerSec) + dataRegOffset;

        return nextClusterByte;
    }

    private static long getClusterNumber(long byteLocation) {
        // get the cluster number
        return ((byteLocation - dataRegOffset) / (BPB_SecPerClus * BPB_BytesPerSec) + 2);
    }

    private static boolean isPrintableChar(char c) {
        return c >= 32 && c < 127;
    }

    private static int getSizeOfFile(String name) {
        // track size
        int size = 0;

        // get the size of the file
        try {
            // get the location of the entry size
            fs.seek(entryMap.get(name) + 28);
            size = Integer.reverseBytes(fs.readInt());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return size;
    }

    private static boolean isDir(long entry) {
        // check if the entry is a directory
        boolean isDir = false;

        try {
            fs.seek(entry + 11);
            int attribute = fs.read();

            if ((attribute & 0x10) == 0x10) {
                isDir = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return isDir;
    }

    private static long getFirstCluster(long entry) {
        // build the first cluster
        long firstCluster = 0;

        try {
            // read first cluster high bytes
            fs.seek(entry + 20);
            int h1 = fs.read();
            int h2 = fs.read();

            int high = (h2 << 8) | h1;

            // read first cluster low bytes
            fs.seek(entry + 26);
            int l1 = fs.read();
            int l2 = fs.read();

            int low = (l2 << 8) | l1;

            // build the first cluster
            firstCluster = (high << 16) | low;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return firstCluster;
    }

    private static void navigateToCluster(long cluster) {
        // check if the cluster is root
        if (cluster == 0) {
            currentLocationByte = root;
        } else {
            // set the current location byte
            currentLocationByte = (cluster - 2) * (BPB_SecPerClus * BPB_BytesPerSec)
                    + dataRegOffset;
        }
    }

    private static void setWorkingDirectory(String dir) {
        // check if the directory is a parent
        if (dir.equals("..")) {
            // remove the last directory
            workingDirectory = workingDirectory.substring(0, workingDirectory.lastIndexOf("/"));

            // check if the working directory is root
            if (workingDirectory.equals("")) {
                workingDirectory = "/";
            }
        } else {
            // check if the working directory is root
            if (workingDirectory.equals("/")) {
                workingDirectory += dir;
            } else {
                workingDirectory += "/" + dir;
            }
        }
    }
}