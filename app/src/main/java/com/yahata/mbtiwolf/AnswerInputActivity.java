package com.yahata.mbtiwolf;

// (import文は変更なし)
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


public class AnswerInputActivity extends AppCompatActivity {
    // (クラスの中身は変更なし、完全版を掲載します)
    private TextView guesserNameTextView;
    private LinearLayout answerFieldsLayout;
    private Button confirmAnswersButton;

    private ArrayList<String> playerList;
    private HashMap<String, GameRole> assignments;
    private int mode;
    private String theme;

    private int currentPlayerIndex = 0;
    private HashMap<String, HashMap<String, String>> allAnswers = new HashMap<>();
    private List<Spinner> currentSpinners = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_input);

        guesserNameTextView = findViewById(R.id.guesserNameTextView);
        answerFieldsLayout = findViewById(R.id.answerFieldsLayout);
        confirmAnswersButton = findViewById(R.id.confirmAnswersButton);

        playerList = getIntent().getStringArrayListExtra("PLAYER_LIST");
        assignments = (HashMap<String, GameRole>) getIntent().getSerializableExtra("ASSIGNMENTS");
        mode = getIntent().getIntExtra("GAME_MODE", 2);
        theme = getIntent().getStringExtra("GAME_THEME");

        setupTurn();

        confirmAnswersButton.setOnClickListener(v -> {
            saveCurrentAnswers();
            currentPlayerIndex++;
            if (currentPlayerIndex < playerList.size()) {
                setupTurn();
            } else {
                goToResultScreen();
            }
        });
    }

    private void setupTurn() {
        String currentGuesser = playerList.get(currentPlayerIndex);
        guesserNameTextView.setText(currentGuesser + "さんの回答");

        answerFieldsLayout.removeAllViews();
        currentSpinners.clear();

        List<String> roleNames = getRoleOptions();

        for (String targetPlayer : playerList) {
            if (targetPlayer.equals(currentGuesser)) continue;

            TextView targetPlayerTextView = new TextView(this);
            targetPlayerTextView.setText(targetPlayer);
            targetPlayerTextView.setTextSize(18);

            Spinner roleSpinner = new Spinner(this);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roleNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            roleSpinner.setAdapter(adapter);

            answerFieldsLayout.addView(targetPlayerTextView);
            answerFieldsLayout.addView(roleSpinner);
            currentSpinners.add(roleSpinner);
        }

        if (currentPlayerIndex == playerList.size() - 1) {
            confirmAnswersButton.setText("全員の回答を確定し結果を見る");
        }
    }

    private List<String> getRoleOptions() {
        List<GameRole> roles = "MBTI".equals(theme) ? DataSource.getMbtiRoles() : DataSource.getLoveTypeRoles();
        List<String> roleNames = roles.stream().map(GameRole::getName).collect(Collectors.toList());
        if (mode == 3) {
            roleNames.add("人狼");
        }
        return roleNames;
    }

    private void saveCurrentAnswers() {
        String currentGuesser = playerList.get(currentPlayerIndex);
        HashMap<String, String> currentGuesses = new HashMap<>();

        int spinnerIndex = 0;
        for (String targetPlayer : playerList) {
            if (targetPlayer.equals(currentGuesser)) continue;

            Spinner spinner = currentSpinners.get(spinnerIndex++);
            String guess = spinner.getSelectedItem().toString();
            currentGuesses.put(targetPlayer, guess);
        }
        allAnswers.put(currentGuesser, currentGuesses);
    }

    private void goToResultScreen() {
        Toast.makeText(this, "全員の回答が完了しました", Toast.LENGTH_SHORT).show();

        Intent intent;
        if (mode == 2) {
            intent = new Intent(this, ResultMode2Activity.class);
        } else { // mode == 3
            intent = new Intent(this, ResultMode3Activity.class);
        }

        // ★★★ 受け取った情報を全て結果画面に渡す ★★★
        intent.putExtra("PLAYER_LIST", playerList);
        intent.putExtra("ASSIGNMENTS", assignments);
        intent.putExtra("ALL_ANSWERS", allAnswers);
        startActivity(intent);
        finish();
    }
}