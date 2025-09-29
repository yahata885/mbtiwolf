package com.yahata.mbtiwolf;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class VoteActivity extends AppCompatActivity {

    private TextView voterNameTextView;
    private LinearLayout voteButtonsLayout;
    private ArrayList<String> playerList;
    private int currentPlayerIndex = 0;
    private HashMap<String, Integer> voteCounts = new HashMap<>();
    private HashMap<String, GameRole> assignments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        voterNameTextView = findViewById(R.id.voterNameTextView);
        voteButtonsLayout = findViewById(R.id.voteButtonsLayout);

        playerList = getIntent().getStringArrayListExtra("PLAYER_LIST");
        assignments = (HashMap<String, GameRole>) getIntent().getSerializableExtra("ASSIGNMENTS");

        setupVoteTurn();
    }

    private void setupVoteTurn() {
        String currentVoter = playerList.get(currentPlayerIndex);
        voterNameTextView.setText(currentVoter + "さんの投票");
        voteButtonsLayout.removeAllViews();

        for (String targetPlayer : playerList) {
            if (targetPlayer.equals(currentVoter)) {
                continue;
            }
            Button playerButton = new Button(this);
            playerButton.setText(targetPlayer);
            playerButton.setTextSize(18);
            playerButton.setOnClickListener(v -> {
                int currentVotes = voteCounts.getOrDefault(targetPlayer, 0);
                voteCounts.put(targetPlayer, currentVotes + 1);
                goToNextVoter();
            });
            voteButtonsLayout.addView(playerButton);
        }
    }

    private void goToNextVoter() {
        currentPlayerIndex++;
        if (currentPlayerIndex < playerList.size()) {
            setupVoteTurn();
        } else {
            // 全員の投票が完了した場合
            Toast.makeText(this, "全員の投票が完了しました", Toast.LENGTH_SHORT).show();

            // ★★★★★ 行き先は ResultActivity.class です ★★★★★
            Intent intent = new Intent(VoteActivity.this, ResultActivity.class);
            intent.putExtra("VOTE_COUNTS", voteCounts);
            intent.putExtra("ASSIGNMENTS", assignments);
            intent.putExtra("GAME_MODE", getIntent().getIntExtra("GAME_MODE", 2));
            startActivity(intent);

            // この画面を閉じる
            finish();
        }
    }
}