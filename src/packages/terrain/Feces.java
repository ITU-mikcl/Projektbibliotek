package packages.terrain;

import itumulator.executable.Program;
import itumulator.world.NonBlocking;
import itumulator.world.World;
import packages.SpawnableObjects;

public class Feces extends SpawnableObjects implements NonBlocking {
    public Feces(World world, Program p) {
        super(world, p, "feces");
    }

}
