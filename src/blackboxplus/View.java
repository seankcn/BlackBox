package blackboxplus;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.stage.Stage;

import java.util.*;

import static java.lang.Math.*;
import static java.lang.Math.signum;

public class View extends Application implements EventHandler<ActionEvent> {
    Group boardGUI = new Group(); // group for hexagons, atoms & rays
    StackPane baseStackPane = new StackPane(); // stackpane for centering group & buttons
    List<Node> atoms = new ArrayList<>(); // list for altering all atoms (making visible)
    List<Polyline> rays = new ArrayList<>(); // list for altering all rays (making visible)
    Set<Integer> atomLocs; // list of atom positions
    double hexRadius = 15; // contant determining size of everything
    int NUMOFATOMS = 4; // constant determining number of guesses
    double edge = (Math.sqrt(3)/2) * 2 * hexRadius;
    public double[][] compass = {{edge, 3*hexRadius},{-edge, 3*hexRadius},{-2*edge, 0},{-edge, -3*hexRadius},{edge, -3*hexRadius},{2*edge, 0}}; // incrementing moves direction clockwise
    Polygon[] guesses = new Polygon[4];
    int numOfGuesses = 0;
    Button submitGuessButton = new Button();
    int myin1 = 1;
    int myin2 = 54;
    boolean currentlyGuessing = false;
    Player player;
    Model myModel; // model for board

