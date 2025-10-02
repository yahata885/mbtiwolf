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

public class ResultMode2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_mode2);

        TextView topPlayerTextView = findViewById(R.id.topPlayerTextView);
        LinearLayout resultsLayout = findViewById(R.id.resultsLayout);
        Button playAgainButton = findViewById(R.id.playAgainButton);

        ArrayList<String> playerList = (ArrayList<String>) getIntent().getSerializableExtra("PLAYER_LIST");
        HashMap<String, GameRole> assignments = (HashMap<String, GameRole>) getIntent().getSerializableExtra("ASSIGNMENTS");
        HashMap<String, HashMap<String, String>> allAnswers = (HashMap<String, HashMap<String, String>>) getIntent().getSerializableExtra("ALL_ANSWERS");

        HashMap<String, Integer> playerScores = new HashMap<>();

        for (String guesser : playerList) {
            TextView guesserTitle = new TextView(this);
            guesserTitle.setText("▼ " + guesser + "さんの回答結果");
            guesserTitle.setTextSize(20);
            guesserTitle.setPadding(0, 24, 0, 8);
            resultsLayout.addView(guesserTitle);

            int score = 0;
            HashMap<String, String> guesses = allAnswers.get(guesser);

            for (Map.Entry<String, String> entry : guesses.entrySet()) {
                String target = entry.getKey();
                String guess = entry.getValue();
                String correctAnswer = assignments.get(target).getName();

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
                resultsLayout.addView(resultLine);
            }

            playerScores.put(guesser, score);

            TextView scoreText = new TextView(this);
            scoreText.setText("正解数: " + score + "/" + guesses.size());
            scoreText.setTextSize(18);
            scoreText.setPadding(0, 8, 0, 0);
            resultsLayout.addView(scoreText);
        }

        int maxScore = playerScores.values().stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);

        List<String> topPlayers = playerScores.entrySet().stream()
                .filter(entry -> entry.getValue() == maxScore)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (maxScore == 0) {
            topPlayerTextView.setText("正解者はいませんでした！");
        } else if (topPlayers.size() == playerList.size()) {
            topPlayerTextView.setText("全員同点です！");
        } else if (topPlayers.size() > 1) {
            String topPlayersText = String.join("さん、", topPlayers) + "さん";
            topPlayerTextView.setText("最も成績が良かったのは " + topPlayersText + " です！");
        } else {
            topPlayerTextView.setText("最も成績が良かったのは " + topPlayers.get(0) + " さんです！");
        }

        playAgainButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}