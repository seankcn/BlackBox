import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static java.lang.Math.*;

public class boardAttempt extends Application implements EventHandler<ActionEvent> {
    StackPane sp = new StackPane(); // stackpane for centering group
    Group radiiOfAtoms = new Group(); // group containing the atom radii for collision checks
    Group hexagons = new Group(); // group for hexagons and atoms
    int atomNum = 0;
    char[][] board = new char[11][11];
    double hexRadius = 15; // can change size of everything by altering this variable
    double edge = (Math.sqrt(3)/2) * 2 * hexRadius;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Background spBackground = new Background(new BackgroundFill(Color.DARKSLATEGREY, CornerRadii.EMPTY, Insets.EMPTY));
        sp.getChildren().addAll(makeBoard()); // add group to stackpane
        //Button button = setStartButton(); // creates the start button
        //sp.getChildren().add(button);
        System.out.println("Click entry points to shoot rays");

        Scene scene = new Scene(sp, 600, 600);
        sp.setBackground(spBackground); // Background needed to change the background color because sp blocks the scene
        primaryStage.setTitle("BlackBox+");
        primaryStage.setScene(scene);
        primaryStage.show();

        getAtomCoordinates();
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
        radiiOfAtoms.getChildren().addAll(radius);
        g.getChildren().addAll(radius, atom); // add atom and radius to group
        g.setViewOrder(-1); // ensure group is displayed in front of hexagons
        //g.setVisible(false); // hide atoms
        atomNum++;
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
                //System.out.println(label.getId());
                String[] nums = label.getId().split(",");
                int[] myargs = new int[4];
                for(int i = 0; i < 4; i++){
                    myargs[i] = Integer.parseInt(nums[i]);
                }
                shootRay2(myargs[0], myargs[1], myargs[2], myargs[3]);
            }
        });

        g.getChildren().addAll(pl, label);
        g.setViewOrder(1);
        return g;
    }
    public Parent makeBoard() {
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

                hexagons.getChildren().addAll(createHex(x, y), coords); // create hexagon and add to group
                if(i == 0 || i == 8 || j == 0 || j == 4 + k){ //outerHexagons, create labels for outersides
                    hexagons.getChildren().addAll(setLabels(x, y, coordx, coordy).getChildren());
                }
                if(myatoms.contains(count)){ // if atom should be here
                    hexagons.getChildren().addAll(createAtom(x, y)); // add atom to group in current position
                    board[coordx+1][coordy+1] = 'a';
                }else{
                    board[coordx+1][coordy+1] = 'e'; // populate model with empty hexagons
                }
                count++;
            }
        }
        makeFields(board);
        return hexagons; // return group
    }
    public void makeFields(final char[][] list){ // add fields to board model
        for(int i = 1; i < list.length-1; i++){
            for(int j = 1; j < list.length-1; j++){
                if(list[i][j] == 'a'){
                    if(list[i-1][j] == 'e'){ list[i-1][j] = 'f';}
                    if(list[i][j-1] == 'e'){ list[i][j-1] = 'f';}
                    if(list[i][j-1] == 'e' && list[i-1][j] == 'e'){ list[i-1][j-1] = 'f';}
                    if(list[i+1][j] == 'e'){ list[i+1][j] = 'f';}
                    if(list[i][j+1] == 'e'){ list[i][j+1] = 'f';}
                    if(list[i][j+1] == 'e' && list[i+1][j] == 'e'){ list[i+1][j+1] = 'f';}
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
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

    public boolean hasAtom(int i, int j){
        return (board[i][j] == 'a');
    }
    public void getAtomCoordinates(){
        for(int i = 0; i<9; i++){
            double k = 4 - Math.abs(4-i);
            for (double j = 0; j < 5+k; j++) {
                int coordy;

                if(i < 5) {
                    coordy = (int) j;
                }else{
                    coordy = (int) (j-k+4);
                }
                boolean x = hasAtom(i, coordy);
                if(x){
                    System.out.println("There is an atom at (" + i + ", " + coordy + ")");
                }
            }
        }
    }

    int myin1 = 1;
    int myin2 = 54;
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

    // TODO
    public double[] shootRay(double x, double y, double deltaX, double deltaY) {
        System.out.println(x + " " + y + " " + deltaX + " " + deltaY);
        boolean rayStopped = false; // array has either reached the border or been absorbed

        while(!rayStopped) { // need to check every surrounding hex for atoms
            for(int i = -1; i < 2; i++) { // row
                for(int j = -1; j < 1; j++) { // 'column'
                    // check
                    if(x + i < 9 && x + i >=0 && y + j < 9 && y + j >= 0) {
                        //System.out.println("    " + i + " " + j);
                        if(hasAtom((int) x + i, (int) y + j)) {
                            System.out.println("Hit at x: " + (x + i) + " y:" + (y + j));
                            rayStopped = true;
                            break;
                        }
                    }
                    if(i == 0 && j == 0 && y - 1 < 9 && y - 1 >= 0) {
                        if(hasAtom((int) x, (int) y - 1)) {
                            System.out.println("this is meant to be on the second row when we have to check 3 hexagons instead of 2");
                        }
                    }
                }
            }
            System.out.println("new hex \n");

            x += deltaX;
            y += deltaY;
            if(x >= 9 || y >= 9) {
                System.out.println("miss");
                rayStopped = true;
            }
        }

        return new double[]{x, y};
    }
    public void shootRay2(int x, int y, int i, int j){
        int xpos = x+1;
        int ypos = y+1;
        while(board[xpos][ypos] == 'e'){ // while at an empty hexagon
            xpos+=i; // move
            ypos+=j;
        }
        if(board[xpos][ypos] == 'n'){ // if at null space, then exited
            System.out.println("Exited at position " + (xpos-1) + "," + (ypos-1));
        }
        if(board[xpos][ypos] == 'f'){ // hit field
            if(board[xpos+i][ypos+j] == 'a'){ // if heading towards atom, hit
                System.out.println("DIRECT HIT");
            }else{
                System.out.println("bounced"); // bounce off field
                //shootRay2(new values) // recursive call until absorbed or exit box
            }
        }
    }
}