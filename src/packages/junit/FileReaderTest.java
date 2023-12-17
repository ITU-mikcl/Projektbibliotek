package packages.junit;
import itumulator.executable.Program;
import packages.FileReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class FileReaderTest {

    @org.junit.jupiter.api.BeforeEach
    void setUp() {

    }


    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    void dirNotFoundTest() {
        assertThrows(NumberFormatException.class, ()-> {
            FileReader.readFile(100, 1);
        });
    }

    @org.junit.jupiter.api.Test
    void fileNotFoundTest() {
        assertThrows(NumberFormatException.class, ()-> {
            FileReader.readFile(1, 100);
        });
    }
}