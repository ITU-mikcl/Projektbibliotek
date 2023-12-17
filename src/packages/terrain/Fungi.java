package packages.terrain;

import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import packages.Organism;
import packages.SpawnableObjects;

public class Fungi extends Organism implements Actor {
    private boolean hasSpread = false;
    private final String image;

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

    @Override
    public void die() {
        world.delete(this);
    }

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

    private boolean foodAvailable() {
        Carcass carcass = null;
        Location carcassLocation = lookForCarcass();

        if (carcassLocation != null) {
            carcass = (Carcass) world.getTile(lookForCarcass());
        }

        return carcass != null;
    }
}