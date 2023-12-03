package packages.animals;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import packages.SpawnableObjects;
import packages.terrain.Grass;
import packages.terrain.Burrow;

import java.awt.*;

public class Rabbit extends Animal implements Actor, DynamicDisplayInformationProvider {
    int sizeOfWorld;
    private int hunger = 5;
    private Location rabbitLoaction;

    private int stepsSinceSpawned;
    boolean isAdult = false;
    private int speed = 2;

    boolean hasAHole = false;
    Location theHoleLocation;

    String[] images = {"rabbit-small", "rabbit-large", "rabbit-small-sleeping", "rabbit-sleeping"};
    int growthState = 0;

    private boolean timeToReproduce = false;

    public Rabbit(World world, Program p) {
        super(world, p, "rabbit-small");
        this.stepsSinceSpawned = world.getCurrentTime();
        this.sizeOfWorld = world.getSize();
    }

    @Override
    public void act(World world) {
        stepsSinceSpawned++;

        if (!isAdult && stepsSinceSpawned > 60) {
            isAdult = true;
            speed = 5;

            growthState = 1;
            getInformation();
        }

        if (stepsSinceSpawned % speed == 0) {
            rabbitLoaction = world.getLocation(this);

            if (world.isDay()) {
                if (!isAdult) {
                    if (growthState == 2) {
                        growthState = 0;
                        getInformation();
                    }
                } else {
                    if (growthState == 3) {
                        growthState = 1;
                        getInformation();
                    }
                }

                if (isAdult && hunger >= 5) {
                    lookForPartner();
                } else {
                    lookForFood();
                }
            } else {
                if (rabbitLoaction != theHoleLocation){
                    lookForHole();
                }
                if (!isAdult) {
                    if (growthState == 0) {
                        growthState = 2;
                        getInformation();
                    }
                } else {
                    if (growthState == 1) {
                        growthState = 3;
                        getInformation();
                    }
                }
            }

        } else {
            if (timeToReproduce) {
                reproduce();
            }
        }

        if (stepsSinceSpawned % 20 == 0) {
            if (hunger == 0) {
                kill();
            }
            hunger--;
        }
    }

    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.white, images[growthState]);
    }

    public void eat(Grass grass) {
        hunger++;
        grass.decompose();
    }

    public void kill() {
        world.delete(this);
    }

    private void lookForPartner() {
        if (lookForBlocking(rabbitLoaction, Rabbit.class) != null) {
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
                super.lookForNonBlocking(rabbitLoaction, Grass.class);
            }
        } catch (IllegalArgumentException e) {
            super.lookForNonBlocking(rabbitLoaction, Grass.class);
        }
    }

    private void lookForHole() {
        if (!hasAHole && hunger >= 3) {
            if (super.lookForNonBlocking(rabbitLoaction, Burrow.class)) {
                theHoleLocation = rabbitLoaction;
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
        if (!world.containsNonBlocking(rabbitLoaction)) {
            world.setTile(rabbitLoaction, new Burrow(world, p));
            theHoleLocation = rabbitLoaction;
            hasAHole = true;
        } else if(hasAHole){
            lookForFood();
            digHole();
        }
    }
}