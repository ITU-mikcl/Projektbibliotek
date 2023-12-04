package packages.animals;

import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import packages.SpawnableObjects;

public abstract class Animal extends SpawnableObjects implements DynamicDisplayInformationProvider {
    final int sizeOfWorld = world.getSize();
    private int stepsSinceSpawned = 0;
    private int speed;
    private boolean isAdult = false;
    private int currentTime = 0;
    public Animal (World world, Program p, String image, int speed){
        super(world,p,image);
        this.speed = speed;
    }

    protected boolean isAdult() {
        if (!isAdult && stepsSinceSpawned > 60) {
            isAdult = true;
            speed /= 2;
        }

        return isAdult;
    }

    protected int getState() {
        if(isAdult()) {
            if (world.isDay()) {
                return 2;
            } else {
                return 3;
            }
        } else if (world.isDay()) {
            return 0;
        } else {
            return 1;
        }
    }

    protected void die(Object me) {
        world.delete(me);
    }

    protected int statusCheck(Object me, int hunger) {
        stepsSinceSpawned++;
        currentTime = world.getCurrentTime();

        if (currentTime == 0) {

            getInformation();
        } else if (currentTime == 10) {
            getInformation();
        }

        if (hunger == 0) {
            die(me);
        }

        return --hunger;
    }

    protected boolean canIAct() {
        return stepsSinceSpawned % speed == 0;
    }

    protected boolean lookForNonBlocking(Location myLocation, Class<?> targetClass) {
            for (int i = 1; i < sizeOfWorld; i++) {
                for (Location targetLocation : world.getSurroundingTiles(myLocation, i)) {
                    if (world.containsNonBlocking(targetLocation)
                            && targetClass.isInstance(world.getNonBlocking(targetLocation))
                            && world.isTileEmpty(targetLocation)) {
                        world.move(this, targetLocation);
                        return true;
                    }
                }
            }
            return false;
    }
    protected Location lookForBlocking(Location myLocation, Class<?> targetClass){
        for (int i = 1; i < sizeOfWorld; i++) {
            for (Location targetLocation: world.getSurroundingTiles(myLocation, i)) {
                if (targetClass.isInstance(world.getTile(targetLocation)) && !world.getEmptySurroundingTiles(targetLocation).isEmpty()) {
                    for (Location partnerLocationEmptySurroundingTile: world.getEmptySurroundingTiles(targetLocation)){
                        world.move(this,partnerLocationEmptySurroundingTile);
                        return targetLocation;
                    }
                }
            }
        }
        return null;
    }
}
