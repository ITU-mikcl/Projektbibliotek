package packages.animals;

import itumulator.executable.Program;
import itumulator.world.World;

/**
 * The FossorialAnimals abstract class is an extension of animal.
 * It allows fossorial animals to look for and get into a burrow.
 */
public abstract class FossorialAnimals extends Animal implements FossorialInterface {
    public FossorialAnimals (World world, Program p, String image, int speed, int hunger){
        super(world,p,image, speed, hunger);
    }

    /**
     * This method can be used by all fossorial animals to check if it is
     * already standing on a burrow or if it needs to find one. It also handles
     * the removal of the animal if it is.
     */
    protected void getToBurrow() {
        if (!isOnBurrow(burrowLocation)){
            lookForBurrow();
        } else {
            world.remove(this);
        }
    }
}
