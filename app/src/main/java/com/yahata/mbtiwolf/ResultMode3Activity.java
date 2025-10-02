package com.yahata.mbtiwolf;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        // Find the player(s) with the most votes
        int maxVotes = wolfVoteCounts.values().stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);

        // 投票が全くない場合は人狼の勝利
        if (maxVotes == 0) {
            voteResultView.setText("誰も人狼だと疑われませんでした。");
            winnerView.setText("人狼チームの勝利！");
            return;
        }

        // 最多票を獲得したプレイヤーをすべて取得
        List<String> mostSuspectedPlayers = wolfVoteCounts.entrySet().stream()
                .filter(entry -> entry.getValue() == maxVotes)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // 勝敗判定ロジック
        boolean wasWolfFound = false;
        for (String player : mostSuspectedPlayers) {
            GameRole role = assignments.get(player);
            if (role != null && role.getName().equals("人狼")) {
                wasWolfFound = true;
                break;
            }
        }

        // 結果表示
        if (mostSuspectedPlayers.size() > 1) {
            String playersText = String.join("さん、", mostSuspectedPlayers) + "さん";
            voteResultView.setText("最も疑われたのは " + playersText + " でした。\nその中に人狼は" + (wasWolfFound ? "いました" : "いませんでした") + "！");
        } else {
            String mostSuspectedPlayer = mostSuspectedPlayers.get(0);
            voteResultView.setText("最も疑われたのは " + mostSuspectedPlayer + " さんでした。\nその正体は... " + (wasWolfFound ? "人狼" : "市民") + "！");
        }

        if (wasWolfFound) {
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