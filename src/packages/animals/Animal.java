package packages.animals;

import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import packages.SpawnableObjects;
import packages.terrain.Hole;

public abstract class Animal extends SpawnableObjects {
    int sizeOfWorld;
    public Animal (World world, Program p, String image){
        super(world,p,image);
        this.sizeOfWorld = world.getSize();
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
