import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

public class Rabbit extends SpawnableObjects implements Actor {
    int sizeOfWorld;
    private int hunger = 5;
    private Location rabbitLoaction;
    private double currentClosesGrassTile = 0.0;
    private Location closesGrassTile;

    public Rabbit(World world, Program p){
        super(world,p,"rabbit-small");

        this.sizeOfWorld = world.getSize();
    }

    @Override
    public void act(World world) {
        if (world.isDay() && world.getCurrentTime() % 2 == 0) {
            rabbitLoaction = world.getLocation(this);

            try {
                Object standingOn = world.getNonBlocking(world.getLocation(this));

                if (standingOn instanceof Grass) {
                    Grass standingOnGrass = (Grass) standingOn;
                    eat(standingOnGrass);
                }
            } catch (IllegalArgumentException e) {
                outloop:
                for (int i = 1; i < sizeOfWorld; i++) {
                    for (Location grassLocation : world.getSurroundingTiles(rabbitLoaction, i)) {
                        if (world.containsNonBlocking(grassLocation)
                                && world.getNonBlocking(grassLocation) instanceof Grass
                                && world.isTileEmpty(grassLocation)) {
                            world.move(this, grassLocation);
                            break outloop;
                        }
                    }
                }


                /*
                for(Location grassLocation : world.getSurroundingTiles(rabbitLoaction, sizeOfWorld)) {
                    if(world.containsNonBlocking(grassLocation) && world.getNonBlocking(grassLocation) instanceof Grass) {
                            if(currentClosesGrassTile == 0.0) {
                                currentClosesGrassTile = Math.sqrt(Math.pow(Math.abs(rabbitLoaction.getX() - grassLocation.getX()),2)
                                        + Math.pow(Math.abs(rabbitLoaction.getY() - grassLocation.getY()),2));
                                closesGrassTile = grassLocation;
                            } else if(Math.sqrt(Math.pow(Math.abs(rabbitLoaction.getX() - grassLocation.getX()),2)
                                    + Math.pow(Math.abs(rabbitLoaction.getY() - grassLocation.getY()),2))
                                    < currentClosesGrassTile) {
                                closesGrassTile = grassLocation;
                            }
                    }



                if(currentClosesGrassTile != 0) {
                    world.move(this, closesGrassTile);
                }

                 */
            }
        }

        if (world.getCurrentTime() % 20 == 0 ) {
            if (hunger == 0) {
                killRabbit();
            }
            hunger--;
        }
    }
    public void eat(Grass grass) {
        hunger++;
        grass.decompose();
    }

    public void killRabbit(){
        world.delete(this);
    }
}
