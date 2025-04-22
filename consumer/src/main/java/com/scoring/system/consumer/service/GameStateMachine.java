package com.scoring.system.consumer.service;

import org.springframework.stereotype.Component;

@Component
public class GameStateMachine {

    private int scoreA = 0;
    private int scoreB = 0;

    public void reset() {
        scoreA = 0;
        scoreB = 0;
    }

    public void pointWonBy(String playerId) {
        if (isGameOver()) return;

        if ("A".equals(playerId)) {
            scoreA++;
        } else if ("B".equals(playerId)) {
            scoreB++;
        }
    }

    public String getStatus() {
        if (hasWinner()) {
            return "Player " + leadingPlayer() + " wins the game";
        }

        if (isDeuce()) {
            return "Deuce";
        }

        if (hasAdvantage()) {
            return "Advantage " + leadingPlayer();
        }

        return "Player A : " + toScore(scoreA) + " / Player B : " + toScore(scoreB);
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

    private String leadingPlayer() {
        return scoreA > scoreB ? "A" : "B";
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
