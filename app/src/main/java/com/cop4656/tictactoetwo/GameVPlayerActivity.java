package com.cop4656.tictactoetwo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Objects;

public class GameVPlayerActivity extends AppCompatActivity {

    GridLayout mGameBoard;
    TicTacToe_vPlayer mGame;
    TextView mPlayerTurn;
    Button mReplayButton;
    Toolbar mToolbar;
    TextView mWinnerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_vplayer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.v_player_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Set up Toolbar so that UP goes to main menu
        mToolbar = findViewById(R.id.vplayer_toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.vs_player);

        //Set references for UI widgets
        mGameBoard = findViewById(R.id.game_board);
        mPlayerTurn = findViewById(R.id.turn_indicator);
        mReplayButton = findViewById(R.id.replay_button);
        mWinnerText = findViewById(R.id.winner_message_player);

        setGameListeners();

        //Set ReplayButton Listener
        mReplayButton.setOnClickListener(l -> {
            mGame.newGame();
            updateUI();
            mReplayButton.setVisibility(View.INVISIBLE);
            mWinnerText.setVisibility(View.INVISIBLE);
            setGameListeners();
        });

        mGame = new TicTacToe_vPlayer();
        mGame.newGame();

        //Fill in previous gamestate if applicable
        SharedPreferences pref = getSharedPreferences("resume", Context.MODE_PRIVATE);
        boolean isResumable = pref.getBoolean("resumable", false);
        if (isResumable){
            mGame.setState(pref.getString("gameState", ""));

            boolean isX = pref.getBoolean("playerIsX", true);
            if (isX){
                mPlayerTurn.setText(R.string.x_turn);
                mGame.CURRENT_PLAYER = TicTacToe_vPlayer.PLAYERS.X;
            } else {
                mPlayerTurn.setText(R.string.o_turn);
                mGame.CURRENT_PLAYER = TicTacToe_vPlayer.PLAYERS.O;
            }
            updateUI();
        }

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private void onGameButtonClick(View view){
        //Set row and col indices
        int buttonIndex = mGameBoard.indexOfChild(view);
        int row = buttonIndex / TicTacToe_vPlayer.GRID_SIZE;
        int col = buttonIndex % TicTacToe_vPlayer.GRID_SIZE;

        //If a valid move was made, change CURRENT_PLAYER and update UI
        if (mGame.selectSquare(row, col, mGame.CURRENT_PLAYER)){
            updateUI();
            mGame.setCURRENT_PLAYER();
            switch (mGame.CURRENT_PLAYER){
                case X:
                    mPlayerTurn.setText(R.string.x_turn);
                    break;
                case O:
                    mPlayerTurn.setText(R.string.o_turn);
                    break;
            }
        } else {        //Show invalid move toast
            Toast.makeText(this, R.string.invalid_move, Toast.LENGTH_SHORT).show();
        }

        //Display WINNER message if game is over
        int gameOverState = mGame.isGameOver();
        if (gameOverState > 0){

            mReplayButton.setVisibility(View.VISIBLE);

            switch (gameOverState){
                case 1:
                    mWinnerText.setText(R.string.x_wins);
                    break;
                case 2:
                    mWinnerText.setText(R.string.o_wins);
                    break;
                case 3:
                    mWinnerText.setText(R.string.cat_wins);
                    break;
                default:
                    break;
            }
            mWinnerText.setVisibility(View.VISIBLE);
            removeGameListeners();
            SharedPreferences pref = getSharedPreferences("resume", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("gameState", "");
            editor.putBoolean("resumable", false);
            editor.apply();
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

    private void updateUI(){
        for (int buttonIndex = 0; buttonIndex < mGameBoard.getChildCount(); buttonIndex++){
            Button gridButton = (Button) mGameBoard.getChildAt(buttonIndex);

            int row = buttonIndex / TicTacToe_vPlayer.GRID_SIZE;
            int col = buttonIndex % TicTacToe_vPlayer.GRID_SIZE;

            gridButton.setText(String.valueOf(mGame.getSquareContents(row, col)));
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
            editor.putBoolean("playerIsX", mGame.CURRENT_PLAYER == TicTacToe_Base.PLAYERS.X);
            editor.putBoolean("gameIsVsPlayer", true);
            editor.apply();
        }
    }
}