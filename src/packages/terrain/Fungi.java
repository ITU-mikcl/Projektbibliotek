package packages.terrain;

import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import packages.Organism;
import packages.SpawnableObjects;

/**
 * The Fungi class is an organism that can act on its own.
 */
public class Fungi extends Organism implements Actor {
    private boolean hasSpread = false;
    private final String image;

    /**
     * The constructor initializes the class as an organism with a Location myLocation.
     * This constructor also handles the hunger value of the fungi. If it's a big fungi
     * 20 hunger points will be added to the animal that eats it, else 10.
     * @param world world
     * @param p Program
     * @param image image of fungi
     * @param myLocation Location input, x, y coordinates
     */
    public Fungi(World world, Program p, String image, Location myLocation) {
        super(world, p, image);
        this.myLocation = myLocation;
        this.image = image;

        if (image.equals("fungi")) {
            this.hunger = 20;
        } else {
            this.hunger = 10;
        }
    }

    /**
     * This method comes from the Actor interface and handles how a fungi acts.
     * A fungi spawns in a carcass if it hasn't been eaten for 40 days
     * It spawns a fungi if it has been 40 steps since it was spawned
     * (in the carcass class a fungi is spawned when a carcass is spawned, then removed)
     * A fungi can also eat uneaten carcasses nearby but only if hasSpread is false.
     * @param world World
     */
    @Override
    public void act(World world) {
        hunger = getHunger(hunger);

        if (stepsSinceSpawned >= 40 && world.isTileEmpty(myLocation) && !world.containsNonBlocking(myLocation)) {
            world.setTile(myLocation, this);
        }

        if (foodAvailable() && !hasSpread) {
            for (Location tile : world.getSurroundingTiles(lookForCarcass())) {
                if (world.isTileEmpty(tile)) {
                    Fungi tempFungi = new Fungi(world, p, image, tile);
                    world.setTile(tile, tempFungi);
                    world.remove(tempFungi);
                    hasSpread = true;
                    break;
                }
            }
        }
    }

    /**
     * This method is responsible for handling the death/deletion of a fungi.
     */
    @Override
    public void die() {
        world.delete(this);
    }

    /**
     * This method handles how a fungi looks for a carcass to eat.
     * @return Location of a nearby Carcass.
     */
    protected Location lookForCarcass() {
        for (int i = 1; i < 4; i++) {
            for (Location targetLocation : world.getSurroundingTiles(myLocation, i)) {
                if (world.getTile(targetLocation) instanceof Carcass) {
                    return targetLocation;
                }
            }
        }

        return null;
    }

    /**
     * This method is responsible for deciding if there is a carcass to eat near the fungi.
     * @return carcass if it is available (true).
     */
    private boolean foodAvailable() {
        Carcass carcass = null;
        Location carcassLocation = lookForCarcass();

        if (carcassLocation != null) {
            carcass = (Carcass) world.getTile(lookForCarcass());
        }

        return carcass != null;
    }
}