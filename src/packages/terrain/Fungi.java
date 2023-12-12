package packages.terrain;

import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import packages.SpawnableObjects;
import java.util.Set;
import java.util.HashSet;

public class Fungi extends SpawnableObjects implements Actor {
    private int stepsSinceSpawned = 0;
    final Location myLocation;
    private int hunger;
    private int state;
    private boolean hasSpread = false;
    private String image;

    public Fungi(World world, Program p, String image, Location myLocation, int state) {
        super(world, p, image);
        this.myLocation = myLocation;
        this.image = image;
        this.state = state;
        if (state > 0) {
            this.hunger = 10;
        } else {
            this.hunger = 5;
        }
    }

    @Override
    public void act(World world) {
        stepsSinceSpawned++;
        if (stepsSinceSpawned >= 40 && world.isTileEmpty(myLocation) && !world.containsNonBlocking(myLocation)) {
            world.setTile(myLocation, this);
        }
        if (hunger == 0) {
            world.delete(this);
        }
        if (stepsSinceSpawned % 5 == 0 && world.isOnTile(this)) {
            hunger--;
        }
        if (foodAvailable() && !hasSpread) {
            for (Location tile : world.getSurroundingTiles(lookForCarcass())) {
                if (world.isTileEmpty(tile)) {
                    Fungi tempFungi = new Fungi(world, p, image, tile, state);
                    world.setTile(tile, tempFungi);
                    world.remove(tempFungi);
                    hasSpread = true;
                    break;
                }
            }
        }
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