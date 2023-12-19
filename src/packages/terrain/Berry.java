package packages.terrain;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.World;
import packages.SpawnableObject;

import java.awt.*;

/**
 * A berry is a Spawnable Object that implements the Actor interface.
 */
public class Berry extends SpawnableObject implements Actor, DynamicDisplayInformationProvider {
    final String[] images = {"bush", "bush-berries"};
    private int stepsSinceSpawned = 0;
    public boolean hasBerries = true;

    /**
     * The Berry classes constructor initializes the class from Spawnable objects with the "bush" image
     * @param world World
     * @param p Program
     */
    public Berry(World world, Program p) {
        super(world,p,"bush");
    }

    /**
     * This method handles how everything in the Berry class acts. It also keeps track
     * of steps since spawned (time) and gives a bush berries after 40 steps.
     * @param world World
     */
    @Override
    public void act(World world) {
        stepsSinceSpawned++;

        if (stepsSinceSpawned % 40 == 0) {
            hasBerries = true;
        }
    }

    /**
     * Method manages the images, does it have berries or not
     * @return image with or without berries.
     */
    public DisplayInformation getInformation() {
        if (hasBerries) {
            return new DisplayInformation(Color.white, images[1]);
        } else {
            return new DisplayInformation(Color.white, images[0]);
        }
    }

    /**
     * This method allows animals to eat the bush and returns an integer value
     * that is used to update the animals hunger. After being eaten it removes the berries.
     *
     * @return Integer value, that animals can use to update their hunger value.
     */
    public int eatBerries() {
        hasBerries = false;
        return 5;
    }
}