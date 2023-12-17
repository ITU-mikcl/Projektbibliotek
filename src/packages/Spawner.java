package packages;

import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.lang.reflect.Constructor;
import java.util.*;

import packages.animals.*;
import packages.terrain.Carcass;
import packages.terrain.Fungi;


public class Spawner{
    public static ArrayList<WolfPack> wolfPacks = new ArrayList<>();
    public static Random rand = new Random();
    public static Wolf wolfCurrent;
    public static World world;
    public static Program p;
    public static int size;
    private static int amountToSpawn = 0;
    private static int xCoords = 0;
    private static int yCoords = 0;
    private static boolean hasCoords = false;

    public Spawner(World world, Program p, int size) {
        Spawner.world = world;
        Spawner.p = p;
        Spawner.size = size;
    }

    public static void spawnObject(ArrayList<String> fileValues) {
        String objToSpawn;
        int x;
        int y;

        for (int i = 1 ; i < fileValues.size(); i++){
            objToSpawn = fileValues.get(i).split(" ")[0];

            objToSpawn = Character.toUpperCase(objToSpawn.charAt(0)) + objToSpawn.substring(1);

            boolean isObjectBlocking = false;

            try {
                isObjectBlocking = Actor.class.isAssignableFrom(Class.forName( "packages.animals." + objToSpawn));

                if (!isObjectBlocking) {
                    isObjectBlocking = Actor.class.isAssignableFrom(Class.forName( "packages.terrain." + objToSpawn));
                }
            } catch (ClassNotFoundException | NumberFormatException e) {

            }

            checkSpace(isObjectBlocking);

            getAmountToSpawn(fileValues, i, objToSpawn.equals("bear"));

            for (int j = 0; j < amountToSpawn; j++) {
                x = rand.nextInt(size);
                y = rand.nextInt(size);
                Location l = new Location(x, y);

                switch (objToSpawn) {
                    case "Wolf":
                        spawnWolf(l, size, (j < 1), null);
                        break;
                    case "Bear":
                        spawnBear(l, new Location(xCoords, yCoords));
                        break;
                    case "Carcass":
                        spawnCarcass(l);
                        break;
                    case "Fungi":
                        spawnFungi(l);
                        break;
                    default:
                        spawnObject(objToSpawn, world, p, size, isObjectBlocking);
                        break;
                }

            }
        }
    }

    private static void getAmountToSpawn(ArrayList<String> fileValues, int i, boolean spawnABear) {
        if (fileValues.get(i).contains("-")) {
            int lowerBound = Integer.parseInt(fileValues.get(i).split(" ")[1].split("-")[0]);
            int upperBound = Integer.parseInt(fileValues.get(i).split(" ")[1].split("-")[1]);

            amountToSpawn = rand.nextInt(upperBound - lowerBound) + lowerBound;
        } else {
            amountToSpawn = Integer.parseInt(fileValues.get(i).split(" ")[1]);
        }

        if (spawnABear) {
            if (fileValues.get(i).contains(",")) {
                xCoords = Integer.parseInt(fileValues.get(i).split("[(]")[1].split(",")[0]);
                yCoords = Integer.parseInt(fileValues.get(i).split(",")[1].split("[)]")[0]);

                hasCoords = true;
            } else {
                hasCoords = false;
            }
        }
    }

    private static Location getEmptyTile(Location l) {
        int x;
        int y;

        while (!world.isTileEmpty(l)) {
            x = rand.nextInt(size);
            y = rand.nextInt(size);
            l = new Location(x, y);
        }

        return l;
    }

    public static void spawnWolf(Location l, int size, boolean spawnALeader, WolfPack myWolfPack) {
        for (int i = 1; i < size; i++) {
            for (Location targetLocation : world.getSurroundingTiles(l, i)) {
                if (world.isTileEmpty(targetLocation)) {
                    l = targetLocation;
                }
            }
        }

        if(spawnALeader){
            wolfPacks.add(new WolfPack(world,p,wolfPacks.size()));
        }

        if (myWolfPack == null) {
            myWolfPack = wolfPacks.get(wolfPacks.size() - 1);
        }

        wolfCurrent = new Wolf(world, p, spawnALeader, myWolfPack);
        myWolfPack.addWolf(wolfCurrent);
        world.setTile(l, wolfCurrent);
    }

    private static void spawnBear(Location l, Location centerLocation) {
        Location myLocation = getEmptyTile(l);

        if (hasCoords) {
            world.setTile(myLocation, new Bear(world, p, world.getSurroundingTiles(centerLocation, (world.getSize() / 3))));
        } else {
            world.setTile(myLocation, new Bear(world, p, world.getSurroundingTiles(myLocation, (world.getSize() / 3))));
        }
    }

    private static void spawnCarcass(Location l){
        Location myLocation = getEmptyTile(l);

        world.setTile(myLocation, new Carcass(world,p,"carcass", "fungi"));
    }

    private static void spawnFungi(Location l){
        Location myLocation = getEmptyTile(l);

        Fungi currentFungi =  new Fungi(world,p,"fungi", myLocation);
        world.setTile(myLocation, currentFungi);
        world.remove(currentFungi);
    }

    private static void spawnObject(String className, World world, Program p, int size, boolean isBlocking) {
        boolean isAnimal = false;

        try {
            isAnimal = Animal.class.isAssignableFrom(Class.forName( "packages.animals." + className));
        } catch (ClassNotFoundException e) {

        }

        if (isAnimal) {
            className = "packages.animals." + className;
        } else {
            className = "packages.terrain." + className;
        }

        Random rand = new Random();
        int x = rand.nextInt(size);
        int y = rand.nextInt(size);
        Location l = new Location(x, y);

        if (isBlocking) {
            l = getEmptyTile(l);
        } else {
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
            System.out.println(e.getMessage());
        }
    }

    public static void checkSpace(boolean isBlocking) {
        if (isBlocking) {
            for(Location tile : world.getSurroundingTiles(new Location(0,0),size)) {
                if (world.isTileEmpty(tile)) {
                    return;
                }
            }

            throw new IllegalArgumentException("No space for Blocking object");
        } else {
            for(Location tile : world.getSurroundingTiles(new Location(0,0),size)) {
                if (!world.containsNonBlocking(tile)){
                    return;
                }
            }

            throw new IllegalArgumentException("No space for nonBlocking object");
        }
    }
}
