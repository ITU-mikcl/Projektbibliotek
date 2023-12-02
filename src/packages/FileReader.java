package packages;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
public class FileReader {

    public static File run() {
        
        Scanner s = new Scanner(System.in);
        System.out.println("Indtast temanr: ");
        int themeNumber = s.nextInt();
        System.out.println("Indtast filnummer: ");
        int fileNumber = s.nextInt();
        s.close();
        return readFile(themeNumber, fileNumber);
    }

    public static File readFile(int themeNumber, int fileNumber) {
        File directory = new File("./data");
        File[] files = directory.listFiles();
        int i = 0;
        for (File file : files) {
            i++;
            if (i == themeNumber) {
                i = 0;
                for (File f : file.listFiles()) {
                    i++;
                    if (i == fileNumber) {
                        return f;
                    }
                }
            }
        } return null;
    }
}