    @Override
    public void start(Stage primaryStage) throws Exception {
        Background spBackground = new Background(new BackgroundFill(Color.DARKSLATEGREY, CornerRadii.EMPTY, Insets.EMPTY));
        baseStackPane.getChildren().addAll(makeBoard()); // add group to stackpane
        //Button button = setStartButton(); // creates the start button
        baseStackPane.getChildren().add(setGuessButton());
        baseStackPane.getChildren().add(setSubmitGuessButton());
        System.out.println("Click entry points to shoot rays");

        Scene scene = new Scene(baseStackPane, 600, 600);
        baseStackPane.setBackground(spBackground); // Background needed to change the background color because sp blocks the scene
        primaryStage.setTitle("BlackBox+");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public Polygon createHex(double x, double y){ // create a hexagon with center (x, y)
        Polygon hex = new Polygon();
        hex.getPoints().addAll(new Double[]{ // set dimensions
                x, y-(2*hexRadius),
                x+edge, y-hexRadius,
                x+edge, y+hexRadius,
                x, y+(2*hexRadius),
                x-edge, y+hexRadius,
                x-edge, y-hexRadius});
        hex.setFill(Color.BLACK);
        hex.setStroke(Color.YELLOW);
        hex.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if(currentlyGuessing) {
                guessAtomLocations(hex);
            }
        });
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
        radius.setStroke(Color.GOLD);
        Circle atom = new Circle();
        atom.setFill(Color.RED);
        atom.setStroke(Color.BLACK);
        atom.setCenterX(x);
        atom.setCenterY(y);
        atom.setRadius(hexRadius);
        //radiiOfAtoms.getChildren().addAll(radius);
        g.getChildren().addAll(radius, atom); // add atom and radius to group
        g.setViewOrder(-1); // ensure group is displayed in front of hexagons
        g.setVisible(false); // hide atoms
        //atomNum++;
        atoms.add(g);
        return g;
    }
    public Parent createIn(double xto, double yto, double xfrom, double yfrom, int num, int row, int col){
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
        label.setLayoutY(yfrom-10);
        label.setText(String.valueOf(num));
        label.setViewOrder(-2);
        label.setBackground(Background.fill(Color.DARKSLATEGREY));
        label.setTextFill(Color.BLACK);
        StringBuilder myid = new StringBuilder(row + "," + col + "," + (int)signum(yto-yfrom) + ",");
        if(yto == yfrom){
            myid.append((int)signum(xto-xfrom));
        }else if(yto > yfrom){
            myid.append((int)max(0.0, signum(xto-xfrom)));
        }else{
            myid.append((int)min(0.0, signum(xto-xfrom)));
        }
        label.setId(myid.toString());
        label.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                String[] nums = label.getId().split(",");
                int[] myargs = new int[4];
                for(int i = 0; i < 4; i++){
                    myargs[i] = Integer.parseInt(nums[i]);
                }
                myModel.startRay(xto, yto, myargs[0], myargs[1], myargs[2], myargs[3]); // shoot ray
                player.incrementRaysShot();
            }
        });
        g.getChildren().addAll(pl, label);
        g.setViewOrder(1);
        return g;
    }
    public Parent makeBoard() {
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

                boardGUI.getChildren().addAll(createHex(x, y), coords); // create hexagon and add to group
                if(i == 0 || i == 8 || j == 0 || j == 4 + k){ //outerHexagons, create labels for outersides
                    boardGUI.getChildren().addAll(setLabels(x, y, coordx, coordy).getChildren());
                }
                if(atomLocs.contains(count)){ // if atom should be here
                    boardGUI.getChildren().addAll(createAtom(x, y)); // add atom to group in current position
                }
                count++;
            }
        }
        return boardGUI; // return group
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
        baseStackPane.getChildren().add(sPane);
    }
    @Override
    public void handle(ActionEvent actionEvent) {} // have to implement this method because of EventHandler
    //function to set labels on outsides of the board
    public Group setLabels(double x, double y, int row, int col){
        Group outerHexG = new Group();
        double e1 = edge*0.75, e2 = edge*1.25;

        if(row != 8 && col == 0){ //upperleft 1,0 - 4,0
            outerHexG.getChildren().addAll(
                    createIn(x, y, x-e1, y-e2, myin1++, row, col),
                    createIn(x, y, x-e1-e1, y, myin1++, row, col)); //a
        }if(row == 4 && col == 0){ //4,0
            outerHexG.getChildren().addAll(
                    createIn(x, y, x-e1, y+e2, myin1++, row, col));
        }
        for(int i = 5; i < 9; i++){ //bottomleft 4,0 - 8,4
            for(int j = 0; j < 5; j++){
                if(row == i && col == j){
                    outerHexG.getChildren().addAll(
                            createIn(x, y, x-e1-e1, y, myin1++, row, col),
                            createIn(x, y, x-e1, y+e2, myin1++, row, col));
                }
            }
        }if(row == 8 && col == 4){ //8,4
            outerHexG.getChildren().addAll(
                    createIn(x, y, x+e1/2, y+e2, myin1++, row, col));
        }
        if(row == 8 && col != 4){ //bottom 8,5 - 8,8
            outerHexG.getChildren().addAll(
                    createIn(x, y, x-e1, y+e1+e1, myin1++, row, col),
                    createIn(x, y, x+e1/2, y+e1+e1, myin1++, row, col));
        }if(col == 8 && row == 8){ //8,8
            outerHexG.getChildren().addAll(
                    createIn(x, y, x+e2, y, myin1++, row, col));
        }if(row == 0 && col == 0){ //0,0
            outerHexG.getChildren().addAll(
                    createIn(x, y, x+e1/2, y-e2, myin2--, row, col));
        }
        if(row == 0 && col != 0){ //upper 0,0 - 0,4
            outerHexG.getChildren().addAll(
                    createIn(x, y, x-e1, y-e1-e1, myin2--, row, col),
                    createIn(x, y, x+e1/2, y-e1-e1, myin2--, row, col));
        }if(row == 0 && col == 4){ //0,4
            outerHexG.getChildren().addAll(
                    createIn(x, y, x+e2, y, myin2--, row, col)); //a
        }
        for(int i = 1; i < 5; i++){ //upperright 1,5 - 4,8
            for(int j = 5; j < 9; j++){
                if(row == i && col == j){
                    outerHexG.getChildren().addAll(
                            createIn(x, y, x+e1, y-e2, myin2--, row, col),
                            createIn(x, y, x+e2, y, myin2--, row, col));
                }
            }
        }if(row == 4 && col == 8){ //4,8
            outerHexG.getChildren().addAll(
                    createIn(x, y, x+e1, y+e2, myin2--, row, col));
        }if(row != 8 && row != 4 && col == 8){ //bottomright 4,8 - 7,8
            outerHexG.getChildren().addAll(
                    createIn(x, y, x+e2, y, myin2--, row, col),
                    createIn(x, y, x+e1, y+e2, myin2--, row, col));
        }
        return outerHexG;
    }
    private Button setGuessButton() {
        Button guessButton = new Button();
        guessButton.setText("Click to toggle guesses!");
        guessButton.setTranslateY(-250);
        guessButton.setViewOrder(-2);
        EventHandler<ActionEvent> event = actionEvent -> {
            currentlyGuessing = !currentlyGuessing;
        };
        guessButton.setOnAction(event);
        return guessButton;
    }
    private Button setSubmitGuessButton() {
        submitGuessButton.setText("Submit guesses?");
        submitGuessButton.setTranslateY(-250);
        submitGuessButton.setTranslateX(150);
        submitGuessButton.setViewOrder(-2);
        submitGuessButton.setVisible(false);
        EventHandler<ActionEvent> event = actionEvent -> {
            checkIfGuessesCorrect();
            System.out.println(player.getPlayerInfo()); // Finish for final project
            makeAtomsVisible();
            showRays();
        };
        submitGuessButton.setOnAction(event);
        return submitGuessButton;
    }
    public void guessAtomLocations(Polygon guess) { // called by an EventHandler in createHex()
        if(numOfGuesses < NUMOFATOMS) {
            if(guess.getFill() == Color.BLACK) {
                guesses[numOfGuesses++] = guess;
                guess.setFill(Color.RED);
            } else {
                guess.setFill(Color.BLACK);
                numOfGuesses--;
            }
        } else {
            if(guess.getFill() == Color.RED) {
                numOfGuesses--;
            }
            guess.setFill(Color.BLACK);
        }

        if(numOfGuesses == NUMOFATOMS) {
            submitGuessButton.setVisible(true);
        }
    }
    public void checkIfGuessesCorrect() {
        // To be implemented later

        // After all guesses are made reveal the atom locations
        // This method will make each atom check the color of the hex its in
        // - If it's red, correct guess
        // - Subtract the amount of correct guesses from total guesses for misses
        // Add the appropriate amount to the score
    }
    public void createRayGUI(double x, double y, List<Integer> movements){ // function to create ray visually using list of movements
        double[] points = new double[(movements.size()+1)*2];
        points[0] = x;
        points[1] = y;
        int pointslen = 2;
        for(Integer i : movements){ // for each movement
            x = x + compass[i][0];
            y = y + compass[i][1]; // move x and y to new position
            points[pointslen++] = x;
            points[pointslen++] = y;
        }
        Polyline v1 = new Polyline(points);
        v1.setViewOrder(-2);
        v1.setStrokeWidth(3);
        v1.setStroke(Color.GREEN);
        v1.setVisible(false);
        boardGUI.getChildren().addAll(v1);
        rays.add(v1);
    }
    public void showRays(){
        for(Polyline p : rays){
            p.setVisible(true); // make each ray visible
        }
    }
    public void makeAtomsVisible(){
        for(Node atom : atoms){
            atom.setVisible(true);
        }
    }
    public View(){
        Random rand = new Random(); // rand for randomly assigning atoms
        Set<Integer> myatoms = new HashSet<Integer>(); // use set so no duplicate positions
        while(myatoms.size() < NUMOFATOMS){myatoms.add(rand.nextInt(61));} // add atoms until done

        this.player = new Player("test");
        this.atomLocs = myatoms;
        this.myModel = new Model(atomLocs, this);
    }
}
