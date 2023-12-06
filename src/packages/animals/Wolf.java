package packages.animals;

import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;

public class Wolf extends Animal implements Actor {
    private Location myLocation;
    private int hunger = 5;
    private Rabbit prey;
    private final boolean isLeader;
    private final int myPack;
    String[] images = {"wolf-small", "wollfl-small-sleeping", "wolf", "wolf-sleeping"};

    public Wolf(World world, Program p, boolean isLeader, int myPack){
        super(world,p,"wolf-small", 4);
        this.isLeader = isLeader;
        this.myPack = myPack;
    }

    public DisplayInformation getInformation() {
        //return new DisplayInformation(Color.white, images[super.getState()]);
        return new DisplayInformation(Color.white, images[0]);
    }

    public void act(World world) {
        hunger = super.statusCheck(this, hunger);

        if (world.isDay()) {

            if (super.canIAct()) {
                myLocation = world.getLocation(this);
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
        }

    }

    private void lookForPrey(){
        try {
            prey = (Rabbit) world.getTile(super.lookForBlocking(myLocation, Rabbit.class));
        } catch (NullPointerException e) {

        }
    }

    private void killPrey(Rabbit prey) {
        super.die(prey);
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