package com.yahata.mbtiwolf;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.Set;
public class AnswerInputActivity extends AppCompatActivity {

    private TextView guesserNameTextView;
    private LinearLayout answerFieldsLayout;
    private Button confirmAnswersButton;

    // ▼▼▼【追加】役職説明画面用のUI変数を追加 ▼▼▼
    private ExtendedFloatingActionButton helpButton;
    private ConstraintLayout roleExplanationOverlayLayout;
    private TextView closeRoleExplanationButton;
    private ImageView role1_image, role2_image, role3_image, role4_image;
    private TextView role1Title, role1Description;
    private TextView role2Title, role2Description;
    private TextView role3Title, role3Description;
    private TextView role4Title, role4Description;
    // ▲▲▲【追加】ここまで ▲▲▲

    private ArrayList<String> playerList;
    private HashMap<String, GameRole> assignments;
    private int mode;
    private String theme;
    private int wolfCount;
    // --- モード1で使う変数 ---
    private int currentPlayerIndex = 0;
    private List<Spinner> currentSpinnersForMode1 = new ArrayList<>();

    // --- モード2で使う変数 ---
    //private HashMap<String, HashMap<String, Spinner>> allSpinnersForMode2 = new HashMap<>();

    // --- モード3で使う変数 ---
//    private HashMap<String, Spinner> allSpinnersForMode3 = new HashMap<>();
    private final List<Spinner> allSpinnersForMode3 = new ArrayList<>();
    // このActivityの成果物
    private HashMap<String, HashMap<String, String>> allAnswers = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_input);

        guesserNameTextView = findViewById(R.id.guesserNameTextView);
        answerFieldsLayout = findViewById(R.id.answerFieldsLayout);
        confirmAnswersButton = findViewById(R.id.confirmAnswersButton);

        // ▼▼▼【追加】役職説明画面のUI要素を紐付け ▼▼▼
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
        // ▲▲▲【追加】ここまで ▲▲▲

        playerList = getIntent().getStringArrayListExtra("PLAYER_LIST");
        assignments = (HashMap<String, GameRole>) getIntent().getSerializableExtra("ASSIGNMENTS");
        mode = getIntent().getIntExtra("GAME_MODE", 1);
        theme = getIntent().getStringExtra("GAME_THEME");
        wolfCount = getIntent().getIntExtra("WOLF_COUNT", 1);
        // ★★★ モードごとに処理を明確に分岐 ★★★
        if (mode == 1 || mode == 2) {
            setupTurnForMode1();
            confirmAnswersButton.setOnClickListener(v -> handleConfirmForMode1());
        } else if (mode == 3) {
            setupSingleVoteScreenForMode3();
            confirmAnswersButton.setOnClickListener(v -> handleConfirmForMode3());
        }

        // ▼▼▼【追加】役職説明画面の表示・非表示処理を追加 ▼▼▼
        helpButton.setOnClickListener(v -> {
            roleExplanationOverlayLayout.setVisibility(View.VISIBLE);
            displayRoleExplanations();
        });
        closeRoleExplanationButton.setOnClickListener(v -> {
            roleExplanationOverlayLayout.setVisibility(View.GONE);
        });
        // ▲▲▲【追加】ここまで ▲▲▲
    }

//        if (mode == 1) {
//            setupTurnForMode1();
//            confirmAnswersButton.setOnClickListener(v -> handleConfirmForMode1());
//        } else if (mode == 2) {
//            setupAllInOneScreenForMode2();
//            confirmAnswersButton.setOnClickListener(v -> handleConfirmForMode2());
//        } else if (mode == 3) {
//            setupSingleVoteScreenForMode3();
//            confirmAnswersButton.setOnClickListener(v -> handleConfirmForMode3());
//        }
//    }

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
    // ★★★ モード2用のロジック (全員分を一度に入力) ★★★
    // =================================================================

//    private void setupAllInOneScreenForMode2() {
//        guesserNameTextView.setText("全員の役職を予想してください");
//        confirmAnswersButton.setText("全員の回答を確定し結果を見る");
//        List<String> roleNames = getRoleOptions();
//
//        for (String guesser : playerList) {
//            TextView guesserHeader = new TextView(this);
//            guesserHeader.setText(guesser + "さんの予想");
//            guesserHeader.setTextSize(20);
//            guesserHeader.setTypeface(null, android.graphics.Typeface.BOLD);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            params.setMargins(0, 24, 0, 8);
//            guesserHeader.setLayoutParams(params);
//            answerFieldsLayout.addView(guesserHeader);
//
//            HashMap<String, Spinner> targetSpinners = new HashMap<>();
//
//            for (String target : playerList) {
//                if (guesser.equals(target)) continue;
//
//                LinearLayout rowLayout = new LinearLayout(this);
//                rowLayout.setOrientation(LinearLayout.HORIZONTAL);
//
//                TextView targetName = new TextView(this);
//                targetName.setText(target + "：");
//                targetName.setTextSize(18);
//
//                Spinner roleSpinner = new Spinner(this);
//                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roleNames);
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                roleSpinner.setAdapter(adapter);
//
//                rowLayout.addView(targetName);
//                rowLayout.addView(roleSpinner);
//                answerFieldsLayout.addView(rowLayout);
//
//                targetSpinners.put(target, roleSpinner);
//            }
//            allSpinnersForMode2.put(guesser, targetSpinners);
//        }
//    }
//
//    private void handleConfirmForMode2() {
//        saveAllAnswersFromScreenForMode2();
//        goToResultScreen();
//    }
//
//    private void saveAllAnswersFromScreenForMode2() {
//        for (String guesser : allSpinnersForMode2.keySet()) {
//            HashMap<String, String> currentGuesses = new HashMap<>();
//            HashMap<String, Spinner> targetSpinners = allSpinnersForMode2.get(guesser);
//
//            for (String target : targetSpinners.keySet()) {
//                Spinner spinner = targetSpinners.get(target);
//                String guess = spinner.getSelectedItem().toString();
//                currentGuesses.put(target, guess);
//            }
//            allAnswers.put(guesser, currentGuesses);
//        }
//    }

    // =================================================================
    // ★★★ モード3用のロジック (人狼への投票) ★★★
    // =================================================================

    //    private void setupSingleVoteScreenForMode3() {
