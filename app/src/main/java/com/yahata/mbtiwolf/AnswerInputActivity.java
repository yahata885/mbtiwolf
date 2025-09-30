package com.yahata.mbtiwolf;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class AnswerInputActivity extends AppCompatActivity {

    private TextView guesserNameTextView;
    private LinearLayout answerFieldsLayout;
    private Button confirmAnswersButton;

    private ArrayList<String> playerList;
    private HashMap<String, GameRole> assignments;
    private int mode;
    private String theme;

    // --- モード1で使う変数 ---
    private int currentPlayerIndex = 0;
    private List<Spinner> currentSpinnersForMode1 = new ArrayList<>();

    // --- モード2, 3で使う変数 ---
    // HashMap<回答者名, HashMap<対象者名, スピナー>>
    private HashMap<String, HashMap<String, Spinner>> allSpinnersForMode23 = new HashMap<>();

    // このActivityの成果物
    private HashMap<String, HashMap<String, String>> allAnswers = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_input);

        guesserNameTextView = findViewById(R.id.guesserNameTextView);
        answerFieldsLayout = findViewById(R.id.answerFieldsLayout);
        confirmAnswersButton = findViewById(R.id.confirmAnswersButton);

        // Intentからデータを取得
        playerList = getIntent().getStringArrayListExtra("PLAYER_LIST");
        assignments = (HashMap<String, GameRole>) getIntent().getSerializableExtra("ASSIGNMENTS");
        mode = getIntent().getIntExtra("GAME_MODE", 1); // デフォルトを1に変更
        theme = getIntent().getStringExtra("GAME_THEME");

        // ★★★ モードによってUIの生成方法とボタンの動作を分岐 ★★★
        if (mode == 1) {
            setupTurnForMode1(); // モード1：一人ずつ入力する画面を準備
            confirmAnswersButton.setOnClickListener(v -> handleConfirmForMode1());
        } else { // モード2, 3
            setupAllInOneScreenForMode23(); // モード2, 3：全員分を一度に入力する画面を準備
            confirmAnswersButton.setOnClickListener(v -> handleConfirmForMode23());
        }
    }

    // =================================================================
    // ★★★ モード1用のロジック (一人ずつ順番に入力) ★★★
    // =================================================================

    private void setupTurnForMode1() {
        String currentGuesser = playerList.get(currentPlayerIndex);
        guesserNameTextView.setText(currentGuesser + "さんの回答");

        answerFieldsLayout.removeAllViews();
        currentSpinnersForMode1.clear();
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
            currentSpinnersForMode1.add(roleSpinner);
        }

        if (currentPlayerIndex == playerList.size() - 1) {
            confirmAnswersButton.setText("全員の回答を確定し結果を見る");
        } else {
            confirmAnswersButton.setText("回答を確定して次の人へ");
        }
    }

    private void handleConfirmForMode1() {
        saveCurrentAnswersForMode1();
        currentPlayerIndex++;
        if (currentPlayerIndex < playerList.size()) {
            setupTurnForMode1();
        } else {
            goToResultScreen();
        }
    }

    private void saveCurrentAnswersForMode1() {
        String currentGuesser = playerList.get(currentPlayerIndex);
        HashMap<String, String> currentGuesses = new HashMap<>();
        int spinnerIndex = 0;
        for (String targetPlayer : playerList) {
            if (targetPlayer.equals(currentGuesser)) continue;
            Spinner spinner = currentSpinnersForMode1.get(spinnerIndex++);
            String guess = spinner.getSelectedItem().toString();
            currentGuesses.put(targetPlayer, guess);
        }
        allAnswers.put(currentGuesser, currentGuesses);
    }

    // =================================================================
    // ★★★ モード2, 3用のロジック (全員分を一度に入力) ★★★
    // =================================================================

    private void setupAllInOneScreenForMode23() {
        guesserNameTextView.setText("全員の役職を予想してください");
        confirmAnswersButton.setText("全員の回答を確定し結果を見る");
        List<String> roleNames = getRoleOptions();

        // 回答者ごとにループ
        for (String guesser : playerList) {
            // 「〇〇さんの予想」という見出しを追加
            TextView guesserHeader = new TextView(this);
            guesserHeader.setText(guesser + "さんの予想");
            guesserHeader.setTextSize(20);
            guesserHeader.setTypeface(null, android.graphics.Typeface.BOLD);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 24, 0, 8); // 上にマージン
            guesserHeader.setLayoutParams(params);
            answerFieldsLayout.addView(guesserHeader);

            HashMap<String, Spinner> targetSpinners = new HashMap<>();

            // 予想対象者ごとにループ
            for (String target : playerList) {
                if (guesser.equals(target)) continue; // 自分は予想しない

                // レイアウトを行にするためのLinearLayout
                LinearLayout rowLayout = new LinearLayout(this);
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);

                TextView targetName = new TextView(this);
                targetName.setText(target + "：");
                targetName.setTextSize(18);

                Spinner roleSpinner = new Spinner(this);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roleNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                roleSpinner.setAdapter(adapter);

                rowLayout.addView(targetName);
                rowLayout.addView(roleSpinner);
                answerFieldsLayout.addView(rowLayout);

                targetSpinners.put(target, roleSpinner);
            }
            allSpinnersForMode23.put(guesser, targetSpinners);
        }
    }

    private void handleConfirmForMode23() {
        saveAllAnswersFromScreen();
        goToResultScreen();
    }

    private void saveAllAnswersFromScreen() {
        // allSpinnersForMode23 から全ての回答を読み取って allAnswers を構築
        for (String guesser : allSpinnersForMode23.keySet()) {
            HashMap<String, String> currentGuesses = new HashMap<>();
            HashMap<String, Spinner> targetSpinners = allSpinnersForMode23.get(guesser);

            for (String target : targetSpinners.keySet()) {
                Spinner spinner = targetSpinners.get(target);
                String guess = spinner.getSelectedItem().toString();
                currentGuesses.put(target, guess);
            }
            allAnswers.put(guesser, currentGuesses);
        }
    }


    // =================================================================
    // ★★★ 共通のヘルパーメソッドと画面遷移 ★★★
    // =================================================================

    private List<String> getRoleOptions() {
        List<GameRole> roles = "MBTI".equals(theme) ? DataSource.getMbtiRoles() : DataSource.getLoveTypeRoles();
        List<String> roleNames = roles.stream().map(GameRole::getName).collect(Collectors.toList());
        if (mode == 3) {
            roleNames.add("人狼");
        }
        return roleNames;
    }

    private void goToResultScreen() {
        Toast.makeText(this, "全員の回答が完了しました", Toast.LENGTH_SHORT).show();

        Intent intent;
        if (mode == 1 ) { // モード1もモード2も同じ結果画面を使う場合
            intent = new Intent(this, ResultMode1Activity.class);
        }
        else if(mode == 2){
            intent = new Intent(this, ResultMode2Activity.class);
        }
        else { // mode == 3
            intent = new Intent(this, ResultMode3Activity.class);
        }

        intent.putExtra("PLAYER_LIST", playerList);
        intent.putExtra("ASSIGNMENTS", assignments);
        intent.putExtra("ALL_ANSWERS", allAnswers);
        startActivity(intent);
        finish();
    }
}