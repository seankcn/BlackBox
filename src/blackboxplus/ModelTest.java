package blackboxplus;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ModelTest {
    @Test
    public void testMakeFields() { //test for makeFields()
        Model model = new Model();
        char[][] input = { //11x11 board of just atoms marked
                {'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n'},
                {'n', 'e', 'e', 'e', 'e', 'e', 'n', 'n', 'n', 'n', 'n'},
                {'n', 'e', 'e', 'e', 'e', 'e', 'e', 'n', 'n', 'n', 'n'},
                {'n', 'e', 'e', 'e', 'a', 'e', 'e', 'e', 'n', 'n', 'n'},
                {'n', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'n', 'n'},
                {'n', 'e', 'e', 'e', 'e', 'a', 'e', 'e', 'e', 'e', 'n'},
                {'n', 'n', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'n'},
                {'n', 'n', 'n', 'e', 'e', 'a', 'e', 'e', 'e', 'e', 'n'},
                {'n', 'n', 'n', 'n', 'e', 'e', 'e', 'e', 'e', 'e', 'n'},
                {'n', 'n', 'n', 'n', 'n', 'e', 'a', 'e', 'e', 'e', 'n'},
                {'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n'}
        };
        char[][] expectedOutput = { //board with atoms and fields marked
                {'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n'},
                {'n', 'e', 'e', 'e', 'e', 'e', 'n', 'n', 'n', 'n', 'n'},
                {'n', 'e', 'e', 'f', 'f', 'e', 'e', 'n', 'n', 'n', 'n'},
                {'n', 'e', 'e', 'f', 'a', 'f', 'e', 'e', 'n', 'n', 'n'},
                {'n', 'e', 'e', 'e', 'f', 'f', 'e', 'e', 'e', 'n', 'n'},
                {'n', 'e', 'e', 'e', 'f', 'a', 'f', 'e', 'e', 'e', 'n'},
                {'n', 'n', 'e', 'e', 'f', 'f', 'f', 'e', 'e', 'e', 'n'},
                {'n', 'n', 'n', 'e', 'f', 'a', 'f', 'e', 'e', 'e', 'n'},
                {'n', 'n', 'n', 'n', 'e', 'f', 'f', 'e', 'e', 'e', 'n'},
                {'n', 'n', 'n', 'n', 'n', 'f', 'a', 'f', 'e', 'e', 'n'},
                {'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n'}
        };
        //check if makeFields creates board as expected
        model.makeFields(input);
        assertArrayEquals(expectedOutput, input);
    }

    @Test
    public void testCreateBoard() { //test for createBoard()
        Model model = new Model();

        char[][] expectedOutput = { //board with atoms and fields marked
                {'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n'},
                {'n', 'e', 'e', 'e', 'e', 'e', 'n', 'n', 'n', 'n', 'n'},
                {'n', 'e', 'e', 'f', 'f', 'e', 'e', 'n', 'n', 'n', 'n'},
                {'n', 'e', 'e', 'f', 'a', 'f', 'e', 'e', 'n', 'n', 'n'},
                {'n', 'e', 'e', 'e', 'f', 'f', 'e', 'e', 'e', 'n', 'n'},
                {'n', 'e', 'e', 'e', 'f', 'a', 'f', 'e', 'e', 'e', 'n'},
                {'n', 'n', 'e', 'e', 'f', 'f', 'f', 'e', 'e', 'e', 'n'},
                {'n', 'n', 'n', 'e', 'f', 'a', 'f', 'e', 'e', 'e', 'n'},
                {'n', 'n', 'n', 'n', 'e', 'f', 'f', 'e', 'e', 'e', 'n'},
                {'n', 'n', 'n', 'n', 'n', 'f', 'a', 'f', 'e', 'e', 'n'},
                {'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n'}
        };

        Set<Integer> myAtoms = new HashSet<>();
        myAtoms.add(14);
        myAtoms.add(30);
        myAtoms.add(45);
        myAtoms.add(57);

        model.createBoard(myAtoms);
        char[][] board = model.getBoard();

        assertArrayEquals(expectedOutput, board);
    }

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void testShootRay() { //test for shootRay()
        char[][] board = { //11x11 model of board with atoms and fields
                {'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n'},
                {'n', 'e', 'e', 'e', 'e', 'e', 'n', 'n', 'n', 'n', 'n'},
                {'n', 'e', 'e', 'f', 'f', 'e', 'e', 'n', 'n', 'n', 'n'},
                {'n', 'e', 'e', 'f', 'a', 'f', 'e', 'e', 'n', 'n', 'n'},
                {'n', 'e', 'e', 'e', 'f', 'f', 'e', 'e', 'e', 'n', 'n'},
                {'n', 'e', 'e', 'e', 'f', 'a', 'f', 'e', 'e', 'e', 'n'},
                {'n', 'n', 'e', 'e', 'f', 'f', 'f', 'e', 'e', 'e', 'n'},
                {'n', 'n', 'n', 'e', 'f', 'a', 'f', 'e', 'e', 'e', 'n'},
                {'n', 'n', 'n', 'n', 'e', 'f', 'f', 'e', 'e', 'e', 'n'},
                {'n', 'n', 'n', 'n', 'n', 'f', 'a', 'f', 'e', 'e', 'n'},
                {'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n'}
        };
        Model model = new Model(board);

        model.shootRay(4,1,0,1);
        String printedOutput = outContent.toString();
        assertEquals("Direct Hit", printedOutput.trim());
        outContent.reset();


    }

}
