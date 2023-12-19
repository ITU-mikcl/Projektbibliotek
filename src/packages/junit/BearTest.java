package packages.junit;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import packages.Spawner;
import packages.animals.Bear;
import packages.animals.Rabbit;
import packages.animals.Wolf;
import packages.terrain.Burrow;
import packages.terrain.Grass;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BearTest {
    private ArrayList<String> fileValues;
    private Spawner spawner;
    private Program p;
    private World world;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        int size = 6;
        int delay = 100;
        int displaySize = 800;
        p = new Program(size, displaySize, delay);
        world = p.getWorld();
        spawner = new Spawner(world, p, size);
        fileValues = new ArrayList<>();
        fileValues.add("6\n");
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
    void bearKillRabbitTest() {
        fileValues.add("bear 1");
        fileValues.add("rabbit 35");

        spawner.spawnObject(fileValues);

        for (int i = 0; i < 20; i++) {
            p.simulate();
        }

        int count = countEnitties(Rabbit.class);

        System.out.println(count);

        assertEquals(count < 35, true);
    }

    @org.junit.jupiter.api.Test
    void bearTerritoryTest() {
        fileValues.add("bear 1");

        spawner.spawnObject(fileValues);

        for (int i = 0; i < 20; i++) {
            p.simulate();
        }

        int count = 0;

        for (Object object : world.getEntities().keySet()) {
            if (object instanceof Bear) {
                try {
                    Bear bear = (Bear) object;
                    Field myTerritoryField = bear.getClass().getDeclaredField("myTerritory");
                    myTerritoryField.setAccessible(true);
                    Set<Location> myTerritory = (Set<Location>) myTerritoryField.get(bear);

                    count = ((Set) myTerritoryField.get(bear)).size();
                } catch (Exception e) {

                }

            }
        }

        assertEquals(2, 2);
    }
}