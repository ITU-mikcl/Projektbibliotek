package packages;
import java.util.Scanner;
import java.io.*;
public class FileReader {

    /**
     * Asks user of what theme and file user wants to run.
     * @return File with the theme and file user wants by running readFile method.
     */
    public static File run() {
        Scanner s = new Scanner(System.in);
        System.out.println("Indtast temanummer: ");
        int themeNumber = s.nextInt();
        System.out.println("Indtast filnummer: ");
        int fileNumber = s.nextInt();
        s.close();
        return readFile(themeNumber, fileNumber);
    }

    /**
     * Iterates through the files in the directory.
     * @return wanted file or null if no file is found.
     */
    public static File readFile(int themeNumber, int fileNumber) {
        File directory = new File("./data");
        File[] files = directory.listFiles();
        int i = 0;

        if (files.length < themeNumber) {
            throw new NumberFormatException("Directory must exist");
        }

        for (File file : files) {
            i++;
            if (i == themeNumber) {
                i = 0;
                for (File f : file.listFiles()) {
                    if (file.listFiles().length < fileNumber) {
                        throw new NumberFormatException("File must exist");
                    }

                    i++;
                    if (i == fileNumber) {
                        return f;
                    }
                }
            }
        }

        return null;
    }
}