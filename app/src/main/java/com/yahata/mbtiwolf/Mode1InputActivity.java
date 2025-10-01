package com.yahata.mbtiwolf;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Mode1InputActivity extends AppCompatActivity {

    private TextView playerTurnTextView;
    private Spinner typeSpinner;
    private Button confirmTypeButton;

    private ArrayList<String> playerList;
    private String theme;
    private int currentPlayerIndex = 0;

    private HashMap<String, String> playerSelections = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode1_input);

        playerTurnTextView = findViewById(R.id.playerTurnTextView);
        typeSpinner = findViewById(R.id.typeSpinner);
        confirmTypeButton = findViewById(R.id.confirmTypeButton);

        playerList = getIntent().getStringArrayListExtra("PLAYER_LIST");
        theme = getIntent().getStringExtra("GAME_THEME");

        setupSpinner(theme);
        updateTurnView();

        confirmTypeButton.setOnClickListener(v -> {

            String selectedType = typeSpinner.getSelectedItem().toString();
            String currentPlayer = playerList.get(currentPlayerIndex);
            playerSelections.put(currentPlayer, selectedType);

            currentPlayerIndex++;

            if (currentPlayerIndex < playerList.size()) {
                updateTurnView();
            } else {

                goToDiscussionScreen();
            }
        });
    }

    private void goToDiscussionScreen() {
        Toast.makeText(this, "全員の入力が完了しました！", Toast.LENGTH_SHORT).show();


        HashMap<String, GameRole> assignments = createAssignmentsFromSelections();

        Intent intent = new Intent(Mode1InputActivity.this, DiscussionActivity.class);
        intent.putStringArrayListExtra("PLAYER_LIST", playerList);
        intent.putExtra("GAME_THEME", theme);
        intent.putExtra("GAME_MODE", 1);
        intent.putExtra("ASSIGNMENTS", assignments);

        startActivity(intent);
        finish();
    }

    // ★★★ プレイヤーの選択(String)から、役職データ(GameRole)のマップを作成するメソッド ★★★
    private HashMap<String, GameRole> createAssignmentsFromSelections() {
        HashMap<String, GameRole> assignmentsMap = new HashMap<>();
        List<GameRole> allRoles = "MBTI".equals(theme) ? DataSource.getMbtiRoles() : DataSource.getLoveTypeRoles();

        for (Map.Entry<String, String> entry : playerSelections.entrySet()) {
            String playerName = entry.getKey();
            String roleName = entry.getValue();

            // 名前に一致するGameRoleオブジェクトを探す
            for (GameRole role : allRoles) {
                if (role.getName().equals(roleName)) {
                    assignmentsMap.put(playerName, role);
                    break;
                }
            }
        }
        return assignmentsMap;
    }

    private void setupSpinner(String theme) {
        List<GameRole> roles = "MBTI".equals(theme) ? DataSource.getMbtiRoles() : DataSource.getLoveTypeRoles();
        List<String> roleNames = roles.stream().map(GameRole::getName).collect(Collectors.toList());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roleNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);
    }

    private void updateTurnView() {
        playerTurnTextView.setText(playerList.get(currentPlayerIndex) + "さんの番です");
        if (currentPlayerIndex == playerList.size() - 1) {
            confirmTypeButton.setText("決定して議論を開始");
        }
    }
}