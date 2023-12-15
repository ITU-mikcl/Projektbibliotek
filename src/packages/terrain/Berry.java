package packages.terrain;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.World;
import packages.SpawnableObjects;

import java.awt.*;

public class Berry extends SpawnableObjects implements Actor, DynamicDisplayInformationProvider {
    final String[] images = {"bush", "bush-berries"};
    private int stepsSinceSpawned = 0;
    public boolean hasBerries = false;

    public Berry(World world, Program p) {
        super(world,p,"bush");
    }

    @Override
    public void act(World world) {
        stepsSinceSpawned++;

        if (stepsSinceSpawned % 40 == 0) {
            hasBerries = true;
        }
    }

    public DisplayInformation getInformation() {
        if (hasBerries) {
            return new DisplayInformation(Color.white, images[1]);
        } else {
            return new DisplayInformation(Color.white, images[0]);
        }
    }

    public int eatBerries() {
        hasBerries = false;
        return 5;
    }
}