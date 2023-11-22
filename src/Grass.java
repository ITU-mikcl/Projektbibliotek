
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Grass extends LivingThings implements NonBlocking, Actor {
    Program p;
    World world;
    int age;
    boolean hasDayChaged = false;
    public Grass(World world, Program p){
        super(world,p,"grass");
        age = 0;
    }
    public void act(World world){
        if(world.isDay() && hasDayChaged){
            hasDayChaged = false;
            age++;
            if(age % 2 == 0){
                spread();
            }
        } else if (world.isNight()) {
            hasDayChaged = true;
        }
    }
    public void decompose(){
        world.delete(this);
    }

    public void spread(){
        Set<Location> surroundingTiles = world.getSurroundingTiles();
        List<Location> list = new ArrayList<>(surroundingTiles);
        for (Location location : list) {
            if (!world.containsNonBlocking(location)) {
                world.setTile(location, new Grass(world, p));
                break;
            }
        }
    }
}