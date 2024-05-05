package blackboxplus;
public class Player {
    private final String playerName;
    private int raysShot = 0;
    private int atomsMissed = 0;
    private int score = 0;

    public Player(String name) {
        try {
            this.playerName = name;
        } catch(NullPointerException nullPointerException) {
            throw new IllegalArgumentException("Invalid input!");
        }
    }

    public void incrementScore(int points) {
        score = score + points;
    }

    public void incrementRaysShot() {
        raysShot++;
        incrementScore(1);
    }public void incrementAtomsMissed() {
        atomsMissed++;
        incrementScore(5);
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getPlayerInfo() { // just for tests for now
        return getPlayerName() + " had a score of " + score + " points with " + raysShot + " rays shot in total and " + atomsMissed + " atoms misplaced!";
    }

    public int getScore() {
        return score;
    }

    public int getRaysShot() {
        return raysShot;
    }

    public int getAtomsMissed() {
        return atomsMissed;
    }
}
