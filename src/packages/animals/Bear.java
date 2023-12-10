package packages.animals;

import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import packages.terrain.Berry;
import packages.terrain.Grass;

import java.awt.*;
import java.util.Set;

public class Bear extends Animal implements Actor {

    private Rabbit prey;
    final String[] images = {"bear-small", "bear-small-sleeping", "bear", "bear-sleeping"};
    Location myLocation;
    Set<Location> myTerritory;
    public Bear(World world, Program p, Set<Location> myTerritory){
        super(world, p, "bear-small", 2, 10);
        this.myTerritory = myTerritory;
    }
    public void act(World world){
        if (!isDead()) {
            if (canIAct()) {
                if (world.isDay()) {
                    if (!world.isOnTile(this)) {

                    } else {
                        myLocation = world.getLocation(this);

                        if (prey != null) {
                            if (world.isOnTile(prey)) {
                                if (myTerritory.contains(world.getLocation(prey))) {
                                    killPrey();
                                } else {
                                    prey = null;
                                }
                            }
                        } else {
                            if (lookForClosestFood(myLocation) instanceof Rabbit) {
                                prey = bearLookForPrey();
                            } else if (!eatBerries()) {
                                bearLookingForGrass();
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean eatBerries() {
        Location targetBushLocation = bearLookForBlocking(Berry.class);

        if (targetBushLocation != null) {
            if (world.getSurroundingTiles().contains(targetBushLocation)) {
                Berry targetBush = (Berry) world.getTile(targetBushLocation);
                hunger += targetBush.eatBerries();
            } else {
                moveToLocation(myLocation, lookForNonBlockingInMyTerritory(myLocation, Berry.class, sizeOfWorld));
            }
            return true;
        }

        return false;
    }

    private Location bearLookForBlocking(Class<?> targetClass){
        for (int i = 1; i < sizeOfWorld; i++) {
            for (Location targetLocation: world.getSurroundingTiles(myLocation, i)) {
                if (targetClass.isInstance(world.getTile(targetLocation))
                        && !world.getEmptySurroundingTiles(targetLocation).isEmpty()
                        && myTerritory.contains(targetLocation)) {
                    return targetLocation;
                }
            }
        }
        return null;
    }

    private Rabbit bearLookForPrey(){
        try {
            return (Rabbit) world.getTile(bearLookForBlocking(Rabbit.class));
        } catch (NullPointerException e) {
            return null;
        }
    }

    private void bearLookingForGrass() {
        try {
            Object standingOn = world.getNonBlocking(world.getLocation(this));

            if (standingOn instanceof Grass) {
                Grass standingOnGrass = (Grass) standingOn;
                eat(standingOnGrass);
            } else {
                moveToLocation(myLocation, lookForNonBlockingInMyTerritory(myLocation, Grass.class, sizeOfWorld));
            }
        } catch (IllegalArgumentException e) {
            moveToLocation(myLocation, lookForNonBlockingInMyTerritory(myLocation, Grass.class, sizeOfWorld));
        }
    }

    protected Location lookForNonBlockingInMyTerritory(Location myLocation, Class<?> targetClass, int radius) {
        for (int i = 1; i < radius; i++) {
            for (Location targetLocation : world.getSurroundingTiles(myLocation, i)) {
                if (world.containsNonBlocking(targetLocation)
                        && targetClass.isInstance(world.getNonBlocking(targetLocation))
                        && world.isTileEmpty(targetLocation)
                        && myTerritory.contains(targetLocation)) {
                    return targetLocation;
                }
            }
        }
        return null;
    }

    private void killPrey() {
        try{
            Location anyBlockingLocation = lookForAnyBlocking(myLocation, prey.getClass(), 1);
            Set<Location> surroundingTiles = world.getSurroundingTiles(myLocation);

            for(Location surroundingTile : surroundingTiles) {
                if (anyBlockingLocation != null) {
                    if (surroundingTile.hashCode() == anyBlockingLocation.hashCode()) {
                        die(prey);
                        hunger += 5;
                        prey = null;
                        return;
                    }
                }
            }
            moveToLocation(myLocation,lookForBlocking(myLocation, prey.getClass()));
        }catch (NullPointerException ignore){}
    }

    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.white, images[getState(isOnBurrow(burrowLocation, myLocation))]);
    }
}
