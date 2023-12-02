package packages.animals;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import packages.SpawnableObjects;

import java.awt.*;

public class Wolf extends Animal implements Actor, DynamicDisplayInformationProvider {
    int sizeOfWorld;
    private int stepsSinceSpawned;
    private int speed = 2;
    private Location myLocation;
    private int hunger = 5;
    private Rabbit prey;
    private boolean isLeader;
    private int myPack;

    String[] images = {"wolf-small", "wolf-large", "wollfl-small-sleeping", "wolf-sleeping"};
    int growthState = 0;

    public Wolf(World world, Program p, boolean isLeader, int myPack){
        super(world,p,"wolf-small");
        this.stepsSinceSpawned = world.getCurrentTime();
        this.sizeOfWorld = world.getSize();
        this.isLeader = isLeader;
        this.myPack = myPack;
    }

    public void kill(){
        world.delete(this);
    }

    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.white, images[growthState]);
    }

    public void act(World world) {
        stepsSinceSpawned++;

        if (world.isDay()) {
            if (growthState == 2) {
                growthState = 0;
                getInformation();
            }

            if (stepsSinceSpawned % speed == 0) {
                myLocation = world.getLocation(this);
                hunger--;

                if (hunger <= 2 && isLeader) {
                    lookForPrey();
                } else {
                    followLeader();
                }
            } else {
                if (prey != null) {
                    killPrey(prey);
                }
            }
        } else {
            if (growthState == 0) {
                growthState = 2;
                getInformation();
            }
        }

    }



    private void lookForPrey(){
        try {
            prey = (Rabbit) world.getTile(super.lookForBlocking(myLocation, Rabbit.class));
        } catch (NullPointerException e) {

        }
    }

    private void killPrey(Rabbit prey) {
        prey.kill();
        hunger += 10;
        this.prey = null;
    }

    private int itsPack() {
        return myPack;
    }

    private boolean isThisTheLeaderOfMyPack(int itsPack) {
        if (myPack == itsPack) {
            return isLeader;
        }

        return false;
    }

    private void followLeader() {
        Object theTarget;

        outerLoop:
        for (int i = 1; i < sizeOfWorld; i++) {
            for (Location targetLocation: world.getSurroundingTiles(myLocation, i)) {
                theTarget = world.getTile(targetLocation);
                if (theTarget instanceof Wolf && !world.getEmptySurroundingTiles(targetLocation).isEmpty() && ((Wolf) theTarget).isThisTheLeaderOfMyPack(itsPack())) {
                    for (Location partnerLocationEmptySurroundingTile: world.getEmptySurroundingTiles(targetLocation)){
                        world.move(this,partnerLocationEmptySurroundingTile);
                        break outerLoop;
                    }
                }
            }
        }
    }
}