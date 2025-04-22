package com.scoring.system.consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.scoring.system.consumer.fsm.GameStateMachine;
import com.scoring.system.consumer.fsm.GameStatus;

public class GameStateMachineTest {

    @Test
    public void testBasicProgression() {
    	
    	// checking detailed score display text
    	 
        GameStateMachine game = new GameStateMachine(false); //disable auto reset

        game.pointWonBy("A");
        assertEquals("Player A : 15 / Player B : 0", game.getStatusMessage());

        game.pointWonBy("A");
        assertEquals("Player A : 30 / Player B : 0", game.getStatusMessage());

        game.pointWonBy("B");
        assertEquals("Player A : 30 / Player B : 15", game.getStatusMessage());

        game.pointWonBy("A");
        assertEquals("Player A : 40 / Player B : 15", game.getStatusMessage());

        game.pointWonBy("A");
        assertEquals("Player A wins the game", game.getStatusMessage());
    }

    @Test
    public void testDeuceAdvantageWin() {
    	
    	// checking game status
    	
        GameStateMachine game = new GameStateMachine(false); // disable auto reset

        game.pointWonBy("A"); // 15-0
        game.pointWonBy("A"); // 30-0
        game.pointWonBy("A"); // 40-0
        game.pointWonBy("B"); // 40-15
        game.pointWonBy("B"); // 40-30
        game.pointWonBy("B"); // Deuce
        assertEquals(GameStatus.DEUCE, game.getStatus());

        game.pointWonBy("A");
        assertEquals(GameStatus.ADVANTAGE_A, game.getStatus());

        game.pointWonBy("B");
        assertEquals(GameStatus.DEUCE, game.getStatus());

        game.pointWonBy("B");
        assertEquals(GameStatus.ADVANTAGE_B, game.getStatus());

        game.pointWonBy("B");
        assertEquals(GameStatus.PLAYER_B_WINS, game.getStatus());
    }

}
