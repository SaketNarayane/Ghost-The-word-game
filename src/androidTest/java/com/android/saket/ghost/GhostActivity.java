package com.android.saket.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Her turn";
    private static final String USER_TURN     = "Your turn";

    private static final String GHOST_TEXT  = "ghosttext";
    private static final String GHOST_LABEL = "ghostlabel";

    private static String start     = "";
    private static boolean switched = true;


    private static int playerScore   = 0;
    private static int computerScore = 0;


    private GhostDictionary dictionary;
    private GhostDictionary dictionary2;
    private boolean userTurn = false;
    private boolean slow     = false;
    private Random random    = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("words.txt");
        //    dictionary  = new SimpleDictionary(inputStream);
            dictionary2 = new FastDictionary(inputStream);


        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
            toast.show();
        }
        onStart(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        // used to select which type of searching will be used
        // using getAnyWordStartingWith
        if (id == R.id.slow) {
            slow          = true;
            playerScore   = 0;
            computerScore = 0;
            updateScores();
            onStart();
            return true;
        }

        // using getGoodWordStartingWith
        if (id == R.id.fast) {
            slow          = false;
            playerScore   = 0;
            computerScore = 0;
            updateScores();
            onStart();
            return true;
        }
        /**
         * future feature:
         * using Trie with the new extensions to methods
        if (id == R.id.hard) {
            slow          = false;
            playerScore   = 0;
            computerScore = 0;
            updateScores();
            onStart();
            return true;
        }
         */

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event){

        TextView text   = (TextView) findViewById(R.id.ghostText);
        TextView label  = (TextView) findViewById(R.id.gameStatus);
        String fragment = text.getText().toString();

        if(keyCode < 29 || keyCode > 54){
            return super.onKeyUp(keyCode, event);
        } else {
            start = "" + (char)(keyCode + 68);
        }

        Switch toggle = (Switch) findViewById(R.id.toggleButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switched = true;
                } else {
                    switched = false;
                }
            }
        });

        if(switched){
            fragment += start;
        } else {
            fragment = start + fragment;
        }

        text.setText(fragment.toLowerCase());

        if(dictionary2.isWord(fragment.toLowerCase())){
            label.setText("You lost!");
            computerScore++;
            updateScores();
            return true;
        }

        userTurn = false;
        label.setText("Her Turn");
        computerTurn();

        return true;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        TextView text  = (TextView) findViewById(R.id.ghostText);
        TextView label = (TextView) findViewById(R.id.gameStatus);

        String ghostText  = text.getText().toString();
        String ghostLabel = label.getText().toString();

        /** Save the user's current game state
        savedInstanceState.putInt(ghostText, mCurrentScore);
        savedInstanceState.putInt(ghostLabel, mCurrentLevel);*/

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }


    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        userTurn = random.nextBoolean();
        TextView text = (TextView) findViewById(R.id.ghostText);
        text.setText("");
        TextView label = (TextView) findViewById(R.id.gameStatus);
        if (userTurn) {
            label.setText(USER_TURN);
        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    private void computerTurn() {

        TextView text   = (TextView) findViewById(R.id.ghostText);
        TextView label  = (TextView) findViewById(R.id.gameStatus);
        String fragment = text.getText().toString().toLowerCase();
        String possibleWord;

        // Do computer turn stuff then make it the user's turn again

        if(slow) {
            possibleWord = dictionary2.getAnyWordStartingWith(fragment);
        } else {
            possibleWord = dictionary2.getGoodWordStartingWith(fragment);
        }

        if(possibleWord == null) {
            computerScore++;
            label.setText("She Challenged, You lost!");
            updateScores();
            return;
        }

        Log.d("computerTurn", "inside method call");

        text.setText(fragment + possibleWord.substring(0,1));

        // for SimpleDictionary
        /**
        int possibleWordSize  = fragment.length();
        String computerOutput = possibleWord.substring(possibleWordSize);

        if(fragment.length() < possibleWordSize ){
            text.setText(fragment + computerOutput.substring(0));
            playerScore++;
            label.setText("You Win!");
            updateScores();
        } else {
            text.setText(fragment + computerOutput.substring(0, 1));
        }
         */

        userTurn = true;
        label.setText(USER_TURN);
    }

    public void challenge(View view){
        TextView text  = (TextView) findViewById(R.id.ghostText);
        TextView label = (TextView) findViewById(R.id.gameStatus);

        if(dictionary2.isWord(text.getText().toString().toLowerCase())){
            playerScore++;
            label.setText("It is a word, You Win!");
        } else {
            String temp=dictionary2.getAnyWordStartingWith(text.getText().toString().toLowerCase());
            if(temp !=null){
                label.setText("It leads to "+text.getText().toString().toLowerCase()+temp+" , You Lost!");
                computerScore++;
            }
            else {
                label.setText("It doesn't lead to a word, You Win!");
                playerScore++;
            }
        }

        updateScores();
    }

    public void updateScores(){
        TextView player_score   = (TextView) findViewById(R.id.player_score);
        TextView computer_score = (TextView) findViewById(R.id.computer_score);

        String player   = "You : " + playerScore;
        String computer = "She : " + computerScore;

        player_score.setText(player);
        computer_score.setText(computer);

    }

}
