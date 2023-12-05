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
    private int hunger = 50;
    private Location myLocation;
    private Location burrowLocation = null;
    private boolean timeToReproduce = false;
    final String[] images = {"rabbit-small", "rabbit-small-sleeping", "rabbit-large", "rabbit-sleeping"};
    public Rabbit(World world, Program p) {
        super(world, p, "rabbit-small", 1);
    }

    @Override
    public void act(World world) {
        hunger = statusCheck(this, hunger);

        if (!isDead()) {
            if (canIAct()) {
                if (world.isDay()) {
                    if (!world.isOnTile(this)) {
                        world.setTile(getEmptyTile(burrowLocation), this);
                    }

                    myLocation = world.getLocation(this);

                    if (isAdult() && hunger >= 5) {
                        lookForPartner();
                    } else {
                        lookForFood();
                    }
                } else {
                    if (world.isOnTile(this)) {
                        if (myLocation != burrowLocation){
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

    public void eat(Grass grass) {
        hunger++;
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
            hunger -= 3;
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
                moveToTile(myLocation, Grass.class);
            }
        } catch (IllegalArgumentException e) {
            moveToTile(myLocation, Grass.class);
        }
    }

    private void lookForHole() {
        if (burrowLocation == null) {
            burrowLocation = lookForNonBlocking(myLocation, Burrow.class);

            if (burrowLocation == null) {
                world.setTile(myLocation, new Burrow(world, p));
                burrowLocation = myLocation;
            }
        }

        moveToTile(myLocation, Burrow.class);
        myLocation = burrowLocation;
    }

    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.white, images[getState()]);
    }
}