package packages.animals;

import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import packages.Organism;
import packages.terrain.Carcass;
import packages.terrain.Feces;
import packages.terrain.Grass;

import java.lang.reflect.Constructor;
import java.util.Set;

/**
 * The Animal abstract class that handles how all animals behave. It is an extension of the Organism class.
 */
public abstract class Animal extends Organism implements DynamicDisplayInformationProvider, AnimalInterface {
    final int sizeOfWorld = world.getSize();
    protected int speed;
    public boolean isAdult = false;
    public boolean isDead = false;
    protected Location burrowLocation = null;
    public int hunger;
    /**
     * This is the Animal class constructor that initializes a new animal
     * with the parameters from organism with speed and hunger which is
     * specific to an animal.
     * @param speed integer value, determines the speed of animal
     * @param hunger integer value that determines the hunger of an animal.
     */
    public Animal (World world, Program p, String image, int speed, int hunger){
        super(world,p,image);
        this.speed = speed;
        this.hunger = hunger;
    }

    /**
     * The die method can be called from all animals. It deletes the objects and spawns a carcass in
     * its place
     */
    public void die() {
        isDead = true;
        world.delete(this);
        Carcass carcass = new Carcass(world,p,"carcass", "fungi");
        world.setTile(myLocation,carcass);
    }

    /**
     * This method can be called from all animals in the animals package.
     * It returns true if the animal can act
     * which it can if it is not dead and steps since spawned modulo the speed of the animal is 0.
     * @return true if the requirements are met.
     */
    protected boolean canIAct() {
        return !isDead && stepsSinceSpawned % speed == 0;
    }

    /**
     * This method can be called from all animals in the animals package.
     * It makes them wake up. and checks if the object is an adult and if it's
     * an animal in a burrow and respawns the object accordingly.
     */
    protected void wakeUp() {
        adultCheck();

        if (!world.isOnTile(this)) {
            Location wakeUpLocation = getEmptyTileClosestToLocation(burrowLocation);

            if (wakeUpLocation != null) {
                world.setTile(wakeUpLocation, this);
                myLocation = wakeUpLocation;
                world.setCurrentLocation(wakeUpLocation);
            }
        }
    }

    /**
     * Adultcheck is used for checking whether an object is adult.
     * It can be used by all animals in the animals package.
     * It is an adult if more than 60 steps have passed.
     * This method also divides the objects speed with 2
     * which makes it twice as fast.
     */
    protected void adultCheck(){
        if (!isAdult && stepsSinceSpawned > 60) {
            isAdult = true;
            speed /= 2;
        }
    }

    /**
     * This method returns the location of empty tiles closest to
     * the object it is being called from.
     * @param theLocation Location
     * @return Location, x, y coordinates of the closest empty tile.
     */
    protected Location getEmptyTileClosestToLocation(Location theLocation) {
        if (world.isTileEmpty(theLocation)) {
            return theLocation;
        }

        for (int i = 0; i < sizeOfWorld; i++) {
            for (Location targetLocation : world.getSurroundingTiles(theLocation, i)) {
                if (world.isTileEmpty(targetLocation)) {
                    return targetLocation;
                }
            }
        }

        return null;
    }

    /**
     *This method can be called from all animals. It looks for blocking object
     * with a specificed class which is also a parameter.
     * it returns the location of the object of the class that is being looked for.
     * @param targetClass Classname of object you want to find
     * @return Location of that object.
     */
    protected Location lookForBlocking(Class<?> targetClass){
        for (int i = 1; i < sizeOfWorld; i++) {
            for (Location targetLocation : world.getSurroundingTiles(myLocation, i)) {
                if (targetClass.isInstance(world.getTile(targetLocation))) {
                    return targetLocation;
                }
            }
        }
        return null;
    }

    /**
     * This method can be used by all animals and is used for
     * moving the object (the class it is called from) to a target location
     * which is the parameter. If it is more than 1 tile away it finds the fastest way.
     * @param targetLocation Location, x, y - Location object must be moved to.
     */
    protected void moveToLocation(Location targetLocation){
        if (targetLocation != null){
            Set<Location> surroundingTiles = world.getEmptySurroundingTiles(myLocation);
            if (!surroundingTiles.isEmpty()) {
                for (Location tile : surroundingTiles) {
                    if (tile == targetLocation) {
                        moveTo(tile);
                        return;
                    }
                }
                moveTo(nextTile(targetLocation, surroundingTiles));
            }
        }
    }

