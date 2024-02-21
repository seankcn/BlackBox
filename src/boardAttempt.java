import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.shape.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class boardAttempt extends Application {

    private Polygon createHex(double x, double y){ // create a hexagon with center (x, y)
        Polygon hex = new Polygon();
        hex.getPoints().addAll(new Double[]{
                x, y-20,
                x+17, y-10,
                x+17, y+10,
                x, y+20,
                x-17, y+10,
                x-17, y-10});
        hex.setFill(Color.BLUE);
        hex.setStroke(Color.BLACK);
        hex.setViewOrder(0);
        return hex;
    }
    private Parent createAtom(double x, double y){
        Group g = new Group(); // group together atom and radius
        Circle radius = new Circle();
        radius.setCenterX(x);
        radius.setCenterY(y);
        radius.setRadius(35);
        radius.setFill(Color.TRANSPARENT); // make internals transparent
        radius.setStroke(Color.BLACK);
        Circle atom = new Circle();
        atom.setCenterX(x);
        atom.setCenterY(y);
        atom.setRadius(10);
        g.getChildren().addAll(radius, atom); // add atom and radius to group
        g.setViewOrder(-1); // ensure group is displayed in front of hexagons
        return g;
    }
    private Parent makeBoard() {
        StackPane sp = new StackPane(); // stackpane for centering group
        Group g = new Group(); // group for hexagons and atoms

        Random rand = new Random(); // rand for randomly assigning atoms
        Set<Integer> myatoms = new HashSet<Integer>(); // use set so no duplicate positions
        while(myatoms.size() < 4){myatoms.add(rand.nextInt(61));} // add atoms until done

        Integer count = 0;
        double x, y;
        for(int i = 0; i < 9; i++) { // iterate through rows
            double k = 4 - Math.abs(4-i); // find how many hexagons for this row
            for (double j = 0; j < 5+k; j++) { // iterate through columns
                x = (j + 2 - (k/2)) * 34; // set x pos
                y = i * 30; // set y pos

                Label coords = new Label();
                if(i < 5) {
                    coords.setText(i + " " + (int) j);
                }else{
                    coords.setText(i + " " + (int) (j-k+4));
                }
                coords.setLayoutX(x);
                coords.setLayoutY(y);
                coords.setViewOrder(-1); // label each hexagon for development purposes

                g.getChildren().addAll(createHex(x, y), coords); // create hexagon and add to group

                if(myatoms.contains(count)){ // if atom should be here
                    g.getChildren().addAll(createAtom(x, y)); // add atom to group in current position
                }
                count++;
            }
        }
        sp.getChildren().addAll(g); // add group to stackframe
        return sp; // return stackframe
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(makeBoard(), 600, 600)); // build stage
        stage.show(); // show
    }
}