import itumulator.executable.Program;
import itumulator.world.World;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import packages.FileReader;
import packages.Spawner;

public class Main {
    public static void main(String[] args){
        int size;
        int delay = 500;
        int displaySize = 800;
        ArrayList<String> fileValues = new ArrayList<>();

        /**
         * Calls the run method on the FileReaderClass, which handles the logic of files.
         * Fills the arraylist fileValues with files that need to be read
         * @throws FileNotFoundException if now file is to be found.
         */
        try {
            File myFile = FileReader.run();
            Scanner reader = new Scanner(myFile);

            System.out.println(myFile);

            while (reader.hasNextLine()) {
                fileValues.add(reader.nextLine());
            }

            reader.close();
        } catch (FileNotFoundException e) {
            System.out.print(e.getMessage());
        }

        /**
         * Sets the size of the world read from the arraylist
         * @throws IllegalArgumentException world is 0 or less.
         */
        size = Integer.parseInt(fileValues.get(0));
        if (size <= 0) {
            throw new IllegalArgumentException("World size can't be less than 1");
        }

        /**
         * Instantiating the world, showing and simulating it for 1000 steps.
         */
        Program p = new Program(size, displaySize, delay);
        World world = p.getWorld();
        Spawner spawner = new Spawner(world, p, size);
        spawner.spawnObject(fileValues);
        p.show();

        for (int i = 0; i < 1000; i++) {
            p.simulate();
        }
    }
}
