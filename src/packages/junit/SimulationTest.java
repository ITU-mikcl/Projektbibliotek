package packages.junit;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import packages.Spawner;
import packages.animals.Rabbit;
import packages.terrain.Burrow;
import packages.terrain.Grass;

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
}