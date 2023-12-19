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

/**
 * The Wolf class is a fossorial animal that can act on its own.
 */
public class Wolf extends FossorialAnimals implements Actor {
    private Animal prey;
    private Carcass carcass;
    private boolean isLeader;
    private final WolfPack myPack;
    String[] images = {"wolf-small", "wollfl-small-sleeping", "wolf", "wolf-sleeping"};
    Wolf newWolf = null;
    private HashMap<Wolf, Boolean> allWolvesInMyPack;
    private Wolf leader;

    /**
     * The constructor initializes a new wolf as a fossorial animal with
     * an added boolean isLeader and WolfPack mypack.
     * @param world World
     * @param p Program
     * @param isLeader boolean value that determines whether the wolf is a leader
     * @param myPack WolfPack
     */
    public Wolf(World world, Program p, boolean isLeader, WolfPack myPack) {
        super(world, p, "wolf-small", 2, 15);
        this.isLeader = isLeader;
        this.myPack = myPack;
    }

    /**
     * getInformation returns a DisplayInformation with a wolf image according to its state and
     * whether it has a burrow.
     * @return DisplayInformation
     */
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.white, images[getState(isOnBurrow(burrowLocation))]);
    }

    /**
     * The act method comes from the Actor interfacce and is responsible
     * for handling the overall wolfs behaviour by calling other methods.
     * @param world providing details of the position on which the actor is currently located and much more.
     */
    public void act(World world) {
        hunger = getHunger(hunger);
        System.out.println("GET ALL WOLVES: "+getAllWolves());
        System.out.println("In pack: "+getAllWolvesInMyPack());

        if (canIAct()) {
            if (world.isDay()) {
                dayAct();
            } else {
                nightAct();
            }
        }
    }

    /**
     * This method handles the wolfs daytime behaviour by calling
     * wolfs other methods accordingly such as wakeUp and fightOpposingwolf.
     * If it is a leader the leaderAct method will be called.
     */
    private void dayAct() {
        if (burrowLocation != null) {
            wakeUp();
        }

        allWolvesInMyPack = getAllWolvesInMyPack();

        fightOpposingWolf();

        if (isLeader) {
            leaderAct();
        } else {
            try {
                if (world.isOnTile(leader)) {
                    wolfAct();
                }
            } catch (IllegalArgumentException e) {

            }

        }
    }

    /**
     * This method handles the wolfs nighttime behaviour.
     * If it is still awake it will return to the wolfpacks burrow.
     * And if its an adult and a leader and there is another wolf in the pack
     * while the hunger level is more than 10 it will reproduce.
     */
    private void nightAct() {
        if (world.isOnTile(this)) {
            getToBurrow();
        } else if (isAdult && isLeader && allWolvesInMyPack.size() >= 2 && hunger >= 10) {
            reproduce();
        }
    }

    /**
     * This method is responsible for determening whether
     * the wolf is a leader. If it is not it will follow the
     * leader. Else it will itself become the leader.
     */
    private void wolfAct() {
        if (leader != null) {
            followLeader();
        } else {
            isLeader = true;
        }
    }

    /**
     * This method handles the logic behind a leader
     * wolfs behaviour. Firstly a new nonleader wolf is spawned
     * Then if its wolfpack is greater or equal to 5 the pack will
     * hunt a bear, else they will look for food.
     * If there is a carcass somewhere the pack will eat those instead.
     * Else if there is prey somewhere the pack will hunt it.
     */
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
                try {
                    if (world.isOnTile(prey)) {
                        prey = killPrey(prey);
                    }
                } catch (IllegalArgumentException e) {

                }
            }
        }
    }

    /**
     * This method spawns a new nonleader wolf.
     */
    private void spawnNewWolf() {
        if (newWolf != null) {
            Spawner.spawnWolf(burrowLocation, Spawner.size, false, myPack);
            newWolf = null;
        }
    }

    /**
     * This method checks the surrounding tiles for wolves.
     * If there is a wolf and not all wolves are opposingWolves
     * the wolf will kill the opposing wolf.
     */
    private void fightOpposingWolf() {
        for (Location surroundingTile : world.getSurroundingTiles()) {
            Object opposingWolf = world.getTile(surroundingTile);
            if (opposingWolf instanceof Wolf && !allWolvesInMyPack.containsKey(opposingWolf)) {
                ((Wolf) opposingWolf).die();
            }
        }
    }

    /**
     * This method when called makes the pack hunt a bear
     * if there is any.
     */
    private void huntBear() {
        Bear targetBear = lookForBear();
        if (targetBear != null) {
            prey = targetBear;
        }
    }

    /**
     * This method returns the Bear Animal.
     * @return Bear
     */
    private Bear lookForBear() {
        try {
            return (Bear) world.getTile(lookForBlocking(Bear.class));
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * This method looks for food, firstly it looks for carcasses
     * if there isn't any it there is no prey already, it will
     * look for rabbits.
     */
    private void lookForFood() {
        carcass = (Carcass) lookForMeat(Carcass.class);
        if (prey == null) {
            prey = (Rabbit) lookForMeat(Rabbit.class);
        }
    }

    /**
     * This method gets the location of a carcass and
     * if the carcass is on one of the surrounding tiles
     * the whole wolfpack will eat, and their hunger level
     * changed accordingly.
     * If it itsn't nearby they will move to the location of a carcass.
     */
    private void eatCarcass() {
        Location carcassLocation = world.getLocation(carcass);
        if (world.getSurroundingTiles().contains(carcassLocation)) {
            changeHungerForPack(eat(carcass));
            carcass = null;
        } else {
            moveToLocation(world.getEmptySurroundingTiles(carcassLocation).iterator().next());
        }
    }

    /**
     * This method changes the hunger for the entire wolfpack
     * it takes a paramter hungeramount which is an integer
     * that defines how much the hunger should be changed.
     * @param hungerAmount Integer value, how much the hunger value must change.
     */
    private void changeHungerForPack(int hungerAmount) {
        for (Wolf wolf : allWolvesInMyPack.keySet()) {
            wolf.changeHunger(hungerAmount);
        }
    }

    /**
     * This method changes the hunger for a single wolf.
     * @param hungerAmount Integer value, determines how much the hunger value must change.
     */
    private void changeHunger(int hungerAmount) {
        hunger += (hungerAmount);
    }

    /**
     * This method makes all the nonleader wolves
     * follow the leader at all time.
     */
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

    /**
     * This method returns a HashSet containing all wolves.
     * @return hashset of all wolf objects in the world
     */
    private Set<Wolf> getAllWolves() {
        Object tile;
        Set<Wolf> allWolves = new HashSet<>();

        allWolves.add(this);

        for (Location targetLocation : world.getSurroundingTiles(myLocation, sizeOfWorld)) {
            tile = world.getTile(targetLocation);
            if (tile instanceof Wolf) {
                allWolves.add((Wolf) tile);
            }
        }

        return allWolves;
    }

    /**
     * This method returns a hashmap of all wolves in the wolfpack and whether a particular wolf is a leader.
     * @return hashmap of all wolves in the pack and if each wolf is a leader.
     */
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

    /**
     * This method makes a wolf look for their burrow if it exists.
     * If it doesn't a new one will be created for the whole pack on
     * the wolfs location.
     */
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

    /**
     * This method allows a wolf to reproduce.
     */
    private void reproduce() {
        newWolf = new Wolf(world, p, false, myPack);
        myPack.addWolf(newWolf);
        newWolf.burrowLocation = burrowLocation;
        changeHungerForPack(-5);
    }
}