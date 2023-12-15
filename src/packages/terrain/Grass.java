package packages.terrain;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;
import packages.SpawnableObjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Grass extends SpawnableObjects implements NonBlocking, Actor {

    public Grass(World world, Program p){
        super(world,p,"grass");
    }
    public void act(World world){
        if (world.getCurrentTime() == 0) {
            spread();
        }
    }

    public void spread(){
        for (Location location : world.getSurroundingTiles()) {
            if (!world.containsNonBlocking(location)) {
                world.setTile(location, new Grass(world, p));
                break;
            }
        }
    }
}