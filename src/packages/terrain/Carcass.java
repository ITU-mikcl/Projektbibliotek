package packages.terrain;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.World;
import packages.SpawnableObjects;

import java.awt.*;


public class Carcass extends SpawnableObjects implements Actor, DynamicDisplayInformationProvider {
    final String[] images = {"carcass-small", "carcass"};
    int stepsSinceSpawned;
    String image;
    int state = 1;

    public Carcass(World world, Program p, String image){
        super(world,p,image);
        this.image = image;
    }
    public void act(World world) {
        stepsSinceSpawned++;
    }
    public boolean isBig(){
        return image.equals("carcass");
    }

    public void changeState(int i){
        state = i;
    }

    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.white, images[state]);
    }
}