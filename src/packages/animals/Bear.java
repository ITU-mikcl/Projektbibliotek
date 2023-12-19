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

/**
 * The Bear class is an animal that can act on its own.
 */
public class Bear extends Animal implements Actor {

    private Rabbit prey;
    final String[] images = {"bear-small", "bear-small-sleeping", "bear", "bear-sleeping"};
    Location myLocation;
    Set<Location> myTerritory;
    Carcass carcass;
    Location preyLocation, carcassLocation;

    /**
     * Initializes a new Bear.
     * @param world World
     * @param p Program
     * @param myTerritory Set of its territory that the bear protects
     */
    public Bear(World world, Program p, Set<Location> myTerritory){
        super(world, p, "bear-small", 2, 10);
        this.myTerritory = myTerritory;
    }

    /**
     * This method can be used by everything (main)
     * It handles how the bear acts and calls all functions that
     * dictate the bears behaviour.
     * @param world World
     */
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

    /**
     * This method allows a bear to eat berries if there are any.
     * It also updates the hunger value of the bear after eating berries.
     * @return boolean value, if true the bear will either eat the berry or move towards the bush
     */
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

    /**
     * This method allows a bear to eat a carcass.
     * It also updates the hunger after successfully eating one.
     * If it isn't already nearby it will find and move to the carcass location.
     */
    private void eatCarcass() {
        if (world.getSurroundingTiles().contains(carcassLocation)) {
            hunger += (eat(carcass));
            carcass = null;
        } else {
            moveToLocation(world.getEmptySurroundingTiles(carcassLocation).iterator().next());
        }
    }

    /**
     * this method allows a bear to eat grass if there is any.
     * if it is standing on grass it will eat it and update the hunger
     * accordingly. If not it will find and move to grass somewhere on the map.
     */
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

    /**
     * This method makes a bear look for any nonblocking object in its territory.
     * In this method a class of the object must be specified as a parameter.
     * If the specified object is anywhere in the bears territory it will return its location.
     * @param targetClass Classname of object the bear wants to find.
     * @return Location of target object (If in its territory) else null
     */
    private Location lookForNonBlockingInMyTerritory(Class<?> targetClass) {
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

    /**
     * This method is used to get the state of the bear.
     * The different states define what picture of the bear
     * must be used. Value 0 means the animal is small and awake.
     * 1 means it is small and sleeping.
     * 2 means it's an adult and awake and 3 adult and sleeping.
     *
     * @return integer value defining which state the bear is in.
     */
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

    /**
     * This method is for how the bear is displayed and what state it is in.
     * @return DisplayInformation
     */
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.white, images[getState()]);
    }
}