package packages.animals;

import itumulator.executable.Program;
import itumulator.world.World;

public abstract class FossorialAnimals extends Animal implements FossorialInterface {
    public FossorialAnimals (World world, Program p, String image, int speed, int hunger){
        super(world,p,image, speed, hunger);
    }
    protected void getToBurrow() {
        if (!isOnBurrow(burrowLocation)){
            lookForBurrow();
        } else {
            world.remove(this);
        }
    }
}
