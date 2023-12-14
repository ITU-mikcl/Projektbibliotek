package packages.animals;
import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import packages.terrain.Burrow;
import packages.terrain.Carcass;

import java.awt.*;

public class Capybara extends Animal implements Actor {
    final String[] images = {"small-capybar", "small-capybar-sleeping", "capybar", "capybar-sleeping"};
    private boolean friendFound;
    Object friend;
    public Capybara(World world, Program p) {
        super(world, p, "small-capybar", 2, 10);
        this.friendFound = false;

    }

    public void act(World world) {
        hunger = getHunger(hunger);

        if (canIAct()) {
            if (world.isDay()) {
                dayAct();
            }
        }

    }
    private void dayAct() {
        if (!friendFound) {
            while (friend instanceof Capybara) {
                friend = world.getTile(lookForBlocking(Animal.class));

            }
            friendFound = true;
        }

        if (hunger <= 5) {
            lookForGrass();

        } else if (friend != null){
            moveTo(world.getLocation(friend));

        } else {
            friendFound = false;
        }
    }
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.white, images[1]);
    }
}
