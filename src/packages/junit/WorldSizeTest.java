package packages.junit;
import itumulator.executable.Program;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class WorldSizeTest {

    @org.junit.jupiter.api.BeforeEach
    void setUp() {

    }


    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    void sizeTest() {
        int size = 0;
        int delay = 500;
        int displaySize = 800;

        assertThrows(IllegalArgumentException.class, ()-> {
                    Program p = new Program(size, displaySize, delay);
        });
    }
}