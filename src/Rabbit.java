import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;

public class Rabbit extends SpawnableObjects implements Actor {
    int sizeOfWorld;
    private int hunger = 5;
    private Location rabbitLoaction;

    private int stepsSinceSpawned;
    boolean isAdult = false;
    private int speed = 2;

    public Rabbit(World world, Program p){
        super(world,p,"rabbit-small");
        this.stepsSinceSpawned = world.getCurrentTime();
        this.sizeOfWorld = world.getSize();
    }

    @Override
    public void act(World world) {
        if(!isAdult && (world.getCurrentTime() - stepsSinceSpawned) > 30 ){
            isAdult = true;
            speed = 5;
            /*
            DisplayInformation di = new DisplayInformation(Color.black, "hole");
            p.setDisplayInformation(this.getClass(), di);
            System.out.println(di.getImageKey());

             */
        }

        if (world.isDay() && world.getCurrentTime() % speed == 0) {
            rabbitLoaction = world.getLocation(this);
            if (isAdult && hunger >= 5) {
                lookForPartner();
            }else{
                lookForFood();
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
    private void reproduce(){
        for (Location birthLocation: world.getEmptySurroundingTiles()){
            world.setTile(birthLocation, new Rabbit(world,p));
            hunger-=3;
            break;
        }
    }
    private void lookForPartner(){
            outerloop:
            for (int i = 1; i < sizeOfWorld; i++) {
                for (Location partnerLocation: world.getSurroundingTiles(rabbitLoaction, i)) {
                    if (world.getTile(partnerLocation) instanceof Rabbit && !world.getEmptySurroundingTiles(partnerLocation).isEmpty()) {
                        for (Location partnerLocationEmptySurroundingTile: world.getEmptySurroundingTiles(partnerLocation)){
                            world.move(this,partnerLocationEmptySurroundingTile);
                            break;
                        }
                        reproduce();
                        break outerloop;
                    }
                }
           }
    }
    private void lookForFood(){
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
        }
    }

}
