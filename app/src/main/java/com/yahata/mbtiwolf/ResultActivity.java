package com.yahata.mbtiwolf;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView winnerTextView = findViewById(R.id.winnerTextView);
        TextView votedPlayerTextView = findViewById(R.id.votedPlayerTextView);
        TextView voteCountsTextView = findViewById(R.id.voteCountsTextView);
        TextView rolesTextView = findViewById(R.id.rolesTextView);
        Button playAgainButton = findViewById(R.id.playAgainButton);

        // 前の画面からデータを受け取る
        HashMap<String, Integer> voteCounts = (HashMap<String, Integer>) getIntent().getSerializableExtra("VOTE_COUNTS");
        HashMap<String, GameRole> assignments = (HashMap<String, GameRole>) getIntent().getSerializableExtra("ASSIGNMENTS");
        int mode = getIntent().getIntExtra("GAME_MODE", 1);

        // 投票結果と役職一覧を表示
        displayVoteCounts(voteCountsTextView, voteCounts);

        // モード2と3では役職一覧を表示する
        if (mode == 2 || mode == 3) {
            rolesTextView.setVisibility(View.VISIBLE);
            displayRoles(rolesTextView, assignments);
        }

        // 結果を判定・表示
        determineWinner(winnerTextView, votedPlayerTextView, voteCounts, assignments, mode);

        // もう一度遊ぶボタンの処理
        playAgainButton.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void determineWinner(TextView winnerTextView, TextView votedPlayerTextView, HashMap<String, Integer> voteCounts, HashMap<String, GameRole> assignments, int mode) {
        // 最多票を持つプレイヤーを見つける
        String votedPlayer = "";
        if (voteCounts != null && !voteCounts.isEmpty()) {
            int maxVotes = Collections.max(voteCounts.values());
            for (Map.Entry<String, Integer> entry : voteCounts.entrySet()) {
                if (entry.getValue() == maxVotes) {
                    votedPlayer = entry.getKey();
                    break;
                }
            }
        }
        votedPlayerTextView.setText("追放されたのは... " + votedPlayer);

        // 勝敗判定
        if (mode == 3) { // 人狼モードの場合
            GameRole votedPlayerRole = assignments.get(votedPlayer);
            if (votedPlayerRole != null && votedPlayerRole.getName().equals("人狼")) {
                winnerTextView.setText("市民チームの勝利！");
            } else {
                winnerTextView.setText("人狼チームの勝利！");
            }
        } else { // モード1, 2の場合
            winnerTextView.setText("ゲーム終了！");
        }
    }

    private void displayVoteCounts(TextView textView, HashMap<String, Integer> voteCounts) {
        StringBuilder sb = new StringBuilder("【投票結果】\n");
        if (voteCounts != null) {
            for (Map.Entry<String, Integer> entry : voteCounts.entrySet()) {
                sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("票\n");
            }
        }
        textView.setText(sb.toString());
    }

    private void displayRoles(TextView textView, HashMap<String, GameRole> assignments) {
        StringBuilder sb = new StringBuilder("【役職一覧】\n");
        if (assignments != null) {
            for (Map.Entry<String, GameRole> entry : assignments.entrySet()) {
                sb.append(entry.getKey()).append(": ").append(entry.getValue().getName()).append("\n");
            }
        }
        textView.setText(sb.toString());
    }
}