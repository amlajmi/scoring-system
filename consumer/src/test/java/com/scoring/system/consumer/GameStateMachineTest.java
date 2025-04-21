package com.scoring.system.consumer;

import org.junit.jupiter.api.Test;

import com.scoring.system.consumer.service.GameStateMachine;

import static org.junit.jupiter.api.Assertions.*;

public class GameStateMachineTest {

    @Test
    public void testBasicProgression() {
    	
        GameStateMachine game = new GameStateMachine();

        game.pointWonBy("A");
        assertEquals("Player A : 15 / Player B : 0", game.getStatus());

        game.pointWonBy("A");
        assertEquals("Player A : 30 / Player B : 0", game.getStatus());

        game.pointWonBy("B");
        assertEquals("Player A : 30 / Player B : 15", game.getStatus());

        game.pointWonBy("A");
        assertEquals("Player A : 40 / Player B : 15", game.getStatus());

        game.pointWonBy("A");
        assertEquals("Player A wins the game", game.getStatus());
    }

    @Test
    public void testDeuceAdvantageWin() {
    	
    	GameStateMachine game = new GameStateMachine();

        game.pointWonBy("A"); // 15-0
        game.pointWonBy("A"); // 30-0
        game.pointWonBy("A"); // 40-0
        game.pointWonBy("B"); // 40-15
        game.pointWonBy("B"); // 40-30
        game.pointWonBy("B"); // Deuce
        assertEquals("Deuce", game.getStatus());

        game.pointWonBy("A");
        assertEquals("Advantage A", game.getStatus());

        game.pointWonBy("B");
        assertEquals("Deuce", game.getStatus());

        game.pointWonBy("B");
        assertEquals("Advantage B", game.getStatus());

        game.pointWonBy("B");
        assertEquals("Player B wins the game", game.getStatus());
    }
}
