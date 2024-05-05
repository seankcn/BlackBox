package blackboxplus;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ViewTest {

    @BeforeClass
    public static void initToolkit() {
        //Initialize JavaFX toolkit
        new JFXPanel();
        Platform.setImplicitExit(false);
    }

    @Test
    public void testCreateHex() {
        double centerX = 100.0;
        double centerY = 100.0;

        View view = new View("Sean");
        Polygon hexagon = view.createHex(centerX, centerY);

        //check if hex has same number of vertices
        assertEquals(6, hexagon.getPoints().size() / 2);

        //check properties
        assertEquals(Color.BLACK, hexagon.getFill());
        assertEquals(Color.YELLOW, hexagon.getStroke());
    }

    @Test
    public void testCreateAtom() {
        double centerX = 100.0;
        double centerY = 100.0;

        View view = new View("Sean");
        Group atomGroup = (Group) view.createAtom(centerX, centerY);

        //check if atom contains 2 children
        assertEquals(2, atomGroup.getChildren().size());

        //check if field is created and properties are correct
        Node radiusNode = atomGroup.getChildren().get(0);
        assertTrue(radiusNode instanceof Circle);
        Circle radius = (Circle) radiusNode;
        assertEquals(Color.TRANSPARENT, radius.getFill());
        assertEquals(Color.GOLD, radius.getStroke());

        //check if atom is created and properties
        Node atomNode = atomGroup.getChildren().get(1);
        assertTrue(atomNode instanceof Circle);
        Circle atom = (Circle) atomNode;
        assertEquals(Color.RED, atom.getFill());
        assertEquals(Color.BLACK, atom.getStroke());

    }

    @Test
    public void testMakeBoard() {
        View view = new View("Sean");
        Group boardGroup = (Group) view.makeBoard();

        assertNotNull(boardGroup); //board must exist
        assertEquals(180, view.boardGUI.getChildren().size()); //total num of children

        //count hex and labels
        int numHex = 0;
        int numLabel = 0;
        for (Node node : boardGroup.getChildren()) {
            if (node instanceof Polygon) {
                numHex++;
            } else if (node instanceof Label) {
                numLabel++;
            }
        }

        //check number of hexagons and labels
        assertEquals(61, numHex);
        assertEquals(61, numLabel);

        //verify all hexagon properties
        for (Node node : boardGroup.getChildren()) {
            if (node instanceof Polygon) {
                Polygon hexagon = (Polygon) node;
                assertEquals(Color.BLACK, hexagon.getFill());
                assertEquals(Color.YELLOW, hexagon.getStroke());
            }
        }
    }

    @Test
    public void testSetGuessButton() {
        View view = new View("Sean");
        Button guessButton = view.setGuessButton();

        assertNotNull(guessButton);
        assertEquals("Click to toggle guesses!", guessButton.getText());
    }
}
