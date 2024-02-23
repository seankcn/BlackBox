import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class boardTests {
    @Test
    public void testCreateHex() { //tests createHex()
        boardAttempt boardClass = new boardAttempt(); //instantiate class
        double x = 0;
        double y = 68;
        Polygon hex = boardClass.createHex(x, y);

        assertEquals(6, hex.getPoints().size() / 2); //if correct no of points
        assertEquals(0, hex.getViewOrder()); //if correct view order
        //optional tests
        assertEquals(Color.BLUE, hex.getFill()); //if correct colour
        assertEquals(Color.BLACK, hex.getStroke()); //if correct stroke colour
    }

    @Test
    public void testCreateAtom() { //tests createAtom()
        boardAttempt boardClass = new boardAttempt(); //instantiate class
        double x = 0;
        double y = 68;
        Group atomGroup = (Group) boardClass.createAtom(x, y);

        assertEquals(2, atomGroup.getChildren().size()); //no of children within the group
        assertTrue(atomGroup.getChildren().get(0) instanceof Circle); //tests if first child is radius
        assertTrue(atomGroup.getChildren().get(1) instanceof Circle); //tests if second is atom

        Circle radius = (Circle) atomGroup.getChildren().get(0);
        Circle atom = (Circle) atomGroup.getChildren().get(1);

        assertEquals(x, radius.getCenterX()); //checks coordinates for radius centre
        assertEquals(y, radius.getCenterY()); //should be same as atom
        assertEquals(35.0, radius.getRadius()); //checks radius size
        assertEquals(Color.TRANSPARENT, radius.getFill()); //colour
        assertEquals(Color.BLACK, radius.getStroke()); //stroke colour

        assertEquals(x, atom.getCenterX()); //checks coordinates for atom centre
        assertEquals(y, atom.getCenterY()); //should be same as radius
        assertEquals(10.0, atom.getRadius()); //checks atom radius

        assertEquals(-1.0, atomGroup.getViewOrder()); //checks if correct view order
    }

    /*
    this test does not pass, it's to do with an JavaFX initialisation error (probably)
    it should work, can't figure out why it doesn't
    */
    @Test
    public void testMakeBoard_initialisation() { //tests makeBoard()
        boardAttempt boardClass = new boardAttempt(); //instantiate class
        //call method
        Parent board = boardClass.makeBoard(); //-----------error happens here!!!------------------ D:

        //assert that board is not null
        assertNotNull(board, "Board should not be null");

        //assert that board has children
        assertTrue(board instanceof StackPane, "Board should be an instance of StackPane");
        StackPane stackPane = (StackPane) board;
        assertFalse(stackPane.getChildren().isEmpty(), "Board should have children");

        //assert that board contains at least one hexagon and one atom
        assertTrue(stackPane.getChildren().stream().anyMatch(child -> child instanceof Polygon),
                "Board should contain hexagons");
        assertTrue(stackPane.getChildren().stream().anyMatch(child -> child instanceof Circle),
                "Board should contain atoms");

        int atomCount = (int) ((StackPane) board).getChildren().stream()
                .filter(child -> child instanceof Circle)
                .count();
        assertEquals(4, atomCount, "Board should contain four atoms");
    }
}
