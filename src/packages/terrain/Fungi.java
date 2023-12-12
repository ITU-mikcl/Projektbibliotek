package packages.terrain;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import packages.SpawnableObjects;

public class Fungi extends SpawnableObjects implements Actor {
    private int stepsSinceSpawned = 0;
    final Location myLocation;

    public Fungi(World world, Program p, String image, Location myLocation) {
        super(world,p,image);
        this.myLocation = myLocation;
    }

    @Override
    public void act(World world) {
        stepsSinceSpawned++;

        if (stepsSinceSpawned >= 40 && world.isTileEmpty(myLocation) && !world.containsNonBlocking(myLocation)) {
            world.setTile(myLocation, this);
        }
    }
}