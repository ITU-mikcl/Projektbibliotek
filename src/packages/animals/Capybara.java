package packages.animals;
import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import packages.terrain.Feces;

import java.awt.*;

/**
 * The Capybara class is an animal that can act on its own.
 */
public class Capybara extends Animal implements Actor {
    final String[] images = {"small-capybara", "small-capybara-sleeping", "capybara", "capybara-sleeping"};
    Object friend;
    protected int timeToNextReproduction;
    private Object droppedFeces;

    /**
     * The constructor initializes a new capybara as a normal animal with the initialized image
     * being of "small-capybara" and speed 2 hunger 10.
     *
     * @param world Wolrd
     * @param p Program
     */
    public Capybara(World world, Program p) {
        super(world, p, "small-capybara", 2, 10);
    }

    /**
     * This method can be used by everything
     * It handles how the Capybara acts and calls all functions that
     * dictate the Capybaras behaviour.
     * @param world providing details of the position on which the actor is currently located and much more.
     */
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

    /**
     * This method is used to handle the capybaras
     * behaviour during the day. It checks whether it has friends
     * and if not it looks for some. It also allows it to drop feces
     * before nighttime and eat it first thing next morning.
     * It also allows it to reproduce after 60 steps which increases with 60 after
     * every reproduction (after 1st reproduction it is 120 steps until 2nd)
     *
     */
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

    /**
     * This method takes care of removing hunger points from the partner
     * (Called from reproduce()) and also sets the partners delay until it can reproduce again
     * Which increases by 60 every reproduction
     * @param partner Animal
     */
    @Override
    protected void waitToReproduce(Animal partner) {
        Capybara capybaraPartner = (Capybara) partner;

        capybaraPartner.hunger -= 5;
        capybaraPartner.timeToNextReproduction += 60;
    }

    /**
     * This method handles the capybaras nighttime behaviour.
     * It makes the capybara drop feces as the first thing when it
     * becomes night if it hasn't already.
     */
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

    /**
     * This method handles how the capybara looks for friends.
     * It searches the entire world size for any Animal other than capybara.
     * It returns the object (animal)
     * @return Object (animal) of a friend it has found if there are any.
     */
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

    /**
     * This method returns an integer value that determines the state
     * the capybara is in.
     * State 0 is small and awake, 1 is small and sleeping.
     * State 2 is adult and awake and 3 is adult and sleeping.
     * @return
     */
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

    /**
     * This method handles the displayinformation (image and state of capybara)
     * @return DisplayInformation
     */
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.white, images[getState()]);
    }
}
