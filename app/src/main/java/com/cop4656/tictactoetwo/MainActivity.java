package com.cop4656.tictactoetwo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements DifficultyDialogFragment.DifficultySelectListener{

    //Declare Buttons
    Button mResumeGameButton;
    Button mVsPlayerButton;
    Button mVsComputerButton;
    Button mRulesButton;
    Toolbar mainToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Assign References to Buttons
        mResumeGameButton = (Button) findViewById(R.id.resume_game_button);
        mVsPlayerButton = (Button) findViewById(R.id.vs_player_button);
        mVsComputerButton = (Button) findViewById(R.id.vs_computer_button);
        mRulesButton = (Button) findViewById(R.id.rules_button);


        //Set Click Listeners for each button
        //ResumeGame
        SharedPreferences pref = getSharedPreferences("resume", Context.MODE_PRIVATE);
        boolean isResumable = pref.getBoolean("resumable", false);
        if (isResumable){
            mResumeGameButton.setEnabled(true);
            mResumeGameButton.setOnClickListener(v -> {
                Intent intent;
                boolean vPlayer = pref.getBoolean("gameIsVsPlayer", true);
                if (vPlayer){
                    intent = new Intent(MainActivity.this, GameVPlayerActivity.class);
                } else {
                    intent = new Intent(MainActivity.this, GameVComputerActivity.class);
                    intent.putExtra("DIFFICULTY", pref.getString("difficulty", "Easy"));
                }
                startActivity(intent);
            });
        } else {
            mResumeGameButton.setEnabled(false);
        }
        //VsPlayer
        mVsPlayerButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("resumable", false);
            editor.apply();

            Intent intent = new Intent(MainActivity.this, GameVPlayerActivity.class);
            startActivity(intent);
        });
        //VsComputer
        mVsComputerButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("resumable", false);
            editor.apply();
            showDifficultyDialog();
        });
        //Rules
        mRulesButton.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RulesFragment())
                    .addToBackStack(null)
                    .commit();
        });

        //Set up Toolbar
        mainToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);

    }

    public void showMainMenu() {
        getSupportFragmentManager().popBackStack();
        mainToolbar.setTitle(R.string.app_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public boolean onSupportNavigateUp(){
        showMainMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.vs_computer_button){
            showDifficultyDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDifficultyDialog(){
        DifficultyDialogFragment frag = new DifficultyDialogFragment();
        frag.setDifficultySelectListener(this);
        frag.show(getSupportFragmentManager(), "difficulty");
    }

    @Override
    public void onDifficultySelected(String diff){
        Intent intent = new Intent(this, GameVComputerActivity.class);
        intent.putExtra("DIFFICULTY", diff);
        startActivity(intent);
    }

    @Override
    public void onResume(){
        super.onResume();
        //Reset ResumeGame listener
        SharedPreferences pref = getSharedPreferences("resume", Context.MODE_PRIVATE);
        boolean isResumable = pref.getBoolean("resumable", false);
        if (isResumable){
            mResumeGameButton.setEnabled(true);
            mResumeGameButton.setOnClickListener(v -> {
                Intent intent;
                boolean vPlayer = pref.getBoolean("gameIsVsPlayer", true);
                if (vPlayer){
                    intent = new Intent(MainActivity.this, GameVPlayerActivity.class);
                } else {
                    intent = new Intent(MainActivity.this, GameVComputerActivity.class);
                    intent.putExtra("DIFFICULTY", pref.getString("difficulty", "Easy"));
                }
                startActivity(intent);
            });
        } else {
            mResumeGameButton.setEnabled(false);
        }
    }
}