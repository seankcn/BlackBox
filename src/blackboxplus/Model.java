package blackboxplus;
import java.util.*;

import static java.lang.Math.abs;

public class Model {
    View myGUI; // store view in order to call GUI functions
    List<Integer> rayPoints; // list to store ray movements through recursive functions
    char[][] board = new char[11][11]; // board model
    public int[][] sundial = {{1,1}, {1,0}, {0,-1}, {-1,-1}, {-1, 0}, {0, 1}}; // incrementing shifts direction clockwise in model
    public int getDirection(int i, int j){ // translates [i,j] board direction from model into compass index
        int dir;
        for(dir = 0; dir < sundial.length; dir++){
            if(sundial[dir][0] == i && sundial[dir][1] == j){ // find the index of the direction
                return dir;
            }
        }
        return -1;
    }
    public void createBoard(Set<Integer> myatoms){
        for(int i = 0; i < board.length; i++) { Arrays.fill(board[i], 'n'); } // fill board model
        Integer count = 0;
        int coordx, coordy;
        for(int i = 0; i < 9; i++) { // iterate through rows
            double k = 4 - Math.abs(4-i); // find how many columns for this row
            for (double j = 0; j < 5+k; j++) { // iterate through columns
                coordx = i;
                if(i < 5) {
                    coordy = (int) j;
                }else{
                    coordy = (int) (j-k+4);
                }

                if(myatoms.contains(count)){ // if atom should be here
                    board[coordx+1][coordy+1] = 'a';
                }else{
                    board[coordx+1][coordy+1] = 'e'; // populate model with empty hexagons
                }
                count++;
            }
        }
        makeFields(board);
    }
    public void makeFields(final char[][] list){ // add fields to board model
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
    public void deflectRay(int x, int y, int i, int j){
        int dir = getDirection(i, j); // find index of direction
        int rotations = 0, newdir, newi, newj;

        int[] anticlockwise, clockwise; // find directions either side in front of ray
        if(dir == 0){
            clockwise = sundial[dir+1];
            anticlockwise = sundial[5];
        }else if(dir == 5){
            clockwise = sundial[0];
            anticlockwise = sundial[dir-1];
        }else{
            clockwise = sundial[dir+1];
            anticlockwise = sundial[dir-1];
        }

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
                rayPoints.add(dir); // add point to ray
                System.out.println("Direct Hit");
                return; // done
            }
        }
        if(rotations == 0){rotations = 3;} // if in field with no atom in front means edge of board, so turn 180
        if(dir+rotations < 0){ // apply rotations to current direction
            newdir = 6 + rotations;
        }else if(dir+rotations > 5){
            newdir = dir+rotations-6;
        }else{
            newdir = dir+rotations;
        }
        newi = sundial[newdir][0]; // find new i and j direction
        newj = sundial[newdir][1];
        rayPoints.add(newdir); // add new point to ray
        shootRay(x+newi, y+newj, newi, newj); // shoot new ray
    }

    public void shootRay(int x, int y, int i, int j){ // function for shooting ray with direction [i,j] from position [x,y]
        int xpos = x;
        int ypos = y;
        int dir = getDirection(i, j); // get index of direction

        while(board[xpos][ypos] == 'e'){ // while at an empty hexagon
            xpos+=i; // move
            ypos+=j;
            rayPoints.add(dir); // add new point to ray movements
        }
        if(board[xpos][ypos] == 'n'){ // if at null space, then exited
            System.out.println("Exited at position " + (xpos-1) + "," + (ypos-1));
        }
        if(board[xpos][ypos] == 'f'){ // hit field
            deflectRay(xpos, ypos, i, j); // call function to execute ray deflection
        }
        if(board[xpos][ypos] == 'a'){ // hit atom
            System.out.println("Direct Hit");
        }
    }
    public void startRay(double realX, double realY, int x, int y, int i, int j){ // function for creating ray components
        rayPoints = new ArrayList<>(); // new list for ray movements
        int dir = getDirection(i, j); // get direction ray is being sent
        rayPoints.add(dir); // add to list (so ray appears outside of box initially)
        shootRay(x+1, y+1, i, j); // shoot ray
        myGUI.createRayGUI(realX-myGUI.compass[dir][0], realY-myGUI.compass[dir][1], rayPoints); // display ray
    }
    public Model(Set<Integer> atomLoc, View myGUI){ // model constructor
        createBoard(atomLoc); // create board with atoms
        this.myGUI = myGUI; // store view for GUI manipulation
    }
}