    /**
     * This method can be used by all classes in the animals package and is used
     * for moving the given object to a new Location.
     * Unlike moveToLocation this method can move objects instantly,
     * so it can only be used if it is 1 tile away.
     * @param tile Location, x, y coordinates - Where object must be moved to.
     */
    protected void moveTo(Location tile) {
        world.move(this, tile);
        world.setCurrentLocation(tile);
        myLocation = tile;
    }

    /**
     * This method can be used by all animals in the animals package.
     * It can calculate the distance between Location of a tile and
     * the target location you wish to find the distance to.
     * It uses the euclidean algorithm to do so.
     * @param tile
     * @param targetLocation
     * @return
     */
    protected double calculateDistance(Location tile, Location targetLocation) {
        double x1 = targetLocation.getX();
        double y1 = targetLocation.getY();
        double x2 = tile.getX();
        double y2 = tile.getY();
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    /**
     * This method calculates the distance between every surrounding tile
     * and the target Location. It returns the location of the closest tile.
     * @param targetLocation Location, x, y coordinates - Target Location.
     * @param surroundingTiles Set of all tiles surrounding the object.
     * @return Location of the surrounding tile closest to the target location.
     */
    private Location nextTile(Location targetLocation, Set<Location> surroundingTiles) {
        double minDistance = Double.POSITIVE_INFINITY;
        Location tileToMoveTo = myLocation;
        double surroundingDistance;

        for (Location tile : surroundingTiles){
            surroundingDistance = calculateDistance(tile, targetLocation);

            if (surroundingDistance < minDistance) {
                minDistance = surroundingDistance;
                tileToMoveTo = tile;
            }
        }
        return tileToMoveTo;
    }

    /**
     * This method can be used by all animals in the animals package.
     * When called by an object it will make that object look for grass
     * and ultimately move to that location.
     */
    protected void lookForGrass() {
        try {
            Object standingOn = world.getNonBlocking(world.getLocation(this));

            if (standingOn instanceof Grass) {
                Grass standingOnGrass = (Grass) standingOn;
                hunger += eat(standingOnGrass);
                return;
            }
        } catch (IllegalArgumentException e) {

        }

        moveToLocation(lookForNonBlocking(Grass.class, sizeOfWorld));
    }

    /**
     * This method can be used by all objects. It makes the object eat
     * and changes the hunger level depending on what type of food is being eaten.
     * @param food
     * @return
     */
    public int eat(Object food) {
        world.delete(food);
        if (food instanceof Carcass && ((Carcass) food).isBig()) {
            return 10;
        } else if (food instanceof Feces) {
            return 2;
        } else {
            return 5;
        }
    }

    /**
     * This method can be used by all animals in the animals package.
     * It returns the location of a nonblocking object from the specified class
     * if it is inside the radius.
     * @param targetClass class of the object you want to find
     * @param radius integer value that defines how far must be looked.
     * @return
     */
    protected Location lookForNonBlocking(Class<?> targetClass, int radius) {
        for (int i = 1; i < radius; i++) {
            for (Location targetLocation : world.getSurroundingTiles(myLocation, i)) {
                if (world.containsNonBlocking(targetLocation)
                        && targetClass.isInstance(world.getNonBlocking(targetLocation))
                        && world.isTileEmpty(targetLocation)) {
                    return targetLocation;
                }
            }
        }
        return null;
    }

    /**
     * This method can be used by all animals in the animals package.
     * It is used by predator animals, and it takes any animal as prey in the parameter.
     * If the prey is present it will call the die method and if not it will move
     * to the location of the prey.
     * @param prey Animal prey, any animal is the prey that must be hunted.
     * @return Animal prey if the prey hasn't been killed if it has null is returned.
     */
    protected Animal killPrey(Animal prey) {
        Location preyLocation = world.getLocation(prey);
        if (world.getSurroundingTiles().contains(preyLocation)) {
            prey.die();
            return null;
        } else {
            moveToLocation(world.getEmptySurroundingTiles(preyLocation).iterator().next());
            return prey;
        }
    }

    /**
     * This method can be used by all animals and is used for
     * figuring out whether an animal is on a burrow or not.
     * It returns true if burrowlocation can be found and the
     * object that calls the method is in the same location
     * as the burrow.
     * @param burrowLocation Location of a burrow.
     * @return true if burrowlocation is the same as mylocation and not null.
     */
    protected boolean isOnBurrow(Location burrowLocation) {
        return burrowLocation != null && myLocation.hashCode() == burrowLocation.hashCode();
    }

    /**
     * This method can be used by all animals in the animals package.
     * It returns the location of a Blocking object from the specified class
     * if it is inside the radius.
     * @param targetClass class of the object you want to find
     * @param radius integer value that defines how far must be looked.
     * @return Location of the target object fromd defined class.
     */
    protected Location lookForAnyBlocking(Class<?> targetClass, int radius){
        for (Location targetLocation : world.getSurroundingTiles(myLocation, radius)) {
            if (!world.isTileEmpty(targetLocation)
                    && targetClass.isInstance(world.getTile(targetLocation))){
                return targetLocation;
            }
        }
        return null;
    }

    /**
     * This method can be used by all animals in the animals package.
     * It is used by an object to look for meat with a specified class - targetclass
     * it tries to return the object, if it cant it will return null.
     * @param targetClass specified classname of the meat you want to find
     * @return Object of the specified class of meat
     */
    protected Object lookForMeat(Class<?> targetClass) {
        try {
            return world.getTile(lookForBlocking(targetClass));
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * This method can be used by all animals in the animals package.
     * It is used to get the state of the animal and takes
     * whether it is on a burrow as a boolean parameter
     * The different states define what picture of the animal
     * must be used. Value 0 means the animal is small and awake.
     * 1 means it is small and sleeping.
     * 2 means it's an adult and awake and 3 adult and sleeping.
     *
     * @param isOnBurrow boolean value determines whether it is on a burrow.
     * @return integer value defining which state the animal is in.
     */
    protected int getState(Boolean isOnBurrow) {
        if(isAdult) {
            if (world.isNight() && isOnBurrow) {
                return 3;
            } else {
                return 2;
            }
        } else if (world.isNight() && isOnBurrow) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * This method can be used by all animals in the animals package.
     * When used in an animal class it makes it reproduce.
     * First of all if it finds a partner of the same classname (OBJECT)
     * it will begin to reproduce and spawn a new instance of that object
     * in the first state.
     * If the partner of same object is null it will call movetolocation method
     * to find a partner.
     * @param className Classname of the object
     */
    protected void reproduce(String className) {
        try {
            className = "packages.animals." + className;
            Class<?> objectType = Class.forName(className);

            Location partnerLocation = lookForPartner(objectType);

            if (partnerLocation != null) {
                if (world.getSurroundingTiles().contains(partnerLocation)) {
                    Constructor<?> constructor = objectType.getConstructor(World.class, Program.class);
                    Object obj = constructor.newInstance(world, p);

                    for (Location birthLocation : world.getEmptySurroundingTiles()) {
                        world.setTile(birthLocation, obj);
                        hunger -= 5;
                        waitToReproduce((Animal) world.getTile(partnerLocation));
                        break;
                    }
                } else {
                    moveToLocation(partnerLocation);
                }

            }

        } catch (Exception e) {

        }
    }

    /**
     * This method changes the hunger level of the partner (USED in reproduce)
     * @param partner Animal
     */
    protected void waitToReproduce(Animal partner) {
        partner.hunger -= 5;
    }

    /**
     * This method can be used by all animals in the animals package.
     * It is used to find a partner of the same class as the object
     * it is being used by. it searches everytile in the world.
     * It returns the location of the partner.
     * @param targetClass Classname of the partners object (the same as when called)
     * @return Location of partner.
     */
    protected Location lookForPartner(Class<?> targetClass) {
        for (int i = 1; i < sizeOfWorld; i++) {
            for (Location targetLocation : world.getSurroundingTiles(myLocation, i)) {
                if (targetClass.isInstance(world.getTile(targetLocation))
                        && ((Animal) world.getTile(targetLocation)).isAdult
                        && ((Animal) world.getTile(targetLocation)).hunger >= 5) {
                    return targetLocation;
                }
            }
        }

        return null;
    }
}
