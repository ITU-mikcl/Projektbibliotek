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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Wolf extends FossorialAnimals implements Actor {
    private Animal prey;
    private Carcass carcass;
    private boolean isLeader;
    private final int myPack;
    String[] images = {"wolf-small", "wollfl-small-sleeping", "wolf", "wolf-sleeping"};
    Wolf newWolf = null;
    private HashMap<Wolf, Boolean> allWolvesInMyPack;
    private Wolf leader;

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

        allWolvesInMyPack = getAllWolvesInMyPack();

        fightOpposingWolf();

        if (isLeader) {
            leaderAct();
        } else {
            if (world.isOnTile(leader)) {
                wolfAct();
            }
        }
    }

    private void nightAct() {
        if (world.isOnTile(this)) {
            getToBurrow();
        } else if (isAdult && isLeader && allWolvesInMyPack.size() >= 2 && hunger >= 5) {
            reproduce();
        }
    }

    private void wolfAct() {
        if (leader != null) {
            followLeader();
        } else {
            isLeader = true;
        }
    }

    private void leaderAct() {
        spawnNewWolf();

        if (hunger <= 10) {
            if (allWolvesInMyPack.size() >= 5) {
                huntBear();
            } else {
                lookForFood();
            }

            if (carcass != null) {
                eatCarcass();
            } else if (prey != null) {
                if (world.isOnTile(prey)) {
                    prey = killPrey(prey);
                }
            }
        }
    }

    private void spawnNewWolf() {
        if (newWolf != null) {
            Spawner.spawnWolf(burrowLocation, burrowLocation.getX(), burrowLocation.getY(), Spawner.size, false);
            newWolf = null;
        }
    }

    private void fightOpposingWolf() {
        for (Location surroundingTile : world.getSurroundingTiles()) {
            Object opposingWolf = world.getTile(surroundingTile);
            if (opposingWolf instanceof Wolf && !allWolvesInMyPack.containsKey(opposingWolf)) {
                ((Wolf) opposingWolf).die();
            }
        }
    }

    private void huntBear() {
        Bear targetBear = lookForBear();
        if (targetBear != null) {
            prey = targetBear;
        }
    }

    private Bear lookForBear() {
        try {
            return (Bear) world.getTile(lookForBlocking(Bear.class));
        } catch (NullPointerException e) {
            return null;
        }
    }

    private void lookForFood() {
        carcass = (Carcass) lookForMeat(Carcass.class);
        if (prey == null) {
            prey = (Rabbit) lookForMeat(Rabbit.class);
        }
    }

    private void eatCarcass() {
        Location carcassLocation = world.getLocation(carcass);
        if (world.getSurroundingTiles().contains(carcassLocation)) {
            changeHungerForPack(eat(carcass));
            carcass = null;
        } else {
            moveToLocation(world.getEmptySurroundingTiles(carcassLocation).iterator().next());
        }
    }

    private void changeHungerForPack(int hungerAmount) {
        for (Wolf wolf : allWolvesInMyPack.keySet()) {
            wolf.changeHunger(hungerAmount);
        }
    }

    private void changeHunger(int hungerAmount) {
        hunger += (hungerAmount);
    }

    private void followLeader() {
        for (Map.Entry<Wolf, Boolean> entry : allWolvesInMyPack.entrySet()) {
            if (entry.getValue()) {
                Set<Location> wolfLeaderEmptySurroundingTiles = world.getEmptySurroundingTiles(world.getLocation(entry.getKey()));
                if (!wolfLeaderEmptySurroundingTiles.isEmpty()) {
                    for (Location targetLocation : wolfLeaderEmptySurroundingTiles) {
                        moveToLocation(targetLocation);
                        return;
                    }
                }
            }
        }
    }

    private Set<Wolf> getAllWolves() {
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

    private HashMap<Wolf, Boolean> getAllWolvesInMyPack() {
        HashMap<Wolf, Boolean> allWolves = new HashMap<>();

        for (Wolf wolf : getAllWolves()) {
            if (wolf.myPack == myPack) {
                allWolves.put(wolf, wolf.isLeader);

                if (wolf.isLeader) {
                    leader = wolf;
                }
            }
        }

        return allWolves;
    }

    @Override
    public void lookForBurrow() {
        if (burrowLocation == null) {
            if (!world.containsNonBlocking(myLocation)) {
                world.setTile(myLocation, new Burrow(world, p, "hole"));
                burrowLocation = myLocation;

                for (Wolf wolf : allWolvesInMyPack.keySet()) {
                    wolf.burrowLocation = burrowLocation;
                }
            }
        } else {
            moveToLocation(burrowLocation);
        }
    }

    private void reproduce() {
        newWolf = new Wolf(world, p, false, myPack);
        Spawner.getMyWolfpack(myPack).addWolf(newWolf);
        newWolf.burrowLocation = burrowLocation;
        hunger -= 5;
        changeHungerForPack(-5);
    }
}