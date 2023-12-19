package packages.terrain;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import packages.SpawnableObject;

import java.awt.*;

/**
 * This class is responsible for handling a Carcass. The class is a SpawnableObject and implements actor.
 */
public class Carcass extends SpawnableObject implements Actor, DynamicDisplayInformationProvider {
    int stepsSinceSpawned;
    String carcassImage;
    String fungiImage;

    /**
     * The Carcass constructor initializes a Carcass with an image of a Carcass and fungi
     * that it will later spawn
     * @param world World
     * @param p Program
     * @param carcassImage String name of which image of a carcass is displayed
     * @param fungiImage String name of which image of a fungi is displayed
     */
    public Carcass(World world, Program p, String carcassImage, String fungiImage){
        super(world,p,carcassImage);
        this.carcassImage = carcassImage;
        this.fungiImage = fungiImage;
    }

    /**
     * This method is responsible for how a carcass must act. It comes from the actor interface.
     * It is also responsible for creating a fungi in its place after 60 steps. (If it hasn't been eaten)
     * @param world World
     */
    public void act(World world) {
        stepsSinceSpawned++;

        if (stepsSinceSpawned % 60 == 0) {
            Location myLocation = world.getLocation(this);
            world.delete(this);
            Fungi currentFungi =  new Fungi(world, p, fungiImage, myLocation);
            world.setTile(myLocation, currentFungi);
            world.remove(currentFungi);
        }
    }

    /**
     * This method returns the image of a big Carcass if isBig is true
     * @return large carcass image
     */
    public boolean isBig(){
        return carcassImage.equals("carcass");
    }

    /**
     * Displaying the carcass image
     * @return image.
     */
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.white, carcassImage);
    }
}