package packages.animals;
import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import packages.terrain.Burrow;
import packages.terrain.Carcass;

import java.awt.*;
import java.util.Set;

public class Capybara extends Animal implements Actor {
    final String[] images = {"small-capybara", "small-capybara-sleeping", "capybara", "capybara-sleeping"};
    Object friend;
    public Capybara(World world, Program p) {
        super(world, p, "small-capybara", 2, 10);
    }

    public void act(World world) {
        hunger = getHunger(hunger);

        if (canIAct()) {
            if (world.isDay()) {
                dayAct();
            }
        }

    }
    private void dayAct() {
        adultCheck();

        if (friend == null) {
            friend = lookForFriend();
        }

        if (hunger <= 5) {
            lookForGrass();
        } else if (friend != null){
            try{
                if(world.isOnTile(friend)){
                    moveToLocation(world.getLocation(friend));
                }
            }catch (IllegalArgumentException e){

            }
        }
    }

    private Object lookForFriend(){
        for (int i = 1; i < sizeOfWorld; i++) {
            for (Location targetLocation : world.getSurroundingTiles(myLocation, i)) {
                Object targetObject = world.getTile(targetLocation);

                if (targetObject instanceof Animal && !(targetObject instanceof Capybara)) {
                    return targetObject;
                }
            }
        }

        return null;
    }


    protected int getState() {
        if(isAdult) {
            if (world.isNight()) {
                return 3;
            } else {
                return 2;
            }
        } else if (world.isNight()) {
            return 1;
        } else {
            return 0;
        }
    }
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.white, images[getState()]);
    }
}
