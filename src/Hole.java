import itumulator.executable.Program;
import itumulator.world.NonBlocking;
import itumulator.world.World;

public class Hole extends SpawnableObjects implements NonBlocking {

    public Hole(World world, Program p) {
        super(world,p,"hole");
    }
}