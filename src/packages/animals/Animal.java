package packages.animals;

import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import packages.SpawnableObjects;

import java.util.Set;

public abstract class Animal extends SpawnableObjects implements DynamicDisplayInformationProvider {
    final int sizeOfWorld = world.getSize();
    private int stepsSinceSpawned = 0;
    private int speed;
    private boolean isAdult = false;
    private int currentTime = 0;
    public Animal (World world, Program p, String image, int speed){
        super(world,p,image);
        this.speed = speed;
    }

    protected boolean isAdult() {
        if (!isAdult && stepsSinceSpawned > 60) {
            isAdult = true;
            speed /= 2;
        }

        return isAdult;
    }

    protected int getState() {
        if(isAdult()) {
            if (world.isDay()) {
                return 2;
            } else {
                return 3;
            }
        } else if (world.isDay()) {
            return 0;
        } else {
            return 1;
        }
    }

    protected void die(Object me) {
        world.delete(me);
    }

    protected int statusCheck(Object me, int hunger) {
        stepsSinceSpawned++;
        currentTime = world.getCurrentTime();

        if (currentTime == 0) {

            getInformation();
        } else if (currentTime == 10) {
            getInformation();
        }

        if (hunger == 0) {
            die(me);
        }

        return --hunger;
    }

    protected boolean canIAct() {
        return stepsSinceSpawned % speed == 0;
    }

    protected Location lookForNonBlocking(Location myLocation, Class<?> targetClass) {
        for (int i = 1; i < sizeOfWorld; i++) {
            for (Location targetLocation : world.getSurroundingTiles(myLocation, i)) {
                if (world.containsNonBlocking(targetLocation)
                        && targetClass.isInstance(world.getNonBlocking(targetLocation))
                        && world.isTileEmpty(targetLocation)) {
                    //world.move(this, targetLocation);
                    return targetLocation;
                }
            }
        }
        return null;
    }

    protected Location lookForBlocking(Location myLocation, Class<?> targetClass){
        for (int i = 1; i < sizeOfWorld; i++) {
            for (Location targetLocation: world.getSurroundingTiles(myLocation, i)) {
                if (targetClass.isInstance(world.getTile(targetLocation)) && !world.getEmptySurroundingTiles(targetLocation).isEmpty()) {
                    for (Location partnerLocationEmptySurroundingTile: world.getEmptySurroundingTiles(targetLocation)){
                        world.move(this,partnerLocationEmptySurroundingTile);
                        return targetLocation;
                    }
                }
            }
        }
        return null;
    }
    protected double calculateDistance(Location myLocation, Class<?> targetClass){
        double x1 = myLocation.getX();
        double y1 = myLocation.getY();
        double x2 = lookForNonBlocking(myLocation, targetClass).getX();
        double y2 = lookForNonBlocking(myLocation, targetClass).getY();

        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    public void moveToTile(Location myLocation, Class<?> targetClass){
        double minDistance = Double.POSITIVE_INFINITY;
        for (Location tile : world.getSurroundingTiles(myLocation)){
            double x1 = lookForNonBlocking(myLocation, targetClass).getX();;
            double y1 = lookForNonBlocking(myLocation, targetClass).getY();;
            double x2 = tile.getX();
            double y2 = tile.getY();
            double surroundingDistance = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
            double distance = calculateDistance(myLocation, targetClass) - surroundingDistance;
            if(distance < surroundingDistance){
                world.move(this, tile);
            }
        }

    }
}
