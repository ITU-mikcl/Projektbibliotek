package packages.animals;

import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import packages.SpawnableObjects;

import javax.swing.table.TableRowSorter;
import java.util.Set;

public abstract class Animal extends SpawnableObjects implements DynamicDisplayInformationProvider {
    final int sizeOfWorld = world.getSize();
    private int stepsSinceSpawned = 0;
    private int speed;
    private boolean isAdult = false;
    private boolean isDead = false;
    protected Location burrowLocation = null;
    protected Location myLocation;
    protected int hunger;
    public Animal (World world, Program p, String image, int speed, int hunger){
        super(world,p,image);
        this.speed = speed;
        this.hunger = hunger;
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

    protected void die(Animal me) {
        me.isDead = true;
        world.delete(me);
    }

    protected boolean isDead() {
        return isDead;
    }

    protected int statusCheck(Animal me, int hunger) {
        stepsSinceSpawned++;

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
        try {
            return stepsSinceSpawned % speed == 0;
        } catch (ArithmeticException e) {
            return true;
        }
    }

    protected Location lookForAnyBlocking(Location myLocation, Class<?> targetClass, int radius){
        for (Location targetLocation : world.getSurroundingTiles(myLocation, radius)) {
            if (!world.isTileEmpty(targetLocation)
                    && targetClass.isInstance(world.getTile(targetLocation))){
                return targetLocation;
            }
        }
        return null;
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

    protected boolean isOnBurrow(Location burrowLocation, Location myLocation) {
        return burrowLocation != null && myLocation.hashCode() == burrowLocation.hashCode();
    }
}
