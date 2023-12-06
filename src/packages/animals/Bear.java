package packages.animals;

import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import packages.terrain.Grass;

import java.awt.*;
import java.util.Set;

public class Bear extends Animal implements Actor {

    private Rabbit prey;
    final String[] images = {"bear-small", "bear-small-sleeping", "bear", "bear-sleeping"};
    Location myLocation;
    public Bear(World world, Program p){
        super(world, p, "bear-small", 2, 10);
    }
    public void act(World world){
        if (!isDead()) {
            if (canIAct()) {
                if (world.isDay()) {
                    if (!world.isOnTile(this)) {

                    }

                    myLocation = world.getLocation(this);

                    if (prey != null) {
                        killPrey();
                    } else {
                        if (lookForClosestFood(myLocation) instanceof Rabbit) {
                            prey = lookForPrey(myLocation);
                        } else {
                            lookForGrass(myLocation);
                        }
                    }
                }
            }
        }
    }

    private void killPrey() {
        try{
            Location anyBlockingLocation = lookForAnyBlocking(myLocation, prey.getClass(), 1);
            Set<Location> surroundingTiles = world.getSurroundingTiles(myLocation);

            for(Location surroundingTile : surroundingTiles) {
                if (anyBlockingLocation != null) {
                    if (surroundingTile.hashCode() == anyBlockingLocation.hashCode()) {
                        die(prey);
                        hunger += 5;
                        prey = null;
                        return;
                    }
                }
            }
            moveToLocation(myLocation,lookForBlocking(myLocation, prey.getClass()));
        }catch (NullPointerException ignore){}
    }




    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.white, images[getState(isOnBurrow(burrowLocation, myLocation))]);
    }
}
