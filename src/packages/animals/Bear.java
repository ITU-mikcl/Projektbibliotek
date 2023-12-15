package packages.animals;

import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import packages.terrain.Berry;
import packages.terrain.Carcass;
import packages.terrain.Grass;

import java.awt.*;
import java.util.Set;

public class Bear extends Animal implements Actor {

    private Rabbit prey;
    final String[] images = {"bear-small", "bear-small-sleeping", "bear", "bear-sleeping"};
    Location myLocation;
    Set<Location> myTerritory;
    Carcass carcass;
    public Bear(World world, Program p, Set<Location> myTerritory){
        super(world, p, "bear-small", 2, 10);
        this.myTerritory = myTerritory;
    }
    public void act(World world){
        hunger = getHunger(hunger);

        if (canIAct() && world.isDay()) {
            carcass = (Carcass) lookForMeat(Carcass.class);
            prey = (Rabbit) lookForMeat(Rabbit.class);
            if (myTerritory.contains(carcass)) {
                eatCarcass();
            } else if (myTerritory.contains(prey)) {
                prey = (Rabbit) killPrey(prey);
            } else if (!eatBerries()) {
                if (myLocation != null) {
                    bearLookingForGrass();
                } else {
                    myLocation = world.getLocation(this);
                }
            }
        }
    }

    private boolean eatBerries() {
        Location targetBushLocation = lookForBlocking(Berry.class);

        if (myTerritory.contains(targetBushLocation)) {
            if (world.getSurroundingTiles().contains(targetBushLocation)) {
                Berry targetBush = (Berry) world.getTile(targetBushLocation);
                hunger += targetBush.eatBerries();
            } else {
                moveToLocation(lookForNonBlockingInMyTerritory(Berry.class));
            }
            return true;
        }
        return false;
    }

    private void eatCarcass() {
        Location carcassLocation = world.getLocation(carcass);
        if (world.getSurroundingTiles().contains(carcassLocation)) {
            hunger += (eat(carcass));
            carcass = null;
        } else {
            moveToLocation(world.getEmptySurroundingTiles(carcassLocation).iterator().next());
        }
    }

    private void bearLookingForGrass() {
        try {
            Object standingOn = world.getNonBlocking(world.getLocation(this));

            if (standingOn instanceof Grass) {
                Grass standingOnGrass = (Grass) standingOn;
                eat(standingOnGrass);
            } else {
                moveToLocation(lookForNonBlockingInMyTerritory(Grass.class));
            }
        } catch (IllegalArgumentException e) {
            moveToLocation(lookForNonBlockingInMyTerritory(Grass.class));
        }
    }

    protected Location lookForNonBlockingInMyTerritory(Class<?> targetClass) {
        for (int i = 1; i < sizeOfWorld; i++) {
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
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.white, images[getState(isOnBurrow(burrowLocation))]);
    }
}