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
    private int hunger = 5;
    private Location myLocation;
    private boolean hasAHole = false;
    private Location theHoleLocation;
    private boolean timeToReproduce = false;
    final String[] images = {"rabbit-small", "rabbit-small-sleeping", "rabbit-large", "rabbit-sleeping"};
    public Rabbit(World world, Program p) {
        super(world, p, "rabbit-small", 2);
    }

    @Override
    public void act(World world) {
        hunger = super.statusCheck(this, hunger);

        if (super.canIAct()) {
            myLocation = world.getLocation(this);

            if (world.isDay()) {
                if (super.isAdult() && hunger >= 5) {
                    lookForPartner();
                } else {
                    lookForFood();
                }
            } else {
                if (myLocation != theHoleLocation){
                    lookForHole();
                }
            }

        } else {
            if (timeToReproduce) {
                reproduce();
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
                super.lookForNonBlocking(myLocation, Grass.class);
            }
        } catch (IllegalArgumentException e) {
            super.lookForNonBlocking(myLocation, Grass.class);
        }
    }

    private void lookForHole() {
        if (!hasAHole && hunger >= 3) {
            if (super.lookForNonBlocking(myLocation, Burrow.class)) {
                theHoleLocation = myLocation;
                hasAHole = true;
            } else {
                digHole();
            }
        } else {
            if (world.isTileEmpty(theHoleLocation)) {
                world.move(this, theHoleLocation);
            } else {
                hasAHole = false;
                lookForHole();
            }
        }
    }

    private void digHole() {
        if (!world.containsNonBlocking(myLocation)) {
            world.setTile(myLocation, new Burrow(world, p));
            theHoleLocation = myLocation;
            hasAHole = true;
        } else if(hasAHole){
            lookForFood();
            digHole();
        }
    }

    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.white, images[super.getState()]);
    }
}