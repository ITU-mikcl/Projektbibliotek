package packages;

import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;

public abstract class Organism extends SpawnableObjects implements OrganismInterface{
    protected int stepsSinceSpawned = 0;
    protected Location myLocation;
    protected int hunger;

    protected Organism(World world, Program p, String image) {
        super(world,p,image);
    }

    protected int getHunger(int hunger) {
        stepsSinceSpawned++;
        if (world.isOnTile(this)) {
            myLocation = world.getLocation(this);
            world.setCurrentLocation(myLocation);

            if (hunger <= 0) {
                die();
            }
        }

        if (stepsSinceSpawned % 10 == 0) {
            return --hunger;
        } else {
            return hunger;
        }
    }
}
