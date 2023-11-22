import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.World;

import java.awt.*;

public class LivingThings{
    Program p;
    World world;
    public LivingThings(World world, Program p, String image){
        this.world = world;
        this.p = p;
        DisplayInformation di = new DisplayInformation(Color.black, image);
        p.setDisplayInformation(this.getClass(), di);

    }
}
