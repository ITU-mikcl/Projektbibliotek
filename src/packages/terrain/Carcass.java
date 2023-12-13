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
    int stepsSinceSpawned;
    String carcassImage;
    String fungiImage;

    public Carcass(World world, Program p, String carcassImage, String fungiImage){
        super(world,p,carcassImage);
        this.carcassImage = carcassImage;
        this.fungiImage = fungiImage;
    }
    public void act(World world) {
        stepsSinceSpawned++;

        if (stepsSinceSpawned % 60 == 0) {
            Location myLocation = world.getLocation(this);
            world.delete(this);
            Fungi currentFungi =  new Fungi(world, p, fungiImage, myLocation);
            world.setTile(myLocation, currentFungi);
            world.remove(currentFungi);
        }
    }
    public boolean isBig(){
        return carcassImage.equals("carcass");
    }

    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.white, carcassImage);
    }
}