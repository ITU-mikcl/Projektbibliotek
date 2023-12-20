package packages.junit;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import packages.Spawner;
import packages.animals.Capybara;
import packages.animals.Rabbit;
import packages.terrain.*;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimulatoinTest {
    private ArrayList<String> fileValues;
    private Spawner spawner;
    private Program p;
    private World world;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        int size = 2;
        int delay = 100;
        int displaySize = 800;
        p = new Program(size, displaySize, delay);
        world = p.getWorld();
        spawner = new Spawner(world, p, size);
        fileValues = new ArrayList<>();
        fileValues.add("2\n");
    }


    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    private int countEnitties(Class<?> targetClass) {
        int coutner = 0;

        for (Object object : world.getEntities().keySet()) {
            if (object.getClass() == targetClass) {
                coutner++;
            }
        }

        return coutner;
    }

    @org.junit.jupiter.api.Test
    void grassSpreadTest() {
        fileValues.add("grass 1");

        spawner.spawnObject(fileValues);

        for (int i = 0; i < 30; i++) {
            p.simulate();
        }

        int count = countEnitties(Grass.class);

        assertEquals(2, count);
    }

    @org.junit.jupiter.api.Test
    void rabbitDeathTest() {
        fileValues.add("rabbit 1");

        spawner.spawnObject(fileValues);

        for (int i = 0; i < 210; i++) {
            p.simulate();
        }

        int count = countEnitties(Rabbit.class);

        assertEquals(0, count);
    }

    @org.junit.jupiter.api.Test
    void burrowTest() {
        fileValues.add("rabbit 1");

        spawner.spawnObject(fileValues);

        for (int i = 0; i < 30; i++) {
            p.simulate();
        }

        int count = countEnitties(Burrow.class);

        assertEquals(1, count);
    }

    @org.junit.jupiter.api.Test
    void wolfHuntTest() {
        fileValues.add("grass 4");
        fileValues.add("rabbit 1");
        fileValues.add("wolf 2");

        spawner.spawnObject(fileValues);

        for (int i = 0; i < 80; i++) {
            p.simulate();
        }

        int count = countEnitties(Rabbit.class);

        assertEquals(0, count);
    }

    @org.junit.jupiter.api.Test
    void carcassSpawnTest() {
        fileValues.add("rabbit 1");

        spawner.spawnObject(fileValues);

        for (int i = 0; i < 120; i++) {
            p.simulate();
        }

        int count = countEnitties(Carcass.class);

        assertEquals(1, count);
    }

    @org.junit.jupiter.api.Test
    void carcassDeathTest() {
        fileValues.add("rabbit 1");

        spawner.spawnObject(fileValues);

        for (int i = 0; i < 200; i++) {
            p.simulate();
        }

        int count = countEnitties(Carcass.class);

        assertEquals(0, count);
    }

    @org.junit.jupiter.api.Test
    void fungiSpawnTest() {
        fileValues.add("rabbit 1");

        spawner.spawnObject(fileValues);

        for (int i = 0; i < 200; i++) {
            p.simulate();
        }

        int count = countEnitties(Fungi.class);

        assertEquals(1, count);
    }

    @org.junit.jupiter.api.Test
    void caybaraEatTest() {
        fileValues.add("capybara 4");
        fileValues.add("grass 1");

        spawner.spawnObject(fileValues);

        for (int i = 0; i < 80; i++) {
            p.simulate();
        }

        int count = countEnitties(Grass.class);

        assertEquals(0, count);
    }

    @org.junit.jupiter.api.Test
    void caybaraFecesTest() {
        fileValues.add("capybara 1");

        spawner.spawnObject(fileValues);

        for (int i = 0; i < 12; i++) {
            p.simulate();
        }

        int count = countEnitties(Feces.class);

        assertEquals(1, count);
    }

    @org.junit.jupiter.api.Test
    void caybaraReproduceTest() {
        fileValues.add("capybara 2");
        fileValues.add("grass 4");

        spawner.spawnObject(fileValues);

        for (int i = 0; i < 80; i++) {
            p.simulate();
        }

        int count = countEnitties(Capybara.class);

        assertEquals(4, count);
    }
}