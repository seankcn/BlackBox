import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.shape.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class boardAttempt extends Application implements EventHandler<ActionEvent> {
    StackPane sp = new StackPane(); // stackpane for centering group
    char[][] board = new char[9][9];
    double hexRadius = 15; // can change size of everything by altering this variable
    double edge = (Math.sqrt(3)/2) * 2 * hexRadius;

    public Polygon createHex(double x, double y){ // create a hexagon with center (x, y)
        Polygon hex = new Polygon();
        hex.getPoints().addAll(new Double[]{ // set dimensions
                x, y-(2*hexRadius),
                x+edge, y-hexRadius,
                x+edge, y+hexRadius,
                x, y+(2*hexRadius),
                x-edge, y+hexRadius,
                x-edge, y-hexRadius});
        hex.setFill(Color.BLUE);
        hex.setStroke(Color.BLACK);
        hex.setViewOrder(0);
        return hex;
    }
    public Parent createAtom(double x, double y){
        Group g = new Group(); // group together atom and radius
        Circle radius = new Circle();
        radius.setCenterX(x);
        radius.setCenterY(y);
        radius.setRadius(2*edge);
        radius.setFill(Color.TRANSPARENT); // make internals transparent
        radius.setStroke(Color.BLACK);
        Circle atom = new Circle();
        atom.setCenterX(x);
        atom.setCenterY(y);
        atom.setRadius(hexRadius);
        g.getChildren().addAll(radius, atom); // add atom and radius to group
        g.setViewOrder(-1); // ensure group is displayed in front of hexagons
        return g;
    }
    public Parent createIn(double xto, double yto, double xfrom, double yfrom, int num){
        Group g = new Group();
        Polyline pl = new Polyline();
        pl.getPoints().addAll(new Double[]{
                xto,yto,
                xfrom,yfrom});
        pl.setFill(Color.BLACK);
        pl.setStroke(Color.BLACK);
        pl.setViewOrder(1);

        Label label = new Label();
        label.setLayoutX(xfrom);
        label.setLayoutY(yfrom-12);
        label.setText(String.valueOf(num));
        label.setViewOrder(-1);

        g.getChildren().addAll(pl, label);
        g.setViewOrder(1);
        return g;
    }
    public Parent makeBoard() {
        Group g = new Group(); // group for hexagons and atoms

        Random rand = new Random(); // rand for randomly assigning atoms
        Set<Integer> myatoms = new HashSet<Integer>(); // use set so no duplicate positions
        while(myatoms.size() < 4){myatoms.add(rand.nextInt(61));} // add atoms until done
        for(int i = 0; i < board.length; i++) {Arrays.fill(board[i], 'n');} // fill board model

        Integer count = 0;
        double x, y;
        int coordx, coordy;
        for(int i = 0; i < 9; i++) { // iterate through rows
            double k = 4 - Math.abs(4-i); // find how many columns for this row
            for (double j = 0; j < 5+k; j++) { // iterate through columns
                x = (j + 2 - (k/2)) * 2*edge; // set x pos
                y = i * 3 * hexRadius; // set y pos

                Label coords = new Label(); // remove before final submission
                coordx = i;
                if(i < 5) {
                    coordy = (int) j;
                }else{
                    coordy = (int) (j-k+4);
                }
                coords.setText(coordx + " " + coordy);
                coords.setLayoutX(x);
                coords.setLayoutY(y);
                coords.setViewOrder(-1); // label each hexagon for development purposes

                g.getChildren().addAll(createHex(x, y), coords); // create hexagon and add to group

                if(myatoms.contains(count)){ // if atom should be here
                    g.getChildren().addAll(createAtom(x, y)); // add atom to group in current position
                    board[coordx][coordy] = 'a';
                }else{
                    board[coordx][coordy] = 'e'; // populate model
                }
                count++;
            }
        }
        return g; // return group
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        sp.getChildren().addAll(makeBoard()); // add group to stackpane
        Button button = setStartButton(); // creates the start button
        sp.getChildren().add(button);


        Scene scene = new Scene(sp, 600, 600);
        primaryStage.setTitle("BlackBox+");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button setStartButton() { // creates the start button
        Button startButton = new Button();
        startButton.setText("Click to start!");
        startButton.setTranslateY(-200);
        startButton.setViewOrder(-2);
        EventHandler<ActionEvent> event = actionEvent -> {
            Main.startOfGame();
            startButton.setVisible(false);
            rayInput();
        };
        startButton.setOnAction(event);
        return startButton;
    }

    private void rayInput() {
        StackPane sPane = new StackPane(); // stackpane for changing alignment
        TextField input = new TextField("");
        input.setViewOrder(-2);
        input.setMaxWidth(100);
        input.setLayoutY(200);
        EventHandler<ActionEvent> event = actionEvent -> {
            Label label = new Label(input.getText());
            System.out.println(label.getText());
            input.clear();
        };
        input.setOnAction(event);
        sPane.getChildren().add(input);
        sPane.setAlignment(Pos.BOTTOM_CENTER);
        sp.getChildren().add(sPane);
    }

    @Override
    public void handle(ActionEvent actionEvent) { // have to implement this method because of EventHandler

    }
}