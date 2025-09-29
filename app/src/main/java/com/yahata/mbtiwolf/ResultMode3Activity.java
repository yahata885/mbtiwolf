package com.yahata.mbtiwolf;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResultMode3Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_mode3);

        // Find views
        TextView wolfVoteResultTextView = findViewById(R.id.wolfVoteResultTextView);
        TextView winnerTextView = findViewById(R.id.winnerTextView);
        LinearLayout roleGuessResultsLayout = findViewById(R.id.roleGuessResultsLayout);
        Button playAgainButton = findViewById(R.id.playAgainButton);

        // Get data from Intent
        ArrayList<String> playerList = (ArrayList<String>) getIntent().getSerializableExtra("PLAYER_LIST");
        HashMap<String, GameRole> assignments = (HashMap<String, GameRole>) getIntent().getSerializableExtra("ASSIGNMENTS");
        HashMap<String, HashMap<String, String>> allAnswers = (HashMap<String, HashMap<String, String>>) getIntent().getSerializableExtra("ALL_ANSWERS");

        // Part 1: Determine werewolf result
        determineWolfResult(wolfVoteResultTextView, winnerTextView, assignments, allAnswers);

        // Part 2: Display role guess results
        displayRoleGuessResults(roleGuessResultsLayout, playerList, assignments, allAnswers);

        // "Play Again" button
        playAgainButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void determineWolfResult(TextView voteResultView, TextView winnerView, HashMap<String, GameRole> assignments, HashMap<String, HashMap<String, String>> allAnswers) {
        HashMap<String, Integer> wolfVoteCounts = new HashMap<>();

        // Count votes for "Werewolf"
        for (HashMap<String, String> guesses : allAnswers.values()) {
            for (Map.Entry<String, String> entry : guesses.entrySet()) {
                if (entry.getValue().equals("人狼")) {
                    String suspectedPlayer = entry.getKey();
                    int count = wolfVoteCounts.getOrDefault(suspectedPlayer, 0);
                    wolfVoteCounts.put(suspectedPlayer, count + 1);
                }
            }
        }

        // Find the player with the most votes for "Werewolf"
        String mostSuspectedPlayer = "";
        int maxVotes = 0;
        for (Map.Entry<String, Integer> entry : wolfVoteCounts.entrySet()) {
            if (entry.getValue() > maxVotes) {
                maxVotes = entry.getValue();
                mostSuspectedPlayer = entry.getKey();
            }
        }

        if (mostSuspectedPlayer.isEmpty()) {
            voteResultView.setText("誰も人狼だと疑われませんでした。");
            winnerView.setText("人狼チームの勝利！");
            return;
        }

        GameRole suspectedRole = assignments.get(mostSuspectedPlayer);
        boolean wasWolf = suspectedRole != null && suspectedRole.getName().equals("人狼");

        voteResultView.setText("最も疑われたのは " + mostSuspectedPlayer + " さんでした。\nその正体は... " + (wasWolf ? "人狼" : "市民") + "！");

        if (wasWolf) {
            winnerView.setText("市民チームの勝利！");
        } else {
            winnerView.setText("人狼チームの勝利！");
        }
    }

    private void displayRoleGuessResults(LinearLayout layout, ArrayList<String> playerList, HashMap<String, GameRole> assignments, HashMap<String, HashMap<String, String>> allAnswers) {
        for (String guesser : playerList) {
            TextView guesserTitle = new TextView(this);
            guesserTitle.setText("▼ " + guesser + "さんの回答結果");
            guesserTitle.setTextSize(20);
            guesserTitle.setPadding(0, 24, 0, 8);
            layout.addView(guesserTitle);

            int score = 0;
            HashMap<String, String> guesses = allAnswers.get(guesser);

            for (Map.Entry<String, String> entry : guesses.entrySet()) {
                String target = entry.getKey();
                String guess = entry.getValue();
                String correctAnswer = assignments.get(target).getName();

                // Don't score the guess for the werewolf
                if (correctAnswer.equals("人狼")) continue;

                TextView resultLine = new TextView(this);
                String resultText;
                if (guess.equals(correctAnswer)) {
                    score++;
                    resultText = "✅ " + target + "さん: 「" + guess + "」で正解！";
                } else {
                    resultText = "❌ " + target + "さん: 「" + guess + "」 (正解: " + correctAnswer + ")";
                }
                resultLine.setText(resultText);
                resultLine.setTextSize(16);
                layout.addView(resultLine);
            }
        }
    }
}
