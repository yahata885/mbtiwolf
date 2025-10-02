package com.yahata.mbtiwolf;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Mode23InputActivity extends AppCompatActivity {

    private TextView playerNameTextView, roleNameTextView, roleDescriptionTextView;
    private Button revealButton, nextPlayerButton;
    private Map<String, GameRole> assignments;
    private ArrayList<String> playerList;
    private int currentPlayerIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_reveal);

        playerNameTextView = findViewById(R.id.playerNameTextView);
        roleNameTextView = findViewById(R.id.roleNameTextView);
        roleDescriptionTextView = findViewById(R.id.roleDescriptionTextView);
        revealButton = findViewById(R.id.revealButton);
        nextPlayerButton = findViewById(R.id.nextPlayerButton);

        playerList = getIntent().getStringArrayListExtra("PLAYER_LIST");
        String theme = getIntent().getStringExtra("GAME_THEME");
        int mode = getIntent().getIntExtra("GAME_MODE", 2);
        //モード3なら人狼を1人割り当てる、他モードは0人
        int wolfCount = (mode == 3) ? 1 : 0;
        assignments = GameLogic.assignRoles(playerList, theme, wolfCount, mode);

        updateTurnView();

        revealButton.setOnClickListener(v -> showRole());

        nextPlayerButton.setOnClickListener(v -> {
            currentPlayerIndex++;
            if (currentPlayerIndex < playerList.size()) {
                updateTurnView();
            } else {
                Toast.makeText(Mode23InputActivity.this, "全員の役割確認が完了しました", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(Mode23InputActivity.this, DiscussionActivity.class);
                intent.putStringArrayListExtra("PLAYER_LIST", playerList);
                intent.putExtra("GAME_THEME", theme);
                intent.putExtra("GAME_MODE", mode);
                // ★★★ 「正解データ」を次の画面に渡す ★★★
                intent.putExtra("ASSIGNMENTS", (HashMap<String, GameRole>) assignments);

                startActivity(intent);
                finish();
            }
        });
    }

    private void updateTurnView() {
        revealButton.setVisibility(View.VISIBLE);
        nextPlayerButton.setVisibility(View.GONE);
        roleNameTextView.setVisibility(View.INVISIBLE);
        roleDescriptionTextView.setVisibility(View.INVISIBLE);
        playerNameTextView.setText(playerList.get(currentPlayerIndex) + "さんの番です");

        if (currentPlayerIndex == playerList.size() - 1) {
            nextPlayerButton.setText("確認完了（議論を開始）");
        } else {
            nextPlayerButton.setText("確認しました（次の人へ）");
        }
    }

    private void showRole() {
        String currentPlayerName = playerList.get(currentPlayerIndex);
        GameRole assignedRole = assignments.get(currentPlayerName);

        if (assignedRole != null) {
            roleNameTextView.setText("あなたの役割は【" + assignedRole.getName() + "】です");
            roleDescriptionTextView.setText(assignedRole.getDescription());
            roleNameTextView.setVisibility(View.VISIBLE);
            roleDescriptionTextView.setVisibility(View.VISIBLE);
            revealButton.setVisibility(View.GONE);
            nextPlayerButton.setVisibility(View.VISIBLE);
        }
    }
}