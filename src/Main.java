import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;
import java.util.ArrayList;
import packages.*;
import packages.animals.Rabbit;
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

        ArrayList<ArrayList<File>> themes = new ArrayList<>();
        ArrayList<File> theme1 = new ArrayList<>();
        ArrayList<File> theme2 = new ArrayList<>();

        try {


            /*
             Dette stykke kode står for at tilføje alle filer fra tema 1, til en
             ArrayList, og derefter tilføjge den Array til vores theme 1 ArrayList.
             */
            for (char c = 'a'; c <= 'd'; c++) {
                String fileName = String.format("t1-1%c.txt", c);
                String filePath = "./data/t1/" + fileName;
                theme1.add(new File(filePath));
            }
            theme1.add(new File("./data/t1/t1-2a.txt"));
            theme1.add(new File("./data/t1/t1-2b.txt"));
            theme1.add(new File("./data/t1/t1-2cde.txt"));
            theme1.add(new File("./data/t1/t1-2fg.txt"));
            theme1.add(new File("./data/t1/t1-3a.txt"));
            theme1.add(new File("./data/t1/t1-3b.txt"));
            themes.add(theme1);


            //Henter og læser filerne fra opgaven
            //File myFile = new File("./src/Tema 1/input-filer/t1-1a.txt");
            //File myFile = new File("./src/Tema 1/input-filer/t1-1b.txt");
            //File myFile = new File("./src/Tema 1/input-filer/t1-1c.txt");
            //File myFile = new File("./src/Tema 1/input-filer/t1-1d.txt");
            //File myFile = new File("./src/Tema 1/input-filer/t1-2a.txt");
            //File myFile = new File("./src/Tema 1/input-filer/t1-2b.txt");
            //File myFile = new File("./src/Tema 1/input-filer/t1-2cde.txt");
            //File myFile = new File("./src/Tema 1/input-filer/t1-2fg.txt");
            //File myFile = new File("./src/Tema 1/input-filer/t1-3a.txt");
            //File myFile = new File("./src/Tema 1/input-filer/t1-3b.txt");

            File myFile = theme1.get(6);
            Scanner reader = new Scanner(myFile);
            System.out.println(myFile);
            while (reader.hasNextLine()) {
                fileValues.add(reader.nextLine());
            }

            reader.close();

            size = Integer.parseInt(fileValues.get(0));
            Program p = new Program(size, displaySize, delay);
            World world = p.getWorld();

            for (int i = 1 ; i < fileValues.size(); i++){
                objToSpawn = fileValues.get(i).split(" ")[0];
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

                    if(objToSpawn.equals("grass")){
                        while (world.containsNonBlocking(l)) {
                            x = rand.nextInt(size);
                            y = rand.nextInt(size);
                            l = new Location(x, y);
                        }

                        world.setTile(l, new Grass(world, p));

                    } else if (objToSpawn.equals("rabbit")) {
                        while (!world.isTileEmpty(l)) {
                            x = rand.nextInt(size);
                            y = rand.nextInt(size);
                            l = new Location(x, y);
                        }

                        world.setTile(l, new Rabbit(world, p));
                    } else if(objToSpawn.equals("burrow")){
                        while (world.containsNonBlocking(l)) {
                            x = rand.nextInt(size);
                            y = rand.nextInt(size);
                            l = new Location(x, y);
                        }

                        world.setTile(l, new Hole(world, p));

                    }
                }
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