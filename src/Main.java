import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        int size = 0;
        int delay = 1000;
        int displaySize = 800;

        String objToSpawn = "";
        int lowerBound = 0;
        int upperBound = 0;
        int amountToSpawn = 0;

        Random rand = new Random();

        ArrayList<String> fileValues = new ArrayList<>();

        try {
            //File myFile = new File("./src/Tema 1/input-filer/t1-1a.txt");
            File myFile = new File("./src/Tema 1/input-filer/t1-1b.txt");
            Scanner reader = new Scanner(myFile);

            while (reader.hasNextLine()) {
                fileValues.add(reader.nextLine());
            }

            reader.close();

            size = Integer.parseInt(fileValues.get(0));

            objToSpawn = fileValues.get(1).split(" ")[0];

            if (objToSpawn.equals("grass")) {
                if (fileValues.get(1).contains("-")) {
                    lowerBound = Integer.parseInt(fileValues.get(1).split(" ")[1].split("-")[0]);
                    upperBound = Integer.parseInt(fileValues.get(1).split(" ")[1].split("-")[1]);

                    amountToSpawn = rand.nextInt(upperBound-lowerBound) + lowerBound;
                } else {
                    amountToSpawn = Integer.parseInt(fileValues.get(1).split(" ")[1]);
                }
            }

            Program p = new Program(size, displaySize, delay);
            World world = p.getWorld();

            for (int i = 0; i < amountToSpawn; i++) {
                int x = rand.nextInt(size);
                int y = rand.nextInt(size);
                Location l = new Location(x, y);

                while (!world.isTileEmpty(l)) {
                    x = rand.nextInt(size);
                    y = rand.nextInt(size);
                    l = new Location(x, y);
                }

                world.setTile(l, new Grass());

                DisplayInformation di = new DisplayInformation(Color.white, "grass");
                p.setDisplayInformation(Grass.class, di);
            }

            p.show();

            for (int i = 0; i < 200; i++) {
                p.simulate();
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}