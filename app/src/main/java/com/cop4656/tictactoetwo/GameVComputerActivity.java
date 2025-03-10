package com.cop4656.tictactoetwo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;


public class GameVComputerActivity extends AppCompatActivity {


    public TicTacToe_vComputer.DIFFICULTY CHOSEN_DIFFICULTY;

    GridLayout mGameBoard;
    TicTacToe_vComputer mGame;
    TextView mPlayerTurn;
    Button mReplayButton;
    Toolbar mToolbar;
    TextView mWinnerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_vs_computer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.v_computer_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Set up Toolbar so that UP goes to Main Menu
        mToolbar = (Toolbar) findViewById(R.id.vcomputer_toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.vs_computer);

        //Set Difficulty
        String difficulty = getIntent().getStringExtra("DIFFICULTY");
        assert difficulty != null;
        if (difficulty.equals("Hard")){
            CHOSEN_DIFFICULTY = TicTacToe_vComputer.DIFFICULTY.HARD;
        } else {
            CHOSEN_DIFFICULTY = TicTacToe_vComputer.DIFFICULTY.EASY;
        }

        //Assign references to UI widgets
        mGameBoard = findViewById(R.id.game_boardC);
        mPlayerTurn = findViewById(R.id.turn_indicatorC);
        mReplayButton = findViewById(R.id.replay_button2);
        mWinnerText = findViewById(R.id.winner_message_computer);

        //Set Gameboard Listeners
        setGameListeners();

        //Set ReplayButton Listener
        //  Note: This listener is much more complex than GameVsPlayer. This is because the computer
        //  needs to be able to make its move first if it lost the last game.
        mReplayButton.setOnClickListener(l -> {
            //Start new game and update UI
            mGame.newGame();
            updateUI();

            //Reset Gameboard listeners
            setGameListeners();
            mReplayButton.setVisibility(View.INVISIBLE);
            mWinnerText.setVisibility(View.INVISIBLE);

            //If the first move belongs to the computer, find and make move.
            if (mGame.CURRENT_PLAYER == TicTacToe_vComputer.PLAYERS.O){
                removeGameListeners();
                switch (CHOSEN_DIFFICULTY){
                    case EASY:
                        new ComputerEasyMove().execute();
                        break;
                    case HARD:
                        new ComputerHardMove().execute();
                        break;
                }
                setGameListeners();
            }
        });

        mGame = new TicTacToe_vComputer(CHOSEN_DIFFICULTY);
        mGame.newGame();

