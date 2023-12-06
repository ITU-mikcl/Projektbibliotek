package packages.animals;

import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class Wolf extends Animal implements Actor {
    private Location myLocation;
    private int hunger = 15;
    private Rabbit prey;
    private boolean isLeader;
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
            if (canIAct()) {
                if (world.isOnTile(this)){
                    myLocation = world.getLocation(this);
                }
                if (hunger <= 10 && isLeader) {
                    lookForPrey();
                    if (prey != null) {
                        killPrey(prey);
                    }
                } else {
                    if(doesPackHaveALeader()){
                        followLeader();
                    }else {
                        isLeader = true;
                    }
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

    private boolean doesPackHaveALeader(){
        for (Wolf wolf : getAllWolves()) {
            if (wolf.isThisTheLeaderOfMyPack(itsPack())) {
                return true;
            }
        }
        return false;
    }

    private void followLeader() {
        outerLoop:
        for (Wolf wolf : getAllWolves()) {
            if (!world.getEmptySurroundingTiles(world.getLocation(wolf)).isEmpty() && wolf.isThisTheLeaderOfMyPack(itsPack())) {
                for (Location partnerLocationEmptySurroundingTile : world.getEmptySurroundingTiles(world.getLocation(wolf))){
                    moveToLocation(myLocation, partnerLocationEmptySurroundingTile);
                    break outerLoop;
                }
            }
        }
    }

    private Set<Wolf> getAllWolves(){
        Object tile;
        Set<Wolf> allWolves = new HashSet<>();
        for (Location targetLocation : world.getSurroundingTiles(myLocation, sizeOfWorld)) {
            tile = world.getTile(targetLocation);
            if (tile instanceof Wolf) {
                allWolves.add((Wolf) tile);
            }
        }
        return allWolves;
    }
}