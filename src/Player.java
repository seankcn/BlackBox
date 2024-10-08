public class Player {
    private final String playerName;
    private int raysShot = 0;
    private int atomMissed = 0;
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
    }

    public void incrementAtomsMissed() {
        atomMissed++;
        incrementScore(5);
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getPlayerInfo() {
        // Outputs the results of the game
        return getPlayerName() + " had a score of " + score + " points with " + raysShot + " rays shot in total and " + atomMissed + " atoms misplaced!";
    }
}
