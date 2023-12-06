package packages.animals;

import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import packages.terrain.Grass;
import packages.terrain.Burrow;

import java.awt.*;
import java.util.Set;

public class Rabbit extends Animal implements Actor {
    boolean isBeingChased = false;
    final String[] images = {"rabbit-small", "rabbit-small-sleeping", "rabbit-large", "rabbit-sleeping"};
    public Rabbit(World world, Program p) {
        super(world, p, "rabbit-small", 2, 10);
    }

    @Override
    public void act(World world) {

        hunger = statusCheck(this, hunger);

        if (!isDead()) {
            if (canIAct()) {
                if (world.isDay()) {
                    if (!world.isOnTile(this)) {
                        world.setTile(getEmptyTile(burrowLocation), this);
                    } else {
                        myLocation = world.getLocation(this);
                        Object predator;
                        for(Location location : world.getSurroundingTiles(2)){
                            predator = world.getTile(location);
                            if(predator instanceof Wolf || predator instanceof Bear){
                                moveTo(this, nextOppositeTile(myLocation, location, world.getSurroundingTiles()));
                                isBeingChased = true;
                                break;
                            }
                            isBeingChased = false;
                        }
                        if (!isBeingChased){
                            if (isAdult() && hunger >= 5) {
                                Location partnerLocation = lookForBlocking(myLocation, Rabbit.class);
                                if (partnerLocation != null){
                                    lookForPartner(partnerLocation);
                                }else{lookForGrass(myLocation);}
                            }else{lookForGrass(myLocation);}
                        }
                    }
                } else {
                    if (world.isOnTile(this)) {
                        myLocation = world.getLocation(this);

                        if (!isOnBurrow(burrowLocation, myLocation)){
                            lookForHole();
                        } else {
                            world.remove(this);
                        }
                    }
                }
            }
        }
    }
    private void lookForPartner(Location partnerLocation) {
        for(Location surroundingTile : world.getSurroundingTiles()){
            if(surroundingTile.hashCode() == partnerLocation.hashCode()){
                reproduce();
                return;
            }
        }
        moveToLocation(myLocation, partnerLocation);
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
            burrowLocation = lookForAnyBlocking(myLocation, Burrow.class, 3);

            if (burrowLocation == null) {
                if (world.containsNonBlocking(myLocation)) {
                    lookForGrass(myLocation);
                } else {
                    world.setTile(myLocation, new Burrow(world, p, "hole-small"));
                    burrowLocation = myLocation;
                }
            }
        } else {
            moveToLocation(myLocation, burrowLocation);
            myLocation = world.getLocation(this);
        }
    }

    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.white, images[getState(isOnBurrow(burrowLocation, myLocation))]);
    }
}