import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
public class Rabbit extends SpawnableObjects implements Actor {
    private int hunger = 5;

    public Rabbit(World world, Program p){
        super(world,p,"rabbit-small");
    }

    @Override
    public void act(World world) {

        if (world.isDay() && world.getCurrentTime() % 2 == 0) {
            try {
                Object standingOn = world.getNonBlocking(world.getLocation(this));

                if (standingOn instanceof Grass) {
                    Grass standingOnGrass = (Grass) standingOn;
                    eat(standingOnGrass);

                }
            } catch (IllegalArgumentException e) {

            }
            }

        if (world.getCurrentTime() % 20 == 0 ) {
            if (hunger == 0) {
                killRabbit();//😵
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
