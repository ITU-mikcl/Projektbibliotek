package packages.junit;
import itumulator.executable.Program;
import itumulator.world.World;
import packages.Spawner;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class SpawnerTest {

    @org.junit.jupiter.api.BeforeEach
    void setUp() {

    }

    @org.junit.jupiter.api.Test
    void worldTest() {
        int size = 5;
        int delay = 500;
        int displaySize = 800;
        Program p = new Program(size, displaySize, delay);
        World world = p.getWorld();
        Spawner spawner = new Spawner(world, p, size);
        ArrayList<String> fileValues = new ArrayList<>();
        fileValues.add("5\n");
        fileValues.add("rabbit 50\n");
        assertThrows(IllegalArgumentException.class, ()-> {
            spawner.spawnObject(fileValues);
        });
    }

    @org.junit.jupiter.api.Test
    void GrassTest() {
        int size = 5;
        int delay = 500;
        int displaySize = 800;
        Program p = new Program(size, displaySize, delay);
        World world = p.getWorld();
        Spawner spawner = new Spawner(world, p, size);
        ArrayList<String> fileValues = new ArrayList<>();
        fileValues.add("5\n");
        fileValues.add("grass 69\n");
        assertThrows(IllegalArgumentException.class, ()-> {
            spawner.spawnObject(fileValues);
        });
    }
}