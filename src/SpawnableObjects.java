import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.executable.Program;
import itumulator.world.World;

import java.awt.*;

public abstract class SpawnableObjects {
    Program p;
    World world;
    public SpawnableObjects(World world, Program p, String image){
        this.world = world;
        this.p = p;
        DisplayInformation di = new DisplayInformation(Color.black, image);
        p.setDisplayInformation(this.getClass(), di);

    }
}
