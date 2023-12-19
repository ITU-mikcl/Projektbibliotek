package packages;

import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;

/**
 * This abstract class extends SpawnableObjects and Implements the OrganismInterface. It is
 * responsible for managing the hunger and Location of an entity.
 */
public abstract class Organism extends SpawnableObject implements OrganismInterface{
    protected int stepsSinceSpawned = 0;
    protected Location myLocation;
    protected int hunger;

    /**
     * This constructor initializes the abstract class.
     * @param world World
     * @param p Program
     * @param image String name of the Image that must be displayed.
     */
    protected Organism(World world, Program p, String image) {
        super(world,p,image);
    }

    /**
     * This method is responsible for returning and changing the hunger of a living entity.
     * @param hunger integer value that keeps track of the hunger level of an entity.
     * @return Hunger, if 10 steps have passed it will subtract the hunger level with 1.
     */
    protected int getHunger(int hunger) {
        stepsSinceSpawned++;
        if (world.isOnTile(this)) {
            myLocation = world.getLocation(this);
            world.setCurrentLocation(myLocation);

            if (hunger <= 0 || stepsSinceSpawned >= 200) {
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
