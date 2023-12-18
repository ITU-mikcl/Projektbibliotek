package packages;
import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.World;

import java.awt.*;

/**
 * This abstract class is the inheritance of every spawnable object.
 */
public abstract class SpawnableObjects {
    public Program p;
    public World world;
    public SpawnableObjects(World world, Program p, String image){
        this.world = world;
        this.p = p;
        DisplayInformation di = new DisplayInformation(Color.black, image);
        p.setDisplayInformation(this.getClass(), di);

    }
}
