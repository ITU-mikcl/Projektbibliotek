package packages.animals;

import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import packages.Organism;
import packages.terrain.Carcass;
import packages.terrain.Grass;

import java.util.Set;

public abstract class Animal extends Organism implements DynamicDisplayInformationProvider {
    final int sizeOfWorld = world.getSize();
    protected int speed;
    public boolean isAdult = false;
    public boolean isDead = false;
    protected Location burrowLocation = null;
    /**
     * This is Aniamals' constructor,
     * parameters World, Program and image are inhereted
     * from super class 'SpawnableObjects'
     * speed and hunger are initialized in animal.
     */
    public Animal (World world, Program p, String image, int speed, int hunger){
        super(world,p,image);
        this.speed = speed;
        this.hunger = hunger;
    }

    public void die() {
        isDead = true;
        world.delete(this);
        Carcass carcass = new Carcass(world,p,"carcass", "fungi");
        world.setTile(myLocation,carcass);
    }

    protected boolean canIAct() {
        return !isDead && stepsSinceSpawned % speed == 0;
    }

    protected void wakeUp() {
        if (!world.isOnTile(this)) {
            adultCheck();
            Location wakeUpLocation = getEmptyTileClosestToLocation(burrowLocation);
            if (wakeUpLocation != null) {
                world.setTile(wakeUpLocation, this);
                myLocation = wakeUpLocation;
                world.setCurrentLocation(wakeUpLocation);
            }
        }
    }

    protected void adultCheck(){
        if (!isAdult && stepsSinceSpawned > 60) {
            isAdult = true;
            speed /= 2;
        }
    }

    protected Location getEmptyTileClosestToLocation(Location theLocation) {
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

    protected Location lookForBlocking(Class<?> targetClass){
        for (int i = 1; i < sizeOfWorld; i++) {
            for (Location targetLocation : world.getSurroundingTiles(myLocation, i)) {
                Set<Location> targetClassSurroundingTiles = world.getSurroundingTiles();
                if (targetClass.isInstance(world.getTile(targetLocation)) && !targetClassSurroundingTiles.isEmpty()) {
                    return targetLocation;
                }
            }
        }
        return null;
    }

    protected void moveToLocation(Location targetLocation){
        if (targetLocation != null){
            Set<Location> surroundingTiles = world.getEmptySurroundingTiles(myLocation);
            if (!surroundingTiles.isEmpty()) {
                for (Location tile : surroundingTiles) {
                    if (tile == targetLocation) {
                        moveTo(tile);
                        return;
                    }
                }
                moveTo(nextTile(targetLocation, surroundingTiles));
            }
        }
    }

    protected void moveTo(Location tile) {
        world.move(this, tile);
        world.setCurrentLocation(tile);
        myLocation = tile;
    }

    protected double calculateDistance(Location tile, Location targetLocation) {
        double x1 = targetLocation.getX();
        double y1 = targetLocation.getY();
        double x2 = tile.getX();
        double y2 = tile.getY();
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    private Location nextTile(Location targetLocation, Set<Location> surroundingTiles) {
        double minDistance = Double.POSITIVE_INFINITY;
        Location tileToMoveTo = myLocation;
        double surroundingDistance;

        for (Location tile : surroundingTiles){
            surroundingDistance = calculateDistance(tile, targetLocation);

            if (surroundingDistance < minDistance) {
                minDistance = surroundingDistance;
                tileToMoveTo = tile;
            }
        }
        return tileToMoveTo;
    }

    /**
     * Method makes animal look for grass
     * tries to check if nonBlocking object is
     */
    protected void lookForGrass() {
        try {
            Object standingOn = world.getNonBlocking(world.getLocation(this));

            if (standingOn instanceof Grass) {
                Grass standingOnGrass = (Grass) standingOn;
                hunger += eat(standingOnGrass);
                return;
            }
        } catch (IllegalArgumentException e) {

        }

        moveToLocation(lookForNonBlocking(Grass.class, sizeOfWorld));
    }

    public int eat(Object food) {
        world.delete(food);
        if (food instanceof Carcass && ((Carcass) food).isBig()) {
            return 10;
        } else {
            return 5;
        }
    }

    protected Location lookForNonBlocking(Class<?> targetClass, int radius) {
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
    protected Animal killPrey(Animal prey) {
        Location preyLocation = world.getLocation(prey);
        if (world.getSurroundingTiles().contains(preyLocation)) {
            prey.die();
            return null;
        } else {
            moveToLocation(world.getEmptySurroundingTiles(preyLocation).iterator().next());
            return prey;
        }
    }

    protected boolean isOnBurrow(Location burrowLocation) {
        return burrowLocation != null && myLocation.hashCode() == burrowLocation.hashCode();
    }

    protected Location lookForAnyBlocking(Class<?> targetClass, int radius){
        for (Location targetLocation : world.getSurroundingTiles(myLocation, radius)) {
            if (!world.isTileEmpty(targetLocation)
                    && targetClass.isInstance(world.getTile(targetLocation))){
                return targetLocation;
            }
        }
        return null;
    }

    protected Object lookForMeat(Class<?> targetClass) {
        try {
            return world.getTile(lookForBlocking(targetClass));
        } catch (NullPointerException e) {
            return null;
        }
    }

    protected int getState(Boolean isOnBurrow) {
        if(isAdult) {
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
}
