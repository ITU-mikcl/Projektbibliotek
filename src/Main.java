import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;

import packages.FileReader;
import packages.Spawner;

public class Main {
    public static void main(String[] args) {
        int size;
        int delay = 1000;
        int displaySize = 800;

        ArrayList<String> fileValues = new ArrayList<>();

        try {
            File myFile = FileReader.run();
            Scanner reader = new Scanner(myFile);
            System.out.println(myFile);
            while (reader.hasNextLine()) {
                fileValues.add(reader.nextLine());
            }
            reader.close();
        } catch (FileNotFoundException e) {}

        size = Integer.parseInt(fileValues.get(0));
        Program p = new Program(size, displaySize, delay);
        World world = p.getWorld();

        Spawner.spawnObject(fileValues, size, world,p);
        p.show();

        for (int i = 0; i < 200; i++) {
            p.simulate();
        }
    }
}
