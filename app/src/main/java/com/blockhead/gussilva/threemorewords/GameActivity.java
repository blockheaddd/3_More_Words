/*
 * Written by Gus Silva (Block-Head)
 * Some code is from Sue Smith, following her tutorial
 *
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blockhead.gussilva.threemorewords;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;


public class GameActivity extends ActionBarActivity {

    private String[] words;
    private String mCategory, mDifficulty;
    private Random rand;
    private String currWord1, currWord2, currWord3;
    private LinearLayout wordLayout1, wordLayout2, wordLayout3;
    private TextView[] answerViews1, answerViews2, answerViews3;
    private TextView chancesLeftView;
    private GridView letters;
    private LetterAdapter ltrAdapt;
    private AlertDialog helpAlert;
    private ActionBar mActionBar;

    //number of chances
    private int numChances =6;
    //current wrong - will increment when wrong answers are chosen
    private int currWrong;
    //number of characters in current word
    private int numChars;
    //number correctly guessed
    private int numCorr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Resources res = getResources();

        //Get array of words to draw from
        words = res.getStringArray(R.array.technologyeasy);

        rand = new Random();

        currWord1 = "";
        currWord2 = "";
        currWord3 = "";

        chancesLeftView = (TextView)findViewById(R.id.chancesText);
        wordLayout1 = (LinearLayout)findViewById(R.id.word1);
        wordLayout2 = (LinearLayout)findViewById(R.id.word2);
        wordLayout3 = (LinearLayout)findViewById(R.id.word3);

        letters = (GridView)findViewById(R.id.letters);


        mActionBar = getActionBar();
        if(mActionBar != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);

        playGame();
    }

    private void playGame() {

        //play a new game
        currWord1 = getNewWord();
        currWord2 = getNewWord();
        currWord3 = getNewWord();

        answerViews1 = new TextView[currWord1.length()];
        answerViews2 = new TextView[currWord2.length()];
        answerViews3 = new TextView[currWord3.length()];

        wordLayout1.removeAllViews();
        wordLayout2.removeAllViews();
        wordLayout3.removeAllViews();

        createAnswerView(currWord1, answerViews1, wordLayout1);
        createAnswerView(currWord2, answerViews2, wordLayout2);
        createAnswerView(currWord3, answerViews3, wordLayout3);


        ltrAdapt=new LetterAdapter(this);
        letters.setAdapter(ltrAdapt);

        currWrong =0;
        numChars= currWord1.length() + currWord2.length() + currWord3.length();
        numCorr=0;

    }

    public void createAnswerView(String currWord, TextView[] currAnswerView, LinearLayout currWordLayout)
    {
        //Set TextViews text to letter of the answer for Word 1
        for (int c = 0; c < currWord.length(); c++) {
            currAnswerView[c] = new TextView(this);
            currAnswerView[c].setText(""+ currWord.charAt(c));

            //Set TextView Properties
            currAnswerView[c].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            currAnswerView[c].setGravity(Gravity.CENTER);
            currAnswerView[c].setTextColor(Color.TRANSPARENT); //So that user does not see that answer
            currAnswerView[c].setBackgroundResource(R.drawable.letter_bg);
            //add to layout
            currWordLayout.addView(currAnswerView[c]);
        }
        chancesLeftView.setTextColor(getResources().getColor(R.color.chances_good));
        chancesLeftView.setText(numChances + " chances left");

    }

    public void letterPressed(View view) {

        String ltr=((TextView)view).getText().toString();
        char letterChar = ltr.charAt(0);
        view.setEnabled(false);
        view.setBackgroundResource(R.drawable.letter_down);

        //Check if it is a correct guess
        boolean correct = false;

        //Check Word 1
        for(int k = 0; k < currWord1.length(); k++) {
            if(currWord1.charAt(k)==letterChar){
                correct = true;
                numCorr++;
                answerViews1[k].setTextColor(Color.BLACK);
            }
        }
        //Check Word 2
        for(int k = 0; k < currWord2.length(); k++) {
            if(currWord2.charAt(k)==letterChar){
                correct = true;
                numCorr++;
                answerViews2[k].setTextColor(Color.BLACK);
            }
        }
        //Check Word 3
        for(int k = 0; k < currWord3.length(); k++) {
            if(currWord3.charAt(k)==letterChar){
                correct = true;
                numCorr++;
                answerViews3[k].setTextColor(Color.BLACK);
            }
        }


        //Check if user has won, lost, or can continue playing
        if (correct) {
            //correct guess
            if (numCorr == numChars) {
                // Disable Buttons
                disableBtns();

                // Display Alert Dialog
                AlertDialog.Builder winBuild = new AlertDialog.Builder(this);
                winBuild.setTitle("Success");
                winBuild.setMessage("You win!\n\nThe answers were:\n"+ currWord1
                        + "\n" + currWord2 + "\n" + currWord3);
                winBuild.setPositiveButton("Play Again",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                GameActivity.this.playGame();
                            }});

                winBuild.setNegativeButton("Exit",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                GameActivity.this.finish();
                            }});

                winBuild.show();
            }
        } else if (currWrong < numChances) { //User still has chances
            currWrong++;
            view.setBackgroundResource(R.drawable.letter_wrong);
            if((numChances - currWrong) > 1)
                chancesLeftView.setText((numChances - currWrong) + " chances left");
            else if((numChances - currWrong) == 1)
                chancesLeftView.setText("1 chance left");
            else
                chancesLeftView.setText("Last Chance!");

            if(currWrong == 3){
                chancesLeftView.setTextColor(getResources().getColor(R.color.chances_medium));
            }
            else if(currWrong == 5){
                chancesLeftView.setTextColor(getResources().getColor(R.color.chances_bad));
            }
        } else{ //User has lost
            disableBtns();

            // Display Alert Dialog
            AlertDialog.Builder loseBuild = new AlertDialog.Builder(this);
            loseBuild.setTitle("OOPS");
            loseBuild.setMessage("You lose!\n\nThe answers were:\n"
                    + currWord1 + "\n" + currWord2 + "\n" + currWord3);
            loseBuild.setPositiveButton("Play Again",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            GameActivity.this.playGame();
                        }});

            loseBuild.setNegativeButton("Exit",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            GameActivity.this.finish();
                        }});

            loseBuild.show();
        }

    }

    public void disableBtns() {
        int numLetters = letters.getChildCount();
        for (int l = 0; l < numLetters; l++) {
            letters.getChildAt(l).setEnabled(false);
        }
    }

    public void showHelp() {
        AlertDialog.Builder helpBuild = new AlertDialog.Builder(this);

        helpBuild.setTitle("How To Play");
        helpBuild.setMessage("Try to guess all three words by selecting the letters" +
                "carefully before you run out of chances.\nGood Luck!");
        helpBuild.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        helpAlert.dismiss();
                    }});
        helpAlert = helpBuild.create();

        helpBuild.show();
    }

    private String getNewWord()
    {
        String newWord = words[rand.nextInt(words.length)];
        while(newWord.equals(currWord1)) newWord = words[rand.nextInt(words.length)];
        return newWord;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_help:
                showHelp();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
