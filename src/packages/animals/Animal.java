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
    private boolean isDead = false;
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

    protected int getState(Boolean isOnBurrow) {
        if(isAdult()) {
            if (world.isNight() && isOnBurrow) {
                return 3;
            } else {
                return 2;
            }
        } else if (world.isNight() && isOnBurrow) {
            return 1;
        } else {
            return 0;
        }
    }

    protected void die(Object me) {
        isDead = true;
        world.delete(me);
    }

    protected boolean isDead() {
        return isDead;
    }

    protected int statusCheck(Object me, int hunger) {
        stepsSinceSpawned++;
        currentTime = world.getCurrentTime();

        if (hunger == 0) {
            die(me);
        }

        if (stepsSinceSpawned % 5 == 0) {
            return --hunger;
        } else {
            return hunger;
        }
    }

    protected boolean canIAct() {
        return stepsSinceSpawned % speed == 0;
    }

    protected Location lookForNonBlocking(Location myLocation, Class<?> targetClass, int radius) {
        for (int i = 1; i < radius; i++) {
            for (Location targetLocation : world.getSurroundingTiles(myLocation, i)) {
                if (world.containsNonBlocking(targetLocation)
                        && targetClass.isInstance(world.getNonBlocking(targetLocation))
                        && world.isTileEmpty(targetLocation)) {
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
                        moveTo(this,partnerLocationEmptySurroundingTile);
                        return targetLocation;
                    }
                }
            }
        }
        return null;
    }

    private Location nextTile(Location myLocation, Location targetLocation, Set<Location> surroundingTiles) {
        double minDistance = Double.POSITIVE_INFINITY;
        Location tileToMoveTo = myLocation;

        for (Location tile : surroundingTiles){
            if (world.isTileEmpty(tile)) {
                double x1 = targetLocation.getX();
                double y1 = targetLocation.getY();
                double x2 = tile.getX();
                double y2 = tile.getY();
                double surroundingDistance = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));

                if (surroundingDistance < minDistance) {
                    minDistance = surroundingDistance;
                    tileToMoveTo = tile;
                }
            }
        }

        return tileToMoveTo;
    }

    public void moveToLocation(Location myLocation, Location targetLocation){
        Set<Location> surroundingTiles = world.getSurroundingTiles(myLocation);

        for (Location tile : surroundingTiles) {
            if (world.isTileEmpty(tile)) {
                try {
                    if (tile == targetLocation) {
                        moveTo(this, tile);
                        return;
                    }
                } catch (IllegalArgumentException e) {

                }
            }
        }

        moveTo(this, nextTile(myLocation, targetLocation, surroundingTiles));
    }

    protected Location getEmptyTile(Location theLocation) {
        if (world.isTileEmpty(theLocation)) {
            return theLocation;
        }

        for (int i = 0; i < sizeOfWorld; i++) {
            for (Location targetLocation : world.getSurroundingTiles(theLocation, i)) {
                if (world.isTileEmpty(targetLocation)) {
                    return targetLocation;
                }
            }
        }

        return null;
    }

    protected void moveTo(Object me, Location tile) {
        world.move(me, tile);
        world.setCurrentLocation(tile);
    }
}
