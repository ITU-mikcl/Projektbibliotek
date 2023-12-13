
package packages.animals;

import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import packages.Spawner;
import packages.terrain.Burrow;
import packages.terrain.Carcass;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Wolf extends Animal implements Actor {
    private Animal prey;

    private Carcass carcass;
    private boolean isLeader;
    private final int myPack;
    String[] images = {"wolf-small", "wollfl-small-sleeping", "wolf", "wolf-sleeping"};
    Wolf newWolf = null;

    public Wolf(World world, Program p, boolean isLeader, int myPack) {
        super(world, p, "wolf-small", 2, 15);
        this.isLeader = isLeader;
        this.myPack = myPack;
    }

    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.white, images[getState(isOnBurrow(burrowLocation))]);
    }

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

    private void dayAct() {
        if (burrowLocation != null) {
            wakeUp();
        }

        if (isLeader) {
            if (newWolf != null) {
                Spawner.spawnWolf(burrowLocation, burrowLocation.getX(), burrowLocation.getY(), Spawner.size, 1);
                newWolf = null;
            }
            Set<Wolf> allWolvesInPack = getAllWolvesInMyPack(1);
            for (Wolf wolf : getAllWolves(1)) {
                if (!allWolvesInPack.contains(wolf)) {
                    fightWolf(wolf);
                    break;
                }
            }
            if (hunger <= 10) {
                if (allWolvesInPack.size() >= 5) {
                    Bear targetBear = lookForBear();
                    if (targetBear != null) {
                        prey = targetBear;
                    }
                } else {
                    carcass = wolfLookForCarcass();
                    if (prey == null) {
                        prey = lookForPrey();
                    }
                }
                if (carcass != null) {
                    eatCarcass();
                } else if (prey != null) {
                    killPrey();
                }
            }
        } else {
            if (doesPackHaveALeader()) {
                followLeader();
            } else {
                isLeader = true;
            }
        }
    }

    private void nightAct() {
        if (world.isOnTile(this)) {
            myLocation = world.getLocation(this);

            if (!isOnBurrow(burrowLocation)){
                lookForHole();
            } else {
                world.remove(this);
            }
        }
    }

    private Bear lookForBear() {
        try {
            return (Bear) world.getTile(lookForBlocking(Bear.class));
        } catch (NullPointerException e) {
            return null;
        }
    }

    private void killPrey() {
        try {
            Location anyBlockingLocation = lookForAnyBlocking(prey.getClass(), 1);
            Set<Location> surroundingTiles = world.getSurroundingTiles(myLocation);

            for (Location surroundingTile : surroundingTiles) {
                if (anyBlockingLocation != null) {
                    if (surroundingTile.hashCode() == anyBlockingLocation.hashCode()) {
                        prey.die();
                        prey = null;
                        return;
                    }
                }
            }
            moveToLocation(lookForBlocking(prey.getClass()));
        } catch (NullPointerException ignore) {
        }
    }

    private void eatCarcass() {
        try {
            Location anyBlockingLocation = lookForAnyBlocking(carcass.getClass(), 1);
            Set<Location> surroundingTiles = world.getSurroundingTiles(myLocation);

            for (Location surroundingTile : surroundingTiles) {
                if (anyBlockingLocation != null) {
                    if (surroundingTile.hashCode() == anyBlockingLocation.hashCode()) {
                        eat(carcass);
                        changeHungerForPack(10);
                        carcass = null;
                        return;
                    }
                }
            }
            moveToLocation(lookForBlocking(prey.getClass()));
        } catch (NullPointerException ignore) {
        }
    }

    private void changeHungerForPack(int hungerAmount) {
        Object theTarget;
        changeHunger(hungerAmount);
        for (Location targetLocation : world.getSurroundingTiles(myLocation, sizeOfWorld)) {
            theTarget = world.getTile(targetLocation);
            if (theTarget instanceof Wolf && ((Wolf) theTarget).itsPack() == myPack) {
                ((Wolf) theTarget).changeHunger(hungerAmount);
            }
        }
    }

    private void changeHunger(int hungerAmount) {
        hunger += (hungerAmount);
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

    private boolean doesPackHaveALeader() {
        for (Wolf wolf : getAllWolves(sizeOfWorld)) {
            if (wolf.isThisTheLeaderOfMyPack(itsPack())) {
                return true;
            }
        }
        return false;
    }

    private void followLeader() {
        outerLoop:
        for (Wolf wolf : getAllWolves(sizeOfWorld)) {
            if (!world.getEmptySurroundingTiles(world.getLocation(wolf)).isEmpty() && wolf.isThisTheLeaderOfMyPack(itsPack())) {
                for (Location partnerLocationEmptySurroundingTile : world.getEmptySurroundingTiles(world.getLocation(wolf))) {
                    moveToLocation(partnerLocationEmptySurroundingTile);
                    break outerLoop;
                }
            }
        }
    }

    private Set<Wolf> getAllWolves(int radius) {
        Object tile;
        Set<Wolf> allWolves = new HashSet<>();
        for (Location targetLocation : world.getSurroundingTiles(myLocation, radius)) {
            tile = world.getTile(targetLocation);
            if (tile instanceof Wolf) {
                allWolves.add((Wolf) tile);
            }
        }
        return allWolves;
    }

    private Set<Wolf> getAllWolvesInMyPack(int radius) {
        Set<Wolf> allWolves = new HashSet<>();

        for (Wolf wolf : getAllWolves(radius)) {
            if (wolf.itsPack() == myPack) {
                allWolves.add(wolf);
            }
        }

        return allWolves;
    }

    private void lookForHole() {
        if (burrowLocation == null) {
            if (!world.containsNonBlocking(myLocation)) {
                world.setTile(myLocation, new Burrow(world, p, "hole"));
                burrowLocation = myLocation;

                for (Wolf wolf : getAllWolvesInMyPack(sizeOfWorld)) {
                    wolf.burrowLocation = burrowLocation;
                }
            }
        } else {
            moveToLocation(burrowLocation);
            myLocation = world.getLocation(this);
        }
    }

    private void reproduce() {
        if (isAdult && hunger >= 5 && getAllWolvesInMyPack(sizeOfWorld).size() > 2) {
            newWolf = new Wolf(world, p, false, myPack);
            Spawner.getMyWolfpack(myPack).addWolf(newWolf);
            newWolf.burrowLocation = burrowLocation;
            changeHungerForPack(-5);
        }
    }

    private void fightWolf(Wolf wolf) {
        wolf.die();
    }
}
