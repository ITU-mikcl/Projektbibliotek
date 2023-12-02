import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;
import java.util.ArrayList;

import packages.FileReader;
import packages.animals.Rabbit;
import packages.animals.Wolf;
import packages.terrain.Grass;
import packages.terrain.Hole;

public class Main {
    public static void main(String[] args) {
        int size;
        int delay = 1000;
        int displaySize = 800;

        String objToSpawn;
        int lowerBound;
        int upperBound;
        int amountToSpawn;

        Random rand = new Random();

        ArrayList<String> fileValues = new ArrayList<>();

        int packCounter = 0;
        Wolf wolfCurrent;
        boolean shouldBeLeader = false;
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

            for (int i = 1 ; i < fileValues.size(); i++){
                objToSpawn = fileValues.get(i).split(" ")[0];
                if (objToSpawn.equals("wolf")){
                    packCounter++;
                    shouldBeLeader = true;
                }
                if (fileValues.get(i).contains("-")) {
                    lowerBound = Integer.parseInt(fileValues.get(i).split(" ")[1].split("-")[0]);
                    upperBound = Integer.parseInt(fileValues.get(i).split(" ")[1].split("-")[1]);

                    amountToSpawn = rand.nextInt(upperBound-lowerBound) + lowerBound;
                } else {
                    amountToSpawn = Integer.parseInt(fileValues.get(i).split(" ")[1]);
                }
                for (int k = 0; k < amountToSpawn; k++) {
                    int x = rand.nextInt(size);
                    int y = rand.nextInt(size);
                    Location l = new Location(x, y);

                    switch (objToSpawn) {
                        case "grass":
                            while (world.containsNonBlocking(l)) {
                                x = rand.nextInt(size);
                                y = rand.nextInt(size);
                                l = new Location(x, y);
                            }

                            world.setTile(l, new Grass(world, p));

                            break;
                        case "rabbit":
                            while (!world.isTileEmpty(l)) {
                                x = rand.nextInt(size);
                                y = rand.nextInt(size);
                                l = new Location(x, y);
                            }

                            world.setTile(l, new Rabbit(world, p));
                            break;
                        case "burrow":
                            while (world.containsNonBlocking(l)) {
                                x = rand.nextInt(size);
                                y = rand.nextInt(size);
                                l = new Location(x, y);
                            }

                            world.setTile(l, new Hole(world, p));

                            break;
                        case "wolf":
                            while (world.containsNonBlocking(l)) {
                                x = rand.nextInt(size);
                                y = rand.nextInt(size);
                                l = new Location(x, y);
                            }
                            wolfCurrent = new Wolf(world, p, shouldBeLeader, packCounter);
                            shouldBeLeader = false;

                            world.setTile(l, wolfCurrent);

                            break;
                    }
                }
            }
            p.show();

            for (int i = 0; i < 200; i++) {
                p.simulate();
            }
        }
}
