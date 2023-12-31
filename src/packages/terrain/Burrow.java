package packages.terrain;

import itumulator.executable.Program;
import itumulator.world.NonBlocking;
import itumulator.world.World;
import packages.SpawnableObject;

/**
 * This class is a nonBlocking Spawnable Object that instantiates a Burrow.
 */
public class Burrow extends SpawnableObject implements NonBlocking {

    public Burrow(World world, Program p, String image) {
        super(world,p,image);
    }
}