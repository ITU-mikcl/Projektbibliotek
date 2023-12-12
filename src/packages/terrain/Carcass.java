package packages.terrain;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import packages.SpawnableObjects;

import java.awt.*;


public class Carcass extends SpawnableObjects implements Actor, DynamicDisplayInformationProvider {
    final String[] images = {"carcass-small", "carcass"};
    final String[] fungiImages = {"fungi-small", "fungi"};
    int stepsSinceSpawned;
    String image;
    int state = 1;

    public Carcass(World world, Program p, String image){
        super(world,p,image);
        this.image = image;
    }
    public void act(World world) {
        stepsSinceSpawned++;

        if (stepsSinceSpawned % 60 == 0) {
            Location myLocation = world.getLocation(this);
            world.delete(this);
            Fungi currentFungi =  new Fungi(world, p, fungiImages[state], myLocation);
            world.setTile(myLocation, currentFungi);
            world.remove(currentFungi);
        }
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