package packages.animals;

import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import packages.terrain.Burrow;
import packages.terrain.Carcass;

import java.awt.*;

/**
 * The rabbit class is a fossorial animal that can act on its own.
 * It decides how a rabbit shall act.
 */
public class Rabbit extends FossorialAnimals implements Actor {
    boolean isBeingChased = false;
    final String[] images = {"rabbit-small", "rabbit-small-sleeping", "rabbit-large", "rabbit-sleeping"};

    /**
     * The rabbit constructor initializes a new rabbit as an animal with the
     * rabbit small image and speed of 2 and 10 hunger.
     * @param world World
     * @param p Program
     */
    public Rabbit(World world, Program p) {
        super(world, p, "rabbit-small", 2, 10);
    }

    /**
     * This method comes from the Actor interface and handles the
     * rabbits overall behavior. It is from this method every other
     * rabbit method is called.
     * @param world providing details of the position on which the actor is currently located and much more.
     */
    @Override
    public void act(World world) {
        hunger = getHunger(hunger);

        if (canIAct()) {
            if (world.isDay()) {
                dayAct();
            } else {
                nightAct();
            }
        }
    }

    /**
     * This method handles the rabbits daytime behaviour.
     * If a rabbit is in a burrow it calls wakeup.
     * It also recognizes predator animals and moves away from them.
     * It also calls the reproduce method if it isn't being chased and
     * its an adult while hunger is over 5.
     * Lastly it calls the lookforgrass method.
     */
    private void dayAct() {
        if (burrowLocation != null) {
            wakeUp();
        }

        Object predator;
        for(Location location : world.getSurroundingTiles(2)){
            predator = world.getTile(location);
            if(predator instanceof Wolf || predator instanceof Bear){
                moveTo(nextOppositeTile(location));
                isBeingChased = true;
                break;
            }
            isBeingChased = false;
        }

        if (!isBeingChased){
            if (isAdult && hunger >= 5) {
                reproduce("Rabbit");
            }

            lookForGrass();
        }
    }

    /**
     * This method handles the rabbits nighttime behaviour whch is returning to its burrow.
     */
    private void nightAct() {
        if (world.isOnTile(this)) {
            getToBurrow();
        }
    }

    /**
     * This method handles how the rabbit dies by deleting it.
     * It's also responsible for spawning a carcass in its place
     * which will later become a fungi if left to itself.
     */
    @Override
    public void die() {
        isDead = true;
        world.delete(this);
        Carcass carcass = new Carcass(world,p,"carcass-small", "fungi-small");
        world.setTile(myLocation,carcass);
    }

    /**
     * This method contains the logic behind how a rabbit must escape a predator.
     * It takes a predators location as a parameter and uses the eucledian algorithm
     * to move to a nearby tile that is further away from the predator.
     * @param targetLocation Location of predator.
     * @return Location of a nearby tile that is further away from the predator
     */
    private Location nextOppositeTile(Location targetLocation) {
        double maxDistance = Double.MIN_VALUE;
        Location tileToMoveTo = myLocation;
        double surroundingDistance;

        for (Location tile : world.getEmptySurroundingTiles()){
            surroundingDistance = calculateDistance(tile, targetLocation);

            if (surroundingDistance > maxDistance) {
                maxDistance = surroundingDistance;
                tileToMoveTo = tile;
            }
        }

        return tileToMoveTo;
    }

    /**
     * This method contains the logic behind how a rabbit looks for a burrow.
     * It is also responsible for creating a new burrow if it doesn't already
     * have one. If doesn't and it is standing on grass it will call the lookForGrass
     * method first.
     */
    @Override
    public void lookForBurrow() {
        if (burrowLocation == null) {
            burrowLocation = lookForAnyBlocking(Burrow.class, 3);

            if (burrowLocation == null) {
                if (world.containsNonBlocking(myLocation)) {
                    lookForGrass();
                } else {
                    world.setTile(myLocation, new Burrow(world, p, "hole-small"));
                    burrowLocation = myLocation;
                }
            }
        } else {
            moveToLocation(burrowLocation);
        }
    }

    /**
     * This method handles the displayinformation.
     * @return DisplayInformation
     */
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.white, images[getState(isOnBurrow(burrowLocation))]);
    }
}