package packages.animals;

import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;
import java.util.Set;

public class Wolf extends Animal implements Actor {
    private Location myLocation;
    private int hunger = 15;
    private Rabbit prey;
    private final boolean isLeader;
    private final int myPack;
    String[] images = {"wolf-small", "wollfl-small-sleeping", "wolf", "wolf-sleeping"};

    public Wolf(World world, Program p, boolean isLeader, int myPack){
        super(world,p,"wolf-small", 2);
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
                if (hunger <= 10 && isLeader) {
                    lookForPrey();
                    if (prey != null) {
                        killPrey(prey);
                    }
                } else {
                    followLeader();
                }
            }
        }

    }

    private void lookForPrey(){
        try {
            prey = (Rabbit) world.getTile(lookForBlocking(myLocation, Rabbit.class));
        } catch (NullPointerException e) {

        }
    }

    private void killPrey(Object prey) {
        try{
            Location anyBlockingLocation = lookForAnyBlocking(myLocation, prey.getClass(), 1);
            Set<Location> surroundingTiles = world.getSurroundingTiles(myLocation);
            for(Location surroundingTile : surroundingTiles) {
                if (anyBlockingLocation != null) {
                    if (surroundingTile.hashCode() == anyBlockingLocation.hashCode()) {
                        die(prey);
                        increaseHungerForPack();
                        this.prey = null;
                        return;
                    }
                }
            }
            moveToLocation(myLocation,lookForBlocking(myLocation, prey.getClass()));
        }catch (NullPointerException ignore){}
    }

    private void increaseHungerForPack(){
        Object theTarget;
        increaseHunger();
        for (Location targetLocation : world.getSurroundingTiles(myLocation, sizeOfWorld)) {
            theTarget = world.getTile(targetLocation);
            if (theTarget instanceof Wolf && ((Wolf) theTarget).itsPack() == myPack) {
                ((Wolf) theTarget).increaseHunger();
            }
        }
    }
    private void increaseHunger(){
        hunger += 10;
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
        for (Location targetLocation : world.getSurroundingTiles(myLocation, sizeOfWorld)) {
            theTarget = world.getTile(targetLocation);
            if (theTarget instanceof Wolf && !world.getEmptySurroundingTiles(targetLocation).isEmpty() && ((Wolf) theTarget).isThisTheLeaderOfMyPack(itsPack())) {
                for (Location partnerLocationEmptySurroundingTile : world.getEmptySurroundingTiles(targetLocation)){
                    moveToLocation(myLocation, partnerLocationEmptySurroundingTile);
                    break outerLoop;
                }
            }
        }
    }
}