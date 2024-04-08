import java.util.Scanner;

public class fat32_reader {

    public static void main(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Requires 1 arg, ie: fat32_reader <fat32.img>");
        } else {
            fat32_reader fsReader = new fat32_reader(args[0]);
            fsReader.start();
        }
    }

    private final String fileName;

    private fat32_reader(String fileName) {
        this.fileName = fileName;

    }

    private void start() {}
}