package packages.animals;
import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import packages.terrain.Feces;

import java.awt.*;
public class Capybara extends Animal implements Actor {
    final String[] images = {"small-capybara", "small-capybara-sleeping", "capybara", "capybara-sleeping"};
    Object friend;
    protected int timeToNextReproduction;
    private Object droppedFeces;
    public Capybara(World world, Program p) {
        super(world, p, "small-capybara", 2, 10);
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
        adultCheck();

        if (friend == null) {
            friend = lookForFriend();
        }

        if (droppedFeces != null) {
            eat(droppedFeces);
            droppedFeces = null;
        } else if (hunger < 5) {
            lookForGrass();
        } else if (stepsSinceSpawned >= timeToNextReproduction && isAdult) {
            timeToNextReproduction += 60;
            reproduce("Capybara");
        } else if (friend != null){
            try{
                if(world.isOnTile(friend)){
                    moveToLocation(world.getLocation(friend));
                }
            }catch (IllegalArgumentException e){

            }
        }
    }

    @Override
    protected void waitToReproduce(Animal partner) {
        Capybara capybaraPartner = (Capybara) partner;

        capybaraPartner.hunger -= 5;
        capybaraPartner.timeToNextReproduction += 60;
    }

    private void nightAct() {
        if (droppedFeces == null) {
            for (Location tile : world.getSurroundingTiles()) {
                if (!world.containsNonBlocking(tile)) {
                    world.setTile(tile, new Feces(world, p));
                    droppedFeces = world.getNonBlocking(tile);
                    break;
                }
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
