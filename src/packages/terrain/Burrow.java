package packages.terrain;

import itumulator.executable.Program;
import itumulator.world.NonBlocking;
import itumulator.world.World;
import packages.SpawnableObjects;

public class Burrow extends SpawnableObjects implements NonBlocking {

    public Burrow(World world, Program p) {
        super(world,p,"hole-small");
    }
}