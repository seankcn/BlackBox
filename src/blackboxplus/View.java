package blackboxplus;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    public static int NUMOFATOMS = 4; // constant determining number of atoms and guesses allowed
    public static double HEXAGON_RADIUS = 15; // contant determining size of everything
    public static double HEXAGON_SIDE_OFFSET = (Math.sqrt(3)/2) * 2 * HEXAGON_RADIUS;
    public static double[][] GUI_DIRECTIONS = { // incrementing moves direction clockwise
            {HEXAGON_SIDE_OFFSET, 3*HEXAGON_RADIUS},
            {-HEXAGON_SIDE_OFFSET, 3*HEXAGON_RADIUS},
            {-2*HEXAGON_SIDE_OFFSET, 0},
            {-HEXAGON_SIDE_OFFSET, -3*HEXAGON_RADIUS},
            {HEXAGON_SIDE_OFFSET, -3*HEXAGON_RADIUS},
            {2*HEXAGON_SIDE_OFFSET, 0}};
    protected Group boardGroup = new Group(); // group for hexagons, atoms & rays
    private List<Node> atomList = new ArrayList<>(); // list for altering all atoms (making visible)
    private List<Polyline> rayList = new ArrayList<>(); // list for altering all rays (making visible)
    private Set<Integer> atomIndexSet; // set of hexagon indexes where atoms are contained
    private Polygon[] hexagonsGuessed = new Polygon[4];
    private int numOfGuesses = 0;
    private Button guessButton;
    private Button submitGuessButton = new Button();
    private int myin1 = 1;
    private int myin2 = 54; // variables tracking label indexes
    private boolean currentlyGuessing = false;
    private boolean gameFinished = false;
    private Player gamePlayer;
    private Model gameModel;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Background spBackground = new Background(new BackgroundFill(Color.DARKSLATEGREY, CornerRadii.EMPTY, Insets.EMPTY));
        StackPane baseStackPane = new StackPane();
        baseStackPane.getChildren().addAll(makeBoard()); // add group to stackpane
        baseStackPane.getChildren().add(setGuessButton());
        baseStackPane.getChildren().add(setSubmitGuessButton());
        System.out.println("Click entry points to shoot rays");

        Scene scene = new Scene(baseStackPane, 600, 600);
        baseStackPane.setBackground(spBackground); // Background needed to change the background color because sp blocks the scene
        primaryStage.setTitle("BlackBox+");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    protected Polygon createHexagon(double x, double y){ // create a hexagon with center (x, y)
        Polygon hexagon = new Polygon();
        hexagon.getPoints().addAll(new Double[]{ // set dimensions
                x, y-(2*HEXAGON_RADIUS),
                x+HEXAGON_SIDE_OFFSET, y-HEXAGON_RADIUS,
                x+HEXAGON_SIDE_OFFSET, y+HEXAGON_RADIUS,
                x, y+(2*HEXAGON_RADIUS),
                x-HEXAGON_SIDE_OFFSET, y+HEXAGON_RADIUS,
                x-HEXAGON_SIDE_OFFSET, y-HEXAGON_RADIUS});
        hexagon.setFill(Color.BLACK);
        hexagon.setStroke(Color.YELLOW);
        hexagon.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if(currentlyGuessing && !gameFinished) {
                guessAtomLocations(hexagon);
            }
        });
        hexagon.setViewOrder(0);
        return hexagon;
    }
    protected Parent createAtom(double x, double y){
        Group g = new Group(); // group together atom and radius
        Circle atom = drawAtom(x, y);
        Circle deflectionField = drawDeflectionField(x, y);
        g.getChildren().addAll(deflectionField, atom);
        g.setViewOrder(-1); // ensure group is displayed in front of hexagons
        g.setVisible(false);
        atomList.add(g);
        return g;
    }
    private Circle drawAtom(double x, double y){
        Circle atom = new Circle();
        atom.setFill(Color.RED);
        atom.setStroke(Color.BLACK);
        atom.setCenterX(x);
        atom.setCenterY(y);
        atom.setRadius(HEXAGON_RADIUS);
        return atom;
    }
    private Circle drawDeflectionField(double x, double y){
        Circle deflectionField = new Circle();
        deflectionField.setCenterX(x);
        deflectionField.setCenterY(y);
        deflectionField.setRadius(2*HEXAGON_SIDE_OFFSET);
        deflectionField.setFill(Color.TRANSPARENT);
        deflectionField.setStroke(Color.GOLD);
        return deflectionField;
    }
    private Parent createIn(double xto, double yto, double xfrom, double yfrom, int num, int row, int col){
        Group g = new Group();
        Polyline pl = drawInputLine(xto, yto, xfrom, yfrom);

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
                handleLabelClick(label, xto, yto);
            }
        });
        g.getChildren().addAll(pl, label);
        g.setViewOrder(1);
        return g;
    }
    private Polyline drawInputLine(double xto, double yto, double xfrom, double yfrom){
        Polyline pl = new Polyline();
        pl.getPoints().addAll(new Double[]{
                xto,yto,
                xfrom,yfrom});
        pl.setFill(Color.BLACK);
        pl.setStroke(Color.BLACK);
        pl.setViewOrder(1);
        return pl;
    }
    private void handleLabelClick(Label label, double xto, double yto){
        String[] nums = label.getId().split(",");
        int[] myargs = new int[4];
        for(int i = 0; i < 4; i++){
            myargs[i] = Integer.parseInt(nums[i]);
        }
        if(!gameFinished){
            gameModel.startRay(xto, yto, myargs[0], myargs[1], myargs[2], myargs[3]);
            gamePlayer.incrementRaysShot();
        }
    }
    protected Parent makeBoard() {
        Integer count = 0;
        double x, y;
        int coordx, coordy;
        for(int i = 0; i < 9; i++) { // iterate through rows
            double k = 4 - Math.abs(4-i); // find how many columns for this row
            for (double j = 0; j < 5+k; j++) { // iterate through columns
                x = (j + 2 - (k/2)) * 2 * HEXAGON_SIDE_OFFSET;
                y = i * 3 * HEXAGON_RADIUS;

                Label coords = new Label();
                coordx = i;
                if(i < 5) {
                    coordy = (int) j;
                }else{
                    coordy = (int) (j-k+4);
                }
                coords.setText(coordx + " " + coordy);
                coords.setLayoutX(x);
                coords.setLayoutY(y);
                coords.setViewOrder(-1);

                boardGroup.getChildren().addAll(createHexagon(x, y), coords); // create hexagon and add to group
                if(i == 0 || i == 8 || j == 0 || j == 4 + k){ //outer Hexagons, create labels for outersides
                    boardGroup.getChildren().addAll(setLabels(x, y, coordx, coordy).getChildren());
                }
                if(atomIndexSet.contains(count)){ // if atom should be here
                    boardGroup.getChildren().addAll(createAtom(x, y)); // add atom to group in current position
                }
                count++;
            }
        }
        return boardGroup; // return group
    }
    @Override
    public void handle(ActionEvent actionEvent) {} // have to implement this method because of EventHandler
    //function to set labels on outsides of the board
    private Group setLabels(double x, double y, int row, int col){
        Group outerHexG = new Group();
        double shortOffset = HEXAGON_SIDE_OFFSET*0.75, longOffset = HEXAGON_SIDE_OFFSET*1.25;

        if(row != 8 && col == 0){ //upperleft 1,0 - 4,0
            outerHexG.getChildren().addAll(
                    createIn(x, y, x-shortOffset, y-longOffset, myin1++, row, col),
                    createIn(x, y, x-(2*shortOffset), y, myin1++, row, col)); //a
        }if(row == 4 && col == 0){ //4,0
            outerHexG.getChildren().addAll(
                    createIn(x, y, x-shortOffset, y+longOffset, myin1++, row, col));
        }
        for(int i = 5; i < 9; i++){ //bottomleft 4,0 - 8,4
            for(int j = 0; j < 5; j++){
                if(row == i && col == j){
                    outerHexG.getChildren().addAll(
                            createIn(x, y, x-(2*shortOffset), y, myin1++, row, col),
                            createIn(x, y, x-shortOffset, y+longOffset, myin1++, row, col));
                }
            }
        }if(row == 8 && col == 4){ //8,4
            outerHexG.getChildren().addAll(
                    createIn(x, y, x+shortOffset/2, y+longOffset, myin1++, row, col));
        }
        if(row == 8 && col != 4){ //bottom 8,5 - 8,8
            outerHexG.getChildren().addAll(
                    createIn(x, y, x-shortOffset, y+(2*shortOffset), myin1++, row, col),
                    createIn(x, y, x+shortOffset/2, y+(2*shortOffset), myin1++, row, col));
        }if(col == 8 && row == 8){ //8,8
            outerHexG.getChildren().addAll(
                    createIn(x, y, x+longOffset, y, myin1++, row, col));
        }if(row == 0 && col == 0){ //0,0
            outerHexG.getChildren().addAll(
                    createIn(x, y, x+shortOffset/2, y-longOffset, myin2--, row, col));
        }
        if(row == 0 && col != 0){ //upper 0,0 - 0,4
            outerHexG.getChildren().addAll(
                    createIn(x, y, x-shortOffset, y-(2*shortOffset), myin2--, row, col),
                    createIn(x, y, x+shortOffset/2, y-(2*shortOffset), myin2--, row, col));
        }if(row == 0 && col == 4){ //0,4
            outerHexG.getChildren().addAll(
                    createIn(x, y, x+longOffset, y, myin2--, row, col)); //a
        }
        for(int i = 1; i < 5; i++){ //upperright 1,5 - 4,8
            for(int j = 5; j < 9; j++){
                if(row == i && col == j){
                    outerHexG.getChildren().addAll(
                            createIn(x, y, x+shortOffset, y-longOffset, myin2--, row, col),
                            createIn(x, y, x+longOffset, y, myin2--, row, col));
                }
            }
        }if(row == 4 && col == 8){ //4,8
            outerHexG.getChildren().addAll(
                    createIn(x, y, x+shortOffset, y+longOffset, myin2--, row, col));
        }if(row != 8 && row != 4 && col == 8){ //bottomright 4,8 - 7,8
            outerHexG.getChildren().addAll(
                    createIn(x, y, x+longOffset, y, myin2--, row, col),
                    createIn(x, y, x+shortOffset, y+longOffset, myin2--, row, col));
        }
        return outerHexG;
    }
    Button setGuessButton() {
        guessButton = new Button();
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
        submitGuessButton.setVisible(false); // invisible until player makes guesses
        EventHandler<ActionEvent> event = actionEvent -> {
            checkIfGuessesCorrect();
            System.out.println(gamePlayer.getPlayerInfo());
            submitGuessButton.setVisible(false);
            guessButton.setVisible(false);
            gameFinished = true;
            makeAtomsVisible();
            bringRaysToFront();
        };
        submitGuessButton.setOnAction(event);
        return submitGuessButton;
    }
    private void guessAtomLocations(Polygon guess) { // called by an EventHandler in createHex()
        if(numOfGuesses < NUMOFATOMS) {
            if(guess.getFill() == Color.BLACK) {
                hexagonsGuessed[numOfGuesses++] = guess;
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

        if(numOfGuesses == NUMOFATOMS && !gameFinished) {
            submitGuessButton.setVisible(true);
        }
    }
    private void checkIfGuessesCorrect() {
        Integer count = 0;
        for (Node node : boardGroup.getChildren()) {
            if(node instanceof Polygon){
                Polygon hex = (Polygon) node;
                if (hex.getFill() == Color.RED){
                    if(!atomIndexSet.contains(count)){
                        gamePlayer.incrementAtomsMissed();
                    }
                }
                count++;
            }
        }
    }
    public void createRayGUI(double x, double y, List<Integer> movements){ // function to create ray visually using list of direction indexes
        double[] points = new double[(movements.size()+1)*2];
        points[0] = x;
        points[1] = y;
        int pointslen = 2;
        for(Integer index : movements){
            x = x + GUI_DIRECTIONS[index][0];
            y = y + GUI_DIRECTIONS[index][1];
            points[pointslen++] = x;
            points[pointslen++] = y;
        }
        Polyline v1 = new Polyline(points);
        v1.setViewOrder(2); // make rays appear behind board
        v1.setStrokeWidth(3);
        v1.setStroke(Color.GREEN);
        boardGroup.getChildren().addAll(v1);
        rayList.add(v1);
    }
    public void bringRaysToFront(){
        for(Polyline p : rayList){
            p.setViewOrder(-2);
        }
    }
    public void makeAtomsVisible(){
        for(Node atom : atomList){
            atom.setVisible(true);
        }
    }
    public View(){
        Random rand = new Random();
        Set<Integer> gameAtoms = new HashSet<>(); // use set to prevent duplicate positions
        while(gameAtoms.size() < NUMOFATOMS){
            gameAtoms.add(rand.nextInt(61));
        }

        Scanner in = new Scanner(System.in);
        System.out.println("Enter Name: ");
        this.gamePlayer = new Player(in.nextLine());
        this.atomIndexSet = gameAtoms;
        this.gameModel = new Model(atomIndexSet, this);
    }

    public View(String playerName) {
        Random rand = new Random(); // rand for randomly assigning atoms
        Set<Integer> myatoms = new HashSet<>(); // use set so no duplicate positions
        while (myatoms.size() < NUMOFATOMS) {
            myatoms.add(rand.nextInt(61)); // add atoms until done
        }

        this.gamePlayer = new Player(playerName);
        this.atomIndexSet = myatoms;
        this.gameModel = new Model(atomIndexSet, this);
    }


}
