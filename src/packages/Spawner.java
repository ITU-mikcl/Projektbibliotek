package packages;

import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.ArrayList;

import packages.animals.*;



public class Spawner{
    public static ArrayList<WolfPack> wolfPacks = new ArrayList<>();
    public static HashSet<String> animals = new HashSet<>(Arrays.asList("rabbit","wolf","bear"));
    public static Random rand = new Random();
    public static Wolf wolfCurrent;
    public static World world;
    public static Program p;
    public static int size;

    public Spawner(World world, Program p, int size) {
        this.world = world;
        this.p = p;
        this.size = size;
    }

    public static void spawnObject(ArrayList<String> fileValues) {
        String objToSpawn;
        int lowerBound;
        int upperBound;
        int amountToSpawn;

        int xCoords;
        int yCoords;

        for (int i = 1 ; i < fileValues.size(); i++){
            objToSpawn = fileValues.get(i).split(" ")[0];
            if (objToSpawn.equals("wolf")){
                wolfPacks.add(new WolfPack(world,p,wolfPacks.size()));
            }

            if (!objToSpawn.equals("bear")) {
                if (fileValues.get(i).contains("-")) {
                    lowerBound = Integer.parseInt(fileValues.get(i).split(" ")[1].split("-")[0]);
                    upperBound = Integer.parseInt(fileValues.get(i).split(" ")[1].split("-")[1]);

                    amountToSpawn = rand.nextInt(upperBound-lowerBound) + lowerBound;
                } else {
                    amountToSpawn = Integer.parseInt(fileValues.get(i).split(" ")[1]);
                }
            } else {
                    lowerBound = Integer.parseInt(fileValues.get(i).split(" ")[1].split("-")[0]);
                    upperBound = Integer.parseInt(fileValues.get(i).split(" ")[1].split("-")[1]);

                    amountToSpawn = rand.nextInt(upperBound-lowerBound) + lowerBound;

                    xCoords = Integer.parseInt(fileValues.get(i).split(" ")[1].split("-")[1].split(",")[0]);
                    yCoords = Integer.parseInt(fileValues.get(i).split(" ")[1].split("-")[1].split(",")[1]);
            }

            for (int k = 0; k < amountToSpawn; k++) {
                int x = rand.nextInt(size);
                int y = rand.nextInt(size);
                Location l = new Location(x, y);

                    if(objToSpawn.equals("wolf")){
                        spawnWolf(l, x, y, size, k);
                    } else {
                        spawnObject(objToSpawn, world,p,size, animals.contains(objToSpawn));
                    }

            }
        }
    }

    public static void spawnWolf(Location l, int x, int y, int size, int spawnALeader) {
        while (!world.isTileEmpty(l)) {
            x = rand.nextInt(size);
            y = rand.nextInt(size);
            l = new Location(x, y);
        }
        if(spawnALeader==0){
            wolfCurrent = new Wolf(world, p, true, wolfPacks.size() - 1);
        }else{
            wolfCurrent = new Wolf(world, p, false, wolfPacks.size() - 1);
        }
        wolfPacks.get(wolfPacks.size() - 1).addWolf(wolfCurrent);
        world.setTile(l, wolfCurrent);
    }

    private static void spawnObject(String className, World world, Program p, int size, boolean isBlocking) {
        if(animals.contains(className)){
            className = "packages.animals." + Character.toUpperCase(className.charAt(0)) + className.substring(1);
        }else{
            className = "packages.terrain." + Character.toUpperCase(className.charAt(0)) + className.substring(1);

        }
        Random rand = new Random();
        int x = rand.nextInt(size);
        int y = rand.nextInt(size);
        Location l = new Location(x, y);

        if (isBlocking) {
        while (!world.isTileEmpty(l)) {
            x = rand.nextInt(size);
            y = rand.nextInt(size);
            l = new Location(x, y);
                }
            }else{
            while (world.containsNonBlocking(l)) {
                    x = rand.nextInt(size);
                    y = rand.nextInt(size);
                    l = new Location(x, y);
                }
            }

            try {
                Class<?> objectType = Class.forName(className);
                Constructor<?> constructor = objectType.getConstructor(World.class, Program.class);
                Object obj = constructor.newInstance(world, p);
                world.setTile(l, obj);
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        public static WolfPack getMyWolfpack(int myWolfPack) {
            return wolfPacks.get(myWolfPack);
        }
}
