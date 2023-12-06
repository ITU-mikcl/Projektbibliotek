package packages.animals;

import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import packages.terrain.Grass;
import packages.terrain.Burrow;

import java.awt.*;

public class Rabbit extends Animal implements Actor {
    private int hunger = 10;
    private Location myLocation;
    private Location burrowLocation = null;
    private boolean timeToReproduce = false;
    final String[] images = {"rabbit-small", "rabbit-small-sleeping", "rabbit-large", "rabbit-sleeping"};
    public Rabbit(World world, Program p) {
        super(world, p, "rabbit-small", 2);
    }

    @Override
    public void act(World world) {
        hunger = statusCheck(this, hunger);

        if (!isDead()) {
            if (canIAct()) {
                if (world.isDay()) {
                    if (!world.isOnTile(this)) {
                        world.setTile(getEmptyTile(burrowLocation), this);
                    } else {
                        myLocation = world.getLocation(this);

                        if (isAdult() && hunger >= 5) {
                            lookForPartner();
                        } else {
                            lookForFood();
                        }
                    }
                } else {
                    if (world.isOnTile(this)) {
                        myLocation = world.getLocation(this);

                        if (!isOnBurrow()){
                            lookForHole();
                        } else {
                            world.remove(this);
                        }
                    }
                }
            } else {
                if (timeToReproduce) {
                    reproduce();
                }
            }

        }
    }

    protected boolean isOnBurrow() {
        if (burrowLocation == null || myLocation.hashCode() != burrowLocation.hashCode()) {
            return false;
        } else {
            return true;
        }
    }

    public void eat(Grass grass) {
        hunger += 5;
        grass.decompose();
    }

    private void lookForPartner() {
        if (lookForBlocking(myLocation, Rabbit.class) != null) {
            timeToReproduce = true;
            reproduce();
        }
    }

    private void reproduce() {
        for (Location birthLocation : world.getEmptySurroundingTiles()) {
            world.setTile(birthLocation, new Rabbit(world, p));
            hunger -= 5;
            timeToReproduce = false;
            break;
        }
    }

    private void lookForFood() {
        try {
            Object standingOn = world.getNonBlocking(world.getLocation(this));

            if (standingOn instanceof Grass) {
                Grass standingOnGrass = (Grass) standingOn;
                eat(standingOnGrass);
            } else {
                moveToLocation(myLocation, lookForNonBlocking(myLocation, Grass.class, sizeOfWorld));
            }
        } catch (IllegalArgumentException e) {
            moveToLocation(myLocation, lookForNonBlocking(myLocation, Grass.class, sizeOfWorld));
        }
    }

    private void lookForHole() {
        if (burrowLocation == null) {
            burrowLocation = lookForNonBlocking(myLocation, Burrow.class, 3);

            if (burrowLocation == null) {
                if (world.containsNonBlocking(myLocation)) {
                    lookForFood();
                } else {
                    world.setTile(myLocation, new Burrow(world, p));
                    burrowLocation = myLocation;
                }
            }
        } else {
            moveToLocation(myLocation, burrowLocation);
            myLocation = world.getLocation(this);
        }

    }

    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.white, images[getState(isOnBurrow())]);
    }
}