package blackboxplus;
import java.util.*;

import static java.lang.Math.abs;

public class Model {
    public static int[][] MODEL_DIRECTIONS = { // incrementing shifts direction clockwise
            {1,1}, {1,0}, {0,-1}, 
            {-1,-1}, {-1, 0}, {0, 1}};
    private View gameView; // store view in order to call GUI functions
    private char[][] board = new char[11][11]; // board model

    public int getDirectionIndex(int x, int y){
        int directionIndex = -1;
        for(int n = 0; n < MODEL_DIRECTIONS.length; n++){
            if(MODEL_DIRECTIONS[n][0] == x && MODEL_DIRECTIONS[n][1] == y){
                directionIndex = n;
            }
        }
        return directionIndex;
    }
    public int turnIndexClockwise(int directionIndex, int turns){
        int newDirectionIndex = directionIndex + turns;
        while(newDirectionIndex < 0){
            newDirectionIndex += 6;
        }
        while(newDirectionIndex > 5){
            newDirectionIndex -= 6;
        }
        return newDirectionIndex;
    }
    protected void createBoard(Set<Integer> myatoms){
        Integer hexagonCount = 0;
        int x, y;
        fillBoardWithN();
        for(int i = 0; i < 9; i++) { // iterate through rows
            double k = 4 - Math.abs(4-i); // find how many columns for this row
            for (double j = 0; j < 5+k; j++) { // iterate through columns
                x = i;
                if(i < 5) {
                    y = (int) j;
                }else{
                    y = (int) (j-k+4);
                }

                if(myatoms.contains(hexagonCount++)){ // if atom should be here
                    board[x+1][y+1] = 'a'; // increment x and y to give 1 line 'n' buffer around outside of model
                }else{
                    board[x+1][y+1] = 'e'; // populate model with empty hexagons
                }
            }
        }
        makeFields(board);
    }
    private void fillBoardWithN(){
        for(int i = 0; i < board.length; i++) {
            Arrays.fill(board[i], 'n');
        }
    }
    protected void makeFields(final char[][] list){ // add fields to board model
        for(int i = 1; i < list.length-1; i++){
            for(int j = 1; j < list.length-1; j++){
                if(list[i][j] == 'a'){
                    if(list[i-1][j] == 'e'){ list[i-1][j] = 'f';}
                    if(list[i][j-1] == 'e'){ list[i][j-1] = 'f';}
                    if(list[i-1][j-1] == 'e'){ list[i-1][j-1] = 'f';}
                    if(list[i+1][j] == 'e'){ list[i+1][j] = 'f';}
                    if(list[i][j+1] == 'e'){ list[i][j+1] = 'f';}
                    if(list[i+1][j+1] == 'e'){ list[i+1][j+1] = 'f';}
                }
            }
        }
    }
    private List<Integer> deflectRay(int x, int y, int i, int j){
        List<Integer> rayPoints = new ArrayList<>();
        int directionIndex = getDirectionIndex(i, j); // find index of direction
        int rotations = 0, newdir, newi, newj;

        int[] anticlockwise = MODEL_DIRECTIONS[turnIndexClockwise(directionIndex, -1)];
        int[] clockwise = MODEL_DIRECTIONS[turnIndexClockwise(directionIndex, 1)];

        if(board[x+anticlockwise[0]][y+anticlockwise[1]] == 'a'){ // if atom at anticlockwise hex
            rotations = 1; // turn 60 degrees clockwise
        }
        if(board[x+clockwise[0]][y+clockwise[1]] == 'a'){ // if atom at clockwise hex
            if(rotations == 1){ // and anticlockwise
                rotations = 3; // turn 180 degrees
            }else{
                rotations = -1; // turn 60 degrees anticlockwise
            }
        }
        if(board[x+i][y+j] == 'a') { // if atom in front hex
            if(abs(rotations) == 1){ // and 1 other atom in clockwise OR anticlockwise hex
                rotations *= 2; // turn 120 degrees in whichever direction you were already turning
            }else if(rotations == 0) { // if no other atoms
                rayPoints.add(directionIndex); // add point to ray
                System.out.println("Direct Hit");
                return rayPoints; // done
            }
        }
        if(rotations == 0){rotations = 3;} // if in field with no atom in front means edge of board, so turn 180
        newdir = turnIndexClockwise(directionIndex, rotations);
        newi = MODEL_DIRECTIONS[newdir][0]; // find new i and j direction
        newj = MODEL_DIRECTIONS[newdir][1];
        rayPoints.add(newdir); // add new point to ray
        rayPoints.addAll(shootRay(x+newi, y+newj, newi, newj)); // shoot new ray
        return rayPoints;
    }

    protected List<Integer> shootRay(int x, int y, int i, int j){ // function for shooting ray with direction [i,j] from position [x,y]
        List<Integer> rayPoints = new ArrayList<>();
        int directionIndex = getDirectionIndex(i, j);

        while(board[x][y] == 'e'){ // while at an empty hexagon
            x += i;
            y += j;
            rayPoints.add(directionIndex); // add new point to ray movements
        }
        if(board[x][y] == 'n'){ // if at null space, then exited
            System.out.println("Exited at position " + (x-1) + "," + (y-1));
        }
        if(board[x][y] == 'f'){ // hit field
            rayPoints.addAll(deflectRay(x, y, i, j)); // call function to execute ray deflection
        }
        if(board[x][y] == 'a'){ // hit atom
            System.out.println("Direct Hit");
        }
        return rayPoints;
    }
    public void startRay(double guiStartX, double guiStartY, int x, int y, int i, int j){ // function for creating ray components
        List<Integer> rayPoints = new ArrayList<>();
        int startingDirection = getDirectionIndex(i, j);
        rayPoints.add(startingDirection); // add starting direction to list (so ray appears outside of box initially)
        rayPoints.addAll(shootRay(x+1, y+1, i, j));
        guiStartX -= gameView.GUI_DIRECTIONS[startingDirection][0];
        guiStartY -= gameView.GUI_DIRECTIONS[startingDirection][1]; // set starting x and y outside of box
        gameView.createRayGUI(guiStartX, guiStartY, rayPoints); // display ray
    }
    public Model(Set<Integer> atomLoc, View gameView){
        createBoard(atomLoc);
        this.gameView = gameView; // store view for GUI manipulation
    }
    public Model(){ // model constructor for unit tests
    }
    public Model(char[][] b){ // model constructor for unit tests
        board = b; //initialises board to be tested
    }

    public char[][] getBoard() {
        return board;
    }
}
