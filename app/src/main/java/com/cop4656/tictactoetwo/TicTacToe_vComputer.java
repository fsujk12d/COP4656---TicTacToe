package com.cop4656.tictactoetwo;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/* TicTacToe_vComputer
 *  This class inherits from TicTacToe_Base but implements a new data type and data member
 *  (DIFFICULTY and CHOSEN_DIFFICULTY, respectively), as well as some methods for determining
 *  computer moves. It provides logic for two computer difficulty levels, Easy and Hard. The Easy
 *  move generator findEasyMove simply selects a random available space, while the Hard move
 *  generator findHardComputerMove uses a Java implementation of the popular Minimax algorithm.
 */
public class TicTacToe_vComputer extends TicTacToe_Base{
    //Difficulty data type and member.
    public static enum DIFFICULTY {EASY, HARD};
    public DIFFICULTY CHOSEN_DIFFICULTY;

    //Constructor - implicitly calls Base constructor, additionally sets chosen difficulty.
    public TicTacToe_vComputer(DIFFICULTY difficulty){
        CHOSEN_DIFFICULTY = difficulty;
    }

    //FindEasyComputerMove - Reads through the gameState string passed as the argument and creates a list
    //  of indices that correspond to open game squares. Then, it randomly selects one of the open
    //  indices and returns that as the index of the Easy Mode move the computer will play.
    public int findEasyComputerMove(String gameState){
        //Make it appear that the computer is taking a minute to think about the move.
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            //Unsure how to handle this exception should it arise...
        }

        List<Integer> validMoves = new ArrayList<>();
        for (int i = 0; i < gameState.length(); i++){
            char c = gameState.charAt(i);
            if (c == ' '){
                validMoves.add(i);
            }
        }

        Random rand = new Random();
        return validMoves.get(rand.nextInt(validMoves.size()));
    }


    //FindHardComputerMove - follows the popular Minimax algorithm for determining best move.
    //  The algorithm is described in detail elsewhere, so a quick Google search replaces what
    //  could be a number of comment lines here.
    public int findHardComputerMove(String gameState) {
        //Pretend to Think about the answer.
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {

        }

        int bestScore = Integer.MIN_VALUE;
        int bestMove = -1;

        for (int i = 0; i < 9; i++) {
            if (gameState.charAt(i) == ' ') {
                String newState = gameState.substring(0, i) + 'O' + gameState.substring(i + 1);
                int score = minimax(newState, 0, false);
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = i;
                }
            }
        }

        return bestMove;
    }

    private int minimax(String gameState, int depth, boolean isMaximizing) {
        if (isWinningState(gameState, 'O')) return 10 - depth;
        if (isWinningState(gameState, 'X')) return depth - 10;
        if (isBoardFull(gameState)) return 0;

        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < 9; i++) {
                if (gameState.charAt(i) == ' ') {
                    String newState = gameState.substring(0, i) + 'O' + gameState.substring(i + 1);
                    int score = minimax(newState, depth + 1, false);
                    bestScore = Math.max(score, bestScore);
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < 9; i++) {
                if (gameState.charAt(i) == ' ') {
                    String newState = gameState.substring(0, i) + 'X' + gameState.substring(i + 1);
                    int score = minimax(newState, depth + 1, true);
                    bestScore = Math.min(score, bestScore);
                }
            }
            return bestScore;
        }
    }

    private boolean isWinningState(String gameState, char player) {
        // Check rows
        for (int i = 0; i < 9; i += 3) {
            if (gameState.charAt(i) == player && gameState.charAt(i+1) == player && gameState.charAt(i+2) == player) {
                return true;
            }
        }

        // Check columns
        for (int i = 0; i < 3; i++) {
            if (gameState.charAt(i) == player && gameState.charAt(i+3) == player && gameState.charAt(i+6) == player) {
                return true;
            }
        }

        // Check diagonals
        if (gameState.charAt(0) == player && gameState.charAt(4) == player && gameState.charAt(8) == player) {
            return true;
        }
        if (gameState.charAt(2) == player && gameState.charAt(4) == player && gameState.charAt(6) == player) {
            return true;
        }

        return false;
    }

    private boolean isBoardFull(String gameState) {
        return !gameState.contains(" ");
    }
}