//        guesserNameTextView.setText("全員で相談して、人狼だと思う人に投票してください");
//        confirmAnswersButton.setText("投票を確定し結果を見る");
//
//        answerFieldsLayout.removeAllViews();
//        allSpinnersForMode3.clear();
//
//        TextView questionTextView = new TextView(this);
//        questionTextView.setText("人狼だと思うのは誰？");
//        questionTextView.setTextSize(20);
//        answerFieldsLayout.addView(questionTextView);
//
//        Spinner playerSpinner = new Spinner(this);
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, playerList);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        playerSpinner.setAdapter(adapter);
//        answerFieldsLayout.addView(playerSpinner);
//
//        allSpinnersForMode3.put("vote", playerSpinner);
//    }
//
//    private void handleConfirmForMode3() {
//        saveSingleVoteForMode3();
//        goToResultScreen();
//    }
//
//    private void saveSingleVoteForMode3() {
//        String votedPlayer = allSpinnersForMode3.get("vote").getSelectedItem().toString();
//
//        HashMap<String, String> voteResult = new HashMap<>();
//        voteResult.put(votedPlayer, "人狼");
//
//        for (String player : playerList) {
//            allAnswers.put(player, voteResult);
//        }
//    }
    private void setupSingleVoteScreenForMode3() {
        guesserNameTextView.setText("全員で相談して、人狼だと思う人に投票してください");
        confirmAnswersButton.setText("投票を確定し結果を見る");

        answerFieldsLayout.removeAllViews();
        allSpinnersForMode3.clear();

        // wolfCountの数だけ投票欄を作成する
        for (int i = 0; i < wolfCount; i++) {
            TextView questionTextView = new TextView(this);
            // 何人目の投票かを表示
            questionTextView.setText(String.format("人狼だと思うのは誰？ (%d人目)", i + 1));
            questionTextView.setTextSize(20);
            answerFieldsLayout.addView(questionTextView);

            Spinner playerSpinner = new Spinner(this);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, playerList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            playerSpinner.setAdapter(adapter);
            answerFieldsLayout.addView(playerSpinner);

            // 作成したSpinnerをリストに追加して管理
            allSpinnersForMode3.add(playerSpinner);
        }
    }

    private void handleConfirmForMode3() {
        if (saveSingleVoteForMode3()) { // 保存処理が成功した場合のみ次に進む
            goToResultScreen();
        }
    }

    private boolean saveSingleVoteForMode3() {
        Set<String> votedPlayers = new HashSet<>();
        // 複数の投票結果を一時的に保持する
        for (Spinner spinner : allSpinnersForMode3) {
            votedPlayers.add(spinner.getSelectedItem().toString());
        }

        // 重複チェック：投票された人数が、設定された人狼の人数より少ない場合
        // (例: 2人投票のはずが、同じ人を2回選んでいる場合)
        if (votedPlayers.size() < wolfCount) {
            Toast.makeText(this, "同じプレイヤーを重複して投票することはできません", Toast.LENGTH_SHORT).show();
            return false; // 処理を中断
        }

        HashMap<String, String> voteResult = new HashMap<>();
        // 投票されたプレイヤーをすべて結果マップに入れる
        for (String votedPlayer : votedPlayers) {
            voteResult.put(votedPlayer, "人狼");
        }

        // 全プレイヤーの回答として、この投票結果を保存する
        for (String player : playerList) {
            allAnswers.put(player, voteResult);
        }
        return true; // 成功
    }

    // ▼▼▼【追加】DiscussionActivityからdisplayRoleExplanationsメソッドをコピー ▼▼▼
    private void displayRoleExplanations() {
        if ("MBTI".equals(theme)) {
            // --- MBTIのテーマの場合 ---
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
            // --- LOVETYPEのテーマの場合 (後で追加する部分) ---
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
    // ▲▲▲【追加】ここまで ▲▲▲

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