        //Fill in previous gamestate if applicable
        SharedPreferences pref = getSharedPreferences("resume", Context.MODE_PRIVATE);
        boolean isResumable = pref.getBoolean("resumable", false);
        if (isResumable){
            //Game State
            mGame.setState(pref.getString("gameState", "         "));
            //Set CURRENT_PLAYER
            boolean isX = pref.getBoolean("playerIsX", true);
            if (isX){
                mPlayerTurn.setText(R.string.your_turn);
                mGame.CURRENT_PLAYER = TicTacToe_vComputer.PLAYERS.X;
            } else {
                mPlayerTurn.setText(R.string.computer_turn);
                mGame.CURRENT_PLAYER = TicTacToe_vComputer.PLAYERS.O;
            }

            updateUI();

            //If the first move belongs to the computer, find and make move.
            if (mGame.CURRENT_PLAYER == TicTacToe_vComputer.PLAYERS.O){
                removeGameListeners();
                switch (CHOSEN_DIFFICULTY){
                    case EASY:
                        new ComputerEasyMove().execute();
                        break;
                    case HARD:
                        new ComputerHardMove().execute();
                        break;
                }
                setGameListeners();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private void onGameButtonClick(View view){
        //Compute row and col indices
        int buttonIndex = mGameBoard.indexOfChild(view);
        int row = buttonIndex / TicTacToe_vComputer.GRID_SIZE;
        int col = buttonIndex % TicTacToe_vComputer.GRID_SIZE;

        //If valid move is made
        if (mGame.selectSquare(row, col, mGame.CURRENT_PLAYER)){
            //Update UI after player move and change CURRENT_PLAYER
            updateUI();
            mPlayerTurn.setText(R.string.computer_turn);
            mGame.setCURRENT_PLAYER();

            //If game is not over, Computer makes move
            if (mGame.isGameOver() < 0){
                //Remove Gameboard listeners, so that the player can't put another move while the
                //  computer is thinking of its move. They are replaced later.
                removeGameListeners();

                //find and make Computer move
                switch (CHOSEN_DIFFICULTY){
                    case EASY:
                        new ComputerEasyMove().execute();
                        break;
                    case HARD:
                        new ComputerHardMove().execute();
                        break;
                }

                //Reset Gameboard listeners
                setGameListeners();
            }
        } else {
            Toast.makeText(this, R.string.invalid_move, Toast.LENGTH_SHORT).show();
        }

        //Display WINNER message if game is over
        if (mGame.isGameOver() > 0){
            onGameOver(mGame.isGameOver());
        }
    }

    private void updateUI(){
        for (int buttonIndex = 0; buttonIndex < mGameBoard.getChildCount(); buttonIndex++){
            Button gridButton = (Button) mGameBoard.getChildAt(buttonIndex);

            int row = buttonIndex / TicTacToe_vComputer.GRID_SIZE;
            int col = buttonIndex % TicTacToe_vComputer.GRID_SIZE;

            gridButton.setText(String.valueOf(mGame.getSquareContents(row, col)));
        }
    }

    public void setGameListeners(){
        for (int buttonIndex = 0; buttonIndex < mGameBoard.getChildCount(); buttonIndex++){
            Button gridButton = (Button) mGameBoard.getChildAt(buttonIndex);
            gridButton.setOnClickListener(this::onGameButtonClick);
        }
    }

    public void removeGameListeners(){
        for (int buttonIndex = 0; buttonIndex < mGameBoard.getChildCount(); buttonIndex++){
            Button gridButton = (Button) mGameBoard.getChildAt(buttonIndex);
            gridButton.setOnClickListener(null);
        }
    }

    //AsyncTask nested class that generates a Hard Mode move in a background thread and returns
    //  it to the main thread
    private class ComputerHardMove extends AsyncTask<Void, Void, Integer>{
        //Generate Computer move in background
        @Override
        protected Integer doInBackground (Void... params){
            return mGame.findHardComputerMove(mGame.getState());
        }

        //Code run once move is found.
        @Override
        protected void onPostExecute(Integer move){
            if (move != -1){
                int row = move / TicTacToe_vComputer.GRID_SIZE;
                int col = move % TicTacToe_vComputer.GRID_SIZE;
                mGame.selectSquare(row, col, mGame.CURRENT_PLAYER);
            }

            updateUI();
            mGame.setCURRENT_PLAYER();
            mPlayerTurn.setText(R.string.your_turn);

            if (mGame.isGameOver() > 0){
                onGameOver(mGame.isGameOver());
            }
        }
    }

    //AsyncTask nested class that generates an Easy Mode move in a background thread and returns
    //  it to the main thread.
    private class ComputerEasyMove extends AsyncTask<Void, Void, Integer>{
        //Generate Computer Move in background
        @Override
        protected Integer doInBackground(Void... params){
            return mGame.findEasyComputerMove(mGame.getState());
        }

        //Code run once move is found
        @Override
        protected void onPostExecute(Integer move){
            if (move != -1){
                int row = move / TicTacToe_vComputer.GRID_SIZE;
                int col = move % TicTacToe_vComputer.GRID_SIZE;
                mGame.selectSquare(row, col, mGame.CURRENT_PLAYER);
            }

            updateUI();
            mGame.setCURRENT_PLAYER();
            mPlayerTurn.setText(R.string.your_turn);

            if (mGame.isGameOver() > 0){
                onGameOver(mGame.isGameOver());
            }
        }
    }

    @Override
    public void onPause(){
        super.onPause();

        SharedPreferences pref = getSharedPreferences("resume", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putBoolean("resumable", false);

        if (mGame.isGameOver() < 0){
            editor.putBoolean("resumable", true);
            editor.putString("gameState", mGame.getState());
            editor.putBoolean("playerIsX", mGame.CURRENT_PLAYER == TicTacToe_vComputer.PLAYERS.X);
            editor.putBoolean("gameIsVsPlayer", false);
            if (mGame.CHOSEN_DIFFICULTY == TicTacToe_vComputer.DIFFICULTY.HARD){
                editor.putString("difficulty", "Hard");
            } else {
                editor.putString("difficulty", "Easy");
            }
            editor.apply();
        }
    }

    private void onGameOver(int gameOverState){
        mReplayButton.setVisibility(View.VISIBLE);
        removeGameListeners();

        switch (gameOverState){
            case 1:
                mWinnerText.setText(R.string.you_win);
                break;
            case 2:
                mWinnerText.setText(R.string.computer_wins);
                break;
            case 3:
                mWinnerText.setText(R.string.cat_wins);
                break;
            default:
            break;
        }
        mWinnerText.setVisibility(View.VISIBLE);

        SharedPreferences pref = getSharedPreferences("resume", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("gameState", "");
        editor.putBoolean("resumable", false);
        editor.apply();
    }
}