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

                        for(Location location : world.getSurroundingTiles(2)){
                            if(world.getTile(location) instanceof Wolf){
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
                                }else{lookForFood();}
                            }else{lookForFood();}
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

    public void eat(Grass grass) {
        hunger += 5;
        grass.decompose();
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

    private void lookForFood() {
        try {
            Object standingOn = world.getNonBlocking(world.getLocation(this));

            if (standingOn instanceof Grass) {
                Grass standingOnGrass = (Grass) standingOn;
                eat(standingOnGrass);
            } else {
                moveToLocation(myLocation, lookForNonBlocking(myLocation, Grass.class, sizeOfWorld));
            }
        } catch (IllegalArgumentException e) {
            moveToLocation(myLocation, lookForNonBlocking(myLocation, Grass.class, sizeOfWorld));
        }
    }

    private void lookForHole() {
        if (burrowLocation == null) {
            burrowLocation = lookForAnyBlocking(myLocation, Burrow.class, 3);

            if (burrowLocation == null) {
                if (world.containsNonBlocking(myLocation)) {
                    lookForFood();
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