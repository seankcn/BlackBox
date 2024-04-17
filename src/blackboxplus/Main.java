package blackboxplus;
import javafx.application.Application;
public class Main {
    public static void main(String[] args) {
        startOfGame();
    }
    public static void startOfGame() { // the code that runs the game
        System.out.println("Game start");
        Application.launch(View.class);
    }
}
