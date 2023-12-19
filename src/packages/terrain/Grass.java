package packages.terrain;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;
import packages.SpawnableObject;

/**
 * The Grass class is a nonblocking spawnable object that can act on its own
 * Grass can grow more grass and be eaten by other animals.
 */
public class Grass extends SpawnableObject implements NonBlocking, Actor {
    /**
     * The Grass constructor initializes grass a new spawnable object with the image "grass"
     * @param world world
     * @param p Program
     */
    public Grass(World world, Program p){
        super(world,p,"grass");
    }

    /**
     * This method comes from the actor interface and is responsible for managing how the grass acts by calling
     * the other methods in the class. Every new day it calls the spread method (that spreads Grass)
     * @param world World.
     */
    public void act(World world){
        if (world.getCurrentTime() == 0) {
            spread();
        }
    }

    /**
     * The spread method allows grass to spawn a new instance of grass on a
     * nearby empty tile.
     */
    public void spread(){
        for (Location location : world.getSurroundingTiles()) {
            if (!world.containsNonBlocking(location)) {
                world.setTile(location, new Grass(world, p));
                break;
            }
        }
    }
}