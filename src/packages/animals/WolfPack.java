package packages.animals;

import itumulator.executable.Program;
import itumulator.world.World;

import java.util.LinkedList;

public class WolfPack{

    LinkedList<Wolf> pack = new LinkedList<>();
    int packNumber;
    World world;
    Program p;
    public WolfPack(World world, Program p, int packNumber) {
        this.world = world;
        this.p = p;
        this.packNumber = packNumber;
    }
    public void addWolf(Wolf wolf){
        pack.add(wolf);
    }

}
