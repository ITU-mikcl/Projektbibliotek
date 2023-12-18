package packages.terrain;

import itumulator.executable.Program;
import itumulator.world.NonBlocking;
import itumulator.world.World;
import packages.SpawnableObjects;

/**
 * The Feces class is a nonBlocking spawnable object that doesn't act on its own
 * It can be spawned as a spawnable object with the display image "feces"
 */
public class Feces extends SpawnableObjects implements NonBlocking {
    public Feces(World world, Program p) {
        super(world, p, "feces");
    }

}
