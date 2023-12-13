package packages.animals;

import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import packages.terrain.Burrow;
import packages.terrain.Carcass;

import java.awt.*;

public class Rabbit extends Animal implements Actor {
    boolean isBeingChased = false;
    final String[] images = {"rabbit-small", "rabbit-small-sleeping", "rabbit-large", "rabbit-sleeping"};
    public Rabbit(World world, Program p) {
        super(world, p, "rabbit-small", 2, 10);
    }

    @Override
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

        Object predator;
        for(Location location : world.getSurroundingTiles(2)){
            predator = world.getTile(location);
            if(predator instanceof Wolf || predator instanceof Bear){
                moveTo(nextOppositeTile(location));
                isBeingChased = true;
                break;
            }
            isBeingChased = false;
        }

        if (!isBeingChased){
            if (isAdult && hunger >= 5) {
                Location partnerLocation = lookForBlocking(Rabbit.class);
                if (partnerLocation != null){
                    lookForPartner(partnerLocation);
                    return;
                }
            }

            lookForGrass();
        }
    }

    private void nightAct() {
        if (world.isOnTile(this)) {
            if (!isOnBurrow(burrowLocation)){
                lookForHole();
            } else {
                world.remove(this);
            }
        }
    }

    @Override
    protected void die() {
        isDead = true;
        world.delete(this);
        Carcass carcass = new Carcass(world,p,"carcass-small");
        world.setTile(myLocation,carcass);
    }

    private Location nextOppositeTile(Location targetLocation) {
        double maxDistance = Double.MIN_VALUE;
        Location tileToMoveTo = myLocation;
        double surroundingDistance;

        for (Location tile : world.getEmptySurroundingTiles()){
            surroundingDistance = calculateDistance(tile, targetLocation);

            if (surroundingDistance > maxDistance) {
                maxDistance = surroundingDistance;
                tileToMoveTo = tile;
            }
        }
        return tileToMoveTo;
    }

    private void lookForPartner(Location partnerLocation) {
        for(Location surroundingTile : world.getSurroundingTiles()){
            if(surroundingTile.hashCode() == partnerLocation.hashCode()){
                reproduce();
                return;
            }
        }
        moveToLocation(partnerLocation);
    }

    private void reproduce() {
        for (Location birthLocation : world.getEmptySurroundingTiles()) {
            world.setTile(birthLocation, new Rabbit(world, p));
            hunger -= 5;
            break;
        }
    }

    private void lookForHole() {
        if (burrowLocation == null) {
            burrowLocation = lookForAnyBlocking(Burrow.class, 3);

            if (burrowLocation == null) {
                if (world.containsNonBlocking(myLocation)) {
                    lookForGrass();
                } else {
                    world.setTile(myLocation, new Burrow(world, p, "hole-small"));
                    burrowLocation = myLocation;
                }
            }
        } else {
            moveToLocation(burrowLocation);
        }
    }

    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.white, images[getState(isOnBurrow(burrowLocation))]);
    }
}