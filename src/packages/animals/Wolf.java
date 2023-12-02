package packages.animals;

import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import packages.SpawnableObjects;

public class Wolf extends Animal implements Actor {
    int sizeOfWorld;
    private int stepsSinceSpawned;
    private int speed = 2;
    private Location myLocation;

    private int hunger = 5;

    private Rabbit prey;

    public Wolf(World world, Program p){
        super(world,p,"wolf-small");
        this.stepsSinceSpawned = world.getCurrentTime();
        this.sizeOfWorld = world.getSize();
    }

    public void kill(){
        world.delete(this);
    }

    public void act(World world) {
        stepsSinceSpawned++;

        if(stepsSinceSpawned % speed == 0) {
            myLocation = world.getLocation(this);
            hunger--;

            if (hunger <= 2) {
                lookForPrey();
            }
        } else {
            if (prey != null) {
                killPrey(prey);
            }
        }

    }



    private void lookForPrey(){
            prey = (Rabbit) world.getTile(super.lookForBlocking(myLocation, Rabbit.class));
    }

    private void killPrey(Rabbit prey) {
        prey.kill();
        hunger += 10;
        this.prey = null;
    }
}