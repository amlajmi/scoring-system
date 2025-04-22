package com.scoring.system.consumer.fsm;

public class GameStateMachine {

    private final boolean autoReset;

    private int scoreA = 0;
    private int scoreB = 0;

    public GameStateMachine(boolean autoReset) {
        this.autoReset = autoReset;
    }

    public void reset() {
        scoreA = 0;
        scoreB = 0;
    }

    public void pointWonBy(String playerId) {
        if (isGameOver()) 
        	return;
        if ("A".equals(playerId)) 
        	scoreA++;
        else if ("B".equals(playerId)) 
        	scoreB++;
    }

    public GameStatus getStatus() {
        if (hasWinner())
            return scoreA > scoreB ? GameStatus.PLAYER_A_WINS : GameStatus.PLAYER_B_WINS;

        if (isDeuce()) 
        	return GameStatus.DEUCE;

        if (hasAdvantage()) 
        	return scoreA > scoreB ? GameStatus.ADVANTAGE_A : GameStatus.ADVANTAGE_B;

        return GameStatus.IN_PROGRESS;
    }

    public String getStatusMessage() {
        return switch (getStatus()) {
            case PLAYER_A_WINS -> "Player A wins the game";
            case PLAYER_B_WINS -> "Player B wins the game";
            case DEUCE -> "Deuce";
            case ADVANTAGE_A -> "Advantage A";
            case ADVANTAGE_B -> "Advantage B";
            case IN_PROGRESS -> "Player A : " + toScore(scoreA) + " / Player B : " + toScore(scoreB);
        };
    }

    public boolean shouldAutoReset() {
        return autoReset;
    }

    private boolean hasWinner() {
        return (scoreA >= 4 || scoreB >= 4) && Math.abs(scoreA - scoreB) >= 2;
    }

    private boolean hasAdvantage() {
        return (scoreA >= 3 && scoreB >= 3) && Math.abs(scoreA - scoreB) == 1;
    }

    private boolean isDeuce() {
        return scoreA >= 3 && scoreA == scoreB;
    }

    private boolean isGameOver() {
        return hasWinner();
    }

    private String toScore(int val) {
        return switch (val) {
            case 0 -> "0";
            case 1 -> "15";
            case 2 -> "30";
            case 3 -> "40";
            default -> "40";
        };
    }
}

