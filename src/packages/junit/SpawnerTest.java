package packages.junit;
import itumulator.executable.Program;
import itumulator.world.World;
import packages.Spawner;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class SpawnerTest {
    private ArrayList<String> fileValues;
    private Spawner spawner;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        int size = 10;
        int delay = 500;
        int displaySize = 800;
        Program p = new Program(size, displaySize, delay);
        World world = p.getWorld();
        spawner = new Spawner(world, p, size);
        fileValues = new ArrayList<>();
        fileValues.add("5\n");
    }

    @org.junit.jupiter.api.Test
    void blockingTest() {
        fileValues.add("rabbit 50\n");
        assertThrows(IllegalArgumentException.class, ()-> {
            spawner.spawnObject(fileValues);
        });
    }

    @org.junit.jupiter.api.Test
    void nonblockingTest() {
        fileValues.add("grass 50\n");
        assertThrows(IllegalArgumentException.class, ()-> {
            spawner.spawnObject(fileValues);
        });
    }

    @org.junit.jupiter.api.Test
    void classTest() {
        fileValues.add("test 50\n");
        assertThrows(NumberFormatException.class, ()-> {
            spawner.spawnObject(fileValues);
        });
    }
}