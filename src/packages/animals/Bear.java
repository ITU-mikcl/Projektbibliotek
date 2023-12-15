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
    Location preyLocation, carcassLocation;
    public Bear(World world, Program p, Set<Location> myTerritory){
        super(world, p, "bear-small", 2, 10);
        this.myTerritory = myTerritory;
    }
    public void act(World world){
        hunger = getHunger(hunger);

        if (canIAct() && world.isDay()) {
            myLocation = world.getLocation(this);

            if (!isAdult && stepsSinceSpawned > 60) {
                isAdult = true;
                speed /= 2;
            }

            carcass = (Carcass) lookForMeat(Carcass.class);
            prey = (Rabbit) lookForMeat(Rabbit.class);

            try {
                preyLocation = world.getLocation(prey);
                carcassLocation = world.getLocation(carcass);
            } catch (IllegalArgumentException e) {

            }

            if (myTerritory.contains(carcassLocation) && carcass != null) {
                eatCarcass();
            } else if (myTerritory.contains(preyLocation) && prey != null) {
                prey = (Rabbit) killPrey(prey);
            } else if (!eatBerries()) {
                bearLookingForGrass();
            }
        }
    }

    private boolean eatBerries() {
        Location targetBushLocation = lookForBlocking(Berry.class);

        if (targetBushLocation != null) {
            Berry targetBush = (Berry) world.getTile(targetBushLocation);

            if (myTerritory.contains(targetBushLocation) && targetBush.hasBerries) {
                if (world.getSurroundingTiles().contains(targetBushLocation)) {
                    hunger += targetBush.eatBerries();
                } else {
                    moveToLocation(targetBushLocation);
                }
                return true;
            }
        }
        return false;
    }

    private void eatCarcass() {
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
                hunger += eat(standingOnGrass);
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

    protected int getState() {
        if(isAdult) {
            if (world.isNight()) {
                return 3;
            } else {
                return 2;
            }
        } else if (world.isNight()) {
            return 1;
        } else {
            return 0;
        }
    }

    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.white, images[getState()]);
    }
}