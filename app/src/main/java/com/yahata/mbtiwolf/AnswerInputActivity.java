package com.yahata.mbtiwolf;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
public class AnswerInputActivity extends BaseActivity {

    // ▼▼▼【変更】タイトル用のTextViewを2つに変更 ▼▼▼
    private TextView playerNameTitleView;
    private TextView playerSubTitleView;
    private LinearLayout answerFieldsLayout;
    private Button confirmAnswersButton;

    private ExtendedFloatingActionButton helpButton;
    private ConstraintLayout roleExplanationOverlayLayout;
    private TextView closeRoleExplanationButton;
    private ImageView role1_image, role2_image, role3_image, role4_image;
    private TextView role1Title, role1Description;
    private TextView role2Title, role2Description;
    private TextView role3Title, role3Description;
    private TextView role4Title, role4Description;

    private ArrayList<String> playerList;
    private HashMap<String, GameRole> assignments;
    private int mode;
    private String theme;
    private int wolfCount;


    private int currentPlayerIndex = 0;
    private List<Spinner> currentSpinnersForMode1 = new ArrayList<>();
    private final List<Spinner> allSpinnersForMode3 = new ArrayList<>();
    private HashMap<String, HashMap<String, String>> allAnswers = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_input);

        // ▼▼▼【変更】UI要素の紐付けを更新 ▼▼▼
        playerNameTitleView = findViewById(R.id.playerNameTitleView);
        playerSubTitleView = findViewById(R.id.playerSubTitleView);
        answerFieldsLayout = findViewById(R.id.answerFieldsLayout);
        confirmAnswersButton = findViewById(R.id.confirmAnswersButton);

        helpButton = findViewById(R.id.helpButton);
        roleExplanationOverlayLayout = findViewById(R.id.roleExplanationOverlayLayout);
        closeRoleExplanationButton = findViewById(R.id.closeRoleExplanationButton);
        role1_image = findViewById(R.id.role1_image);
        role1Title = findViewById(R.id.role1_title);
        role1Description = findViewById(R.id.role1_description);
        role2_image = findViewById(R.id.role2_image);
        role2Title = findViewById(R.id.role2_title);
        role2Description = findViewById(R.id.role2_description);
        role3_image = findViewById(R.id.role3_image);
        role3Title = findViewById(R.id.role3_title);
        role3Description = findViewById(R.id.role3_description);
        role4_image = findViewById(R.id.role4_image);
        role4Title = findViewById(R.id.role4_title);
        role4Description = findViewById(R.id.role4_description);

        playerList = getIntent().getStringArrayListExtra("PLAYER_LIST");
        assignments = (HashMap<String, GameRole>) getIntent().getSerializableExtra("ASSIGNMENTS");
        mode = getIntent().getIntExtra("GAME_MODE", 1);
        theme = getIntent().getStringExtra("GAME_THEME");
        wolfCount = getIntent().getIntExtra("WOLF_COUNT", 1);

        if (mode == 1 || mode == 2) {
            setupTurnForMode1();
            confirmAnswersButton.setOnClickListener(v -> handleConfirmForMode1());
        } else if (mode == 3) {
            setupSingleVoteScreenForMode3();
            confirmAnswersButton.setOnClickListener(v -> handleConfirmForMode3());
        }

        helpButton.setOnClickListener(v -> {
            roleExplanationOverlayLayout.setVisibility(View.VISIBLE);
            displayRoleExplanations();
        });
        closeRoleExplanationButton.setOnClickListener(v -> {
            roleExplanationOverlayLayout.setVisibility(View.GONE);
        });
    }


    private void setupTurnForMode1() {
        String currentGuesser = playerList.get(currentPlayerIndex);
        // ▼▼▼【変更】2つのTextViewに分けてテキストを設定 ▼▼▼
        playerNameTitleView.setText(currentGuesser);
        playerSubTitleView.setText("さんの回答");
        // サブタイトルを表示状態に戻す（確認画面から戻ってきた場合のため）
        playerSubTitleView.setVisibility(View.VISIBLE);


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
            showConfirmationScreen();
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
    private void showConfirmationScreen() {
        // ▼▼▼【変更】確認画面のタイトル表示を更新 ▼▼▼
        playerNameTitleView.setText("全員の入力が完了しました");
        // サブタイトルは不要なので非表示にする
        playerSubTitleView.setVisibility(View.GONE);

        answerFieldsLayout.removeAllViews();
        answerFieldsLayout.setVisibility(View.GONE);
        helpButton.setVisibility(View.GONE);
        confirmAnswersButton.setText("結果を確認する");
        confirmAnswersButton.setOnClickListener(v -> goToResultScreen());
    }

    private void setupSingleVoteScreenForMode3() {
        // ▼▼▼【変更】投票画面のタイトル表示を更新 ▼▼▼
        playerNameTitleView.setText("人狼に投票");
        playerSubTitleView.setText(String.format("全員で相談して選択してください"));
        playerSubTitleView.setVisibility(View.VISIBLE);

        confirmAnswersButton.setText("投票を確定し結果を見る");

        answerFieldsLayout.removeAllViews();
        allSpinnersForMode3.clear();

        for (int i = 0; i < wolfCount; i++) {
            TextView questionTextView = new TextView(this);
            questionTextView.setText(String.format("人狼だと思うのは誰？ (%d人目)", i + 1));
            questionTextView.setTextSize(20);
            answerFieldsLayout.addView(questionTextView);

            Spinner playerSpinner = new Spinner(this);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, playerList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            playerSpinner.setAdapter(adapter);
            answerFieldsLayout.addView(playerSpinner);

            allSpinnersForMode3.add(playerSpinner);
        }
    }

    private void handleConfirmForMode3() {
        if (saveSingleVoteForMode3()) {
            goToResultScreen();
        }
    }

    private boolean saveSingleVoteForMode3() {
        Set<String> votedPlayers = new HashSet<>();
        for (Spinner spinner : allSpinnersForMode3) {
            votedPlayers.add(spinner.getSelectedItem().toString());
        }

        if (votedPlayers.size() < wolfCount) {
            Toast.makeText(this, "同じプレイヤーを重複して投票することはできません", Toast.LENGTH_SHORT).show();
            return false;
        }

        HashMap<String, String> voteResult = new HashMap<>();
        for (String votedPlayer : votedPlayers) {
            voteResult.put(votedPlayer, "人狼");
        }

        for (String player : playerList) {
            allAnswers.put(player, voteResult);
        }
        return true;
    }

    private void displayRoleExplanations() {
        if ("MBTI".equals(theme)) {
            role1_image.setImageResource(R.drawable.mbti_analyst);
            role1Title.setText("分析家");
            role1Description.setText("想像力が豊かで、知的好奇心が旺盛");

            role2_image.setImageResource(R.drawable.mbti_diplomat);
            role2Title.setText("外交官");
            role2Description.setText("人と付き合うことが得意で、仲介役やリーダー役に進んで手を挙げる");

            role3_image.setImageResource(R.drawable.mbti_guardian);
            role3Title.setText("番人");
            role3Description.setText("空想よりも事実にもとづいた思考を好む");

            role4_image.setImageResource(R.drawable.mbti_explorer);
            role4Title.setText("探検家");
            role4Description.setText("エネルギッシュで、退屈することを極端に嫌う");

        } else {
            role1_image.setImageResource(R.drawable.lovetype_lc);
            role1Title.setText("L×C（主導×甘えたい）");
            role1Description.setText("外向的で頼れる印象を与えながら、実はパートナーに安心感や愛情を求めやすい");

            role2_image.setImageResource(R.drawable.lovetype_la);
            role2Title.setText("L×A（主導×受け止めたい）");
            role2Description.setText("リーダーシップを発揮しつつ、相手の感情や立場を尊重できるため、信頼感を与えやすい");

            role3_image.setImageResource(R.drawable.lovetype_fc);
            role3Title.setText("F×C（協調×甘えたい）");
            role3Description.setText("自分から引っ張るよりは相手にリードしてほしいと感じやすく、安心できる相手と出会うと素直に甘えられる");

            role4_image.setImageResource(R.drawable.lovetype_fa);
            role4Title.setText("F×A（協調×受け止めたい）");
            role4Description.setText("聞き役やサポート役になることが多く、誠実で安心感のある関係を築きやすい");
        }
    }

    private List<String> getRoleOptions() {
        List<GameRole> roles = "MBTI".equals(theme) ? DataSource.getMbtiRoles() : DataSource.getLoveTypeRoles();
        List<String> roleNames = new ArrayList<>();
        for (GameRole role : roles) {
            roleNames.add(role.getName());
        }
        if (mode == 3) {
            roleNames.add("人狼");
        }
        return roleNames;
    }

    private void goToResultScreen() {
        Toast.makeText(this, "全員の回答が完了しました", Toast.LENGTH_SHORT).show();

        Intent intent;
        if (mode == 1) {
            intent = new Intent(this, ResultMode1Activity.class);
        } else if (mode == 2) {
            intent = new Intent(this, ResultMode2Activity.class);
        } else { // mode == 3
            intent = new Intent(this, ResultMode3Activity.class);
        }

        intent.putExtra("PLAYER_LIST", playerList);
        intent.putExtra("ASSIGNMENTS", assignments);
        intent.putExtra("ALL_ANSWERS", allAnswers);
        startActivity(intent);
        finish();
    }
}