package packages.animals;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import packages.SpawnableObjects;
import packages.terrain.Grass;
import packages.terrain.Hole;

import java.awt.*;

public class Rabbit extends SpawnableObjects implements Actor, DynamicDisplayInformationProvider {
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

    public Rabbit(World world, Program p){
        super(world,p,"rabbit-small");
        this.stepsSinceSpawned = world.getCurrentTime();
        this.sizeOfWorld = world.getSize();
    }

    @Override
    public void act(World world) {
        stepsSinceSpawned++;

        if(!isAdult && stepsSinceSpawned > 60){
            isAdult = true;
            speed = 5;

            growthState = 1;
            getInformation();
        }

        if(stepsSinceSpawned % speed == 0) {
            rabbitLoaction = world.getLocation(this);

            if (world.isDay()) {
                if (!isAdult) {
                    if(growthState == 2) {
                        growthState = 0;
                        getInformation();
                    }
                } else {
                    if(growthState == 3) {
                        growthState = 1;
                        getInformation();
                    }
                }

                if (isAdult && hunger >= 5) {
                    lookForPartner();
                } else{
                    lookForFood();
                }
            } else {
                lookForHole();

                if (!isAdult) {
                    if(growthState == 0) {
                        growthState = 2;
                        getInformation();
                    }
                } else {
                    if(growthState == 1) {
                        growthState = 3;
                        getInformation();
                    }
                }
            }

        }

        if (stepsSinceSpawned % 20 == 0) {
            if (hunger == 0) {
                killRabbit();
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

    public void killRabbit(){
        world.delete(this);
    }
    private void reproduce(){
        for (Location birthLocation: world.getEmptySurroundingTiles()){
            world.setTile(birthLocation, new Rabbit(world,p));
            hunger-=3;
            break;
        }
    }
    private void lookForPartner(){
            outerloop:
            for (int i = 1; i < sizeOfWorld; i++) {
                for (Location partnerLocation: world.getSurroundingTiles(rabbitLoaction, i)) {
                    if (world.getTile(partnerLocation) instanceof Rabbit && !world.getEmptySurroundingTiles(partnerLocation).isEmpty()) {
                        for (Location partnerLocationEmptySurroundingTile: world.getEmptySurroundingTiles(partnerLocation)){
                            world.move(this,partnerLocationEmptySurroundingTile);
                            break;
                        }
                        reproduce();
                        break outerloop;
                    }
                }
           }
    }
    private void lookForFood(){
        try {
            Object standingOn = world.getNonBlocking(world.getLocation(this));

            if (standingOn instanceof Grass) {
                Grass standingOnGrass = (Grass) standingOn;
                eat(standingOnGrass);
            } else {
                findGrass();
            }
        } catch (IllegalArgumentException e) {
            findGrass();
        }
    }

    private void findGrass() {
        outloop:
        for (int i = 1; i < sizeOfWorld; i++) {
            for (Location grassLocation : world.getSurroundingTiles(rabbitLoaction, i)) {
                if (world.containsNonBlocking(grassLocation)
                        && world.getNonBlocking(grassLocation) instanceof Grass
                        && world.isTileEmpty(grassLocation)) {
                    world.move(this, grassLocation);
                    break outloop;
                }
            }
        }
    }

    private void lookForHole() {
        if (!hasAHole) {
            outloop:
            for (int i = 1; i < 3; i++) {
                for (Location holeLocation : world.getSurroundingTiles(rabbitLoaction, i)) {
                    if (world.containsNonBlocking(holeLocation)
                            && world.getNonBlocking(holeLocation) instanceof Hole
                            && world.isTileEmpty(holeLocation)) {
                        world.move(this, holeLocation);
                        theHoleLocation = holeLocation;
                        hasAHole = true;
                        break outloop;
                    }
                }
            }

            if (!hasAHole) {
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
            world.setTile(rabbitLoaction, new Hole(world, p));
            theHoleLocation = rabbitLoaction;
            hasAHole = true;
        }
    }
}