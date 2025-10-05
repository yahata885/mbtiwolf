package com.yahata.mbtiwolf;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.List;
import java.util.Collections;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.widget.ImageView;

public class DiscussionActivity extends AppCompatActivity {

    //時間入力用UI
    private Button addMinuteButton;
    private Button subtractMinuteButton;
    private Button startButton;

    //役職リスト表示用
    private LinearLayout roleListLayout;

    // --- ヒント機能 ---
    private Button hintButton1, hintButton2, hintButton3;
    private List<String> masterHintList = new ArrayList<>();
    private List<String> selectedHints = new ArrayList<>();

    // 4分割エリアのImageView変数を追加
    private ImageView role1_image, role2_image, role3_image, role4_image;

    // 4分割エリアのTextView変数を追加
    private TextView role1Title, role1Description;
    private TextView role2Title, role2Description;
    private TextView role3Title, role3Description;
    private TextView role4Title, role4Description;
    private String theme;

    // --- 役職説明画面 ---
    private FloatingActionButton helpButton; // 「?」ボタン
    private ConstraintLayout roleExplanationOverlayLayout; // 役職説明画面の全体
    private TextView closeRoleExplanationButton; // 役職説明画面の「✖」ボタン



    private TextView timerTextView;
    private TextView playerListTextView;
    private Button goToVoteButton;
    private Handler timerHandler = new Handler(Looper.getMainLooper());
    //    private long startTime = 0;
    private ArrayList<String> playerList;
    private long timeRemainingInMillis = 0;
    private int wolfCount;
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (timeRemainingInMillis > 0) {
                timeRemainingInMillis -= 1000; // 1秒減らす

                long minutes = (timeRemainingInMillis / 1000) / 60;
                long seconds = (timeRemainingInMillis / 1000) % 60;
                timerTextView.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));

                timerHandler.postDelayed(this, 1000); // 1秒後にもう一度実行
            } else {
                // タイムアップ時の処理
                timerTextView.setText("00:00");
                Toast.makeText(DiscussionActivity.this, "議論終了！", Toast.LENGTH_SHORT).show();
//                goToVoteButton.setEnabled(true); // 投票ボタンを有効化
//                startButton.setEnabled(false); // 開始ボタンを無効化
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion);


        addMinuteButton = findViewById(R.id.addMinuteButton);
        subtractMinuteButton = findViewById(R.id.subtractMinuteButton);
        startButton = findViewById(R.id.startButton);
        timerTextView = findViewById(R.id.timerTextView);
//        playerListTextView = findViewById(R.id.playerListTextView);
        goToVoteButton = findViewById(R.id.goToVoteButton);


//        roleListLayout = findViewById(R.id.roleListLayout);
//        ArrayList<GameRole> roleList = (ArrayList<GameRole>) getIntent().getSerializableExtra("ROLE_LIST");
//        displayRoleList(roleList);

        // 変更箇所: 4分割エリアのImageView,TextViewを関連付け
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

//      //bmtiかlovetypeか
        theme = getIntent().getStringExtra("GAME_THEME");

        // --- ヒント機能 ---
        hintButton1 = findViewById(R.id.hintButton1);
        hintButton2 = findViewById(R.id.hintButton2);
        hintButton3 = findViewById(R.id.hintButton3);

        // --- 役職説明画面 ---
        helpButton = findViewById(R.id.helpButton);
        roleExplanationOverlayLayout = findViewById(R.id.roleExplanationOverlayLayout);
        closeRoleExplanationButton = findViewById(R.id.closeRoleExplanationButton);

        // ヒントの初期設定を行う
        initializeHints();

        // ヒントボタンのクリック処理
        hintButton1.setOnClickListener(v -> {
            if (selectedHints.size() > 0) {
                hintButton1.setText(selectedHints.get(0));
                hintButton1.setClickable(false);
            }
        });
        hintButton2.setOnClickListener(v -> {
            if (selectedHints.size() > 1) {
                hintButton2.setText(selectedHints.get(1));
                hintButton2.setClickable(false);
            }
        });
        hintButton3.setOnClickListener(v -> {
            if (selectedHints.size() > 2) {
                hintButton3.setText(selectedHints.get(2));
                hintButton3.setClickable(false);
            }
        });

        // 役職説明画面の表示・非表示処理
        helpButton.setOnClickListener(v -> {
            roleExplanationOverlayLayout.setVisibility(View.VISIBLE);
            displayRoleExplanations();
        });
        closeRoleExplanationButton.setOnClickListener(v -> {
            roleExplanationOverlayLayout.setVisibility(View.GONE);
        });

        goToVoteButton.setEnabled(true);

        timeRemainingInMillis = 3 * 60 * 1000;
        updateTimerDisplay();

        playerList = getIntent().getStringArrayListExtra("PLAYER_LIST");
//        displayPlayerList(playerList);
        wolfCount = getIntent().getIntExtra("WOLF_COUNT", 1);
        //  「+1分」ボタンの処理
        addMinuteButton.setOnClickListener(v -> {
            timeRemainingInMillis += 60 * 1000;
            updateTimerDisplay();
        });

        //  「-1分」ボタンの処理
        subtractMinuteButton.setOnClickListener(v -> {
            if (timeRemainingInMillis >= 60 * 1000) { // 0分未満にならないように
                timeRemainingInMillis -= 60 * 1000;
                updateTimerDisplay();
            }
        });

        //  「開始」ボタンの処理
        startButton.setOnClickListener(v -> {
            // タイマー開始
            timerHandler.postDelayed(timerRunnable, 1000);

            // 開始後は時間調整ボタンを無効化
            startButton.setEnabled(false);
//            addMinuteButton.setEnabled(false);
//            subtractMinuteButton.setEnabled(false);
        });

        // 7. 「投票へ」ボタンの処理
        goToVoteButton.setOnClickListener(v -> {
            timerHandler.removeCallbacks(timerRunnable);
            Intent intent = new Intent(DiscussionActivity.this, AnswerInputActivity.class);
            intent.putStringArrayListExtra("PLAYER_LIST", playerList);
            intent.putExtra("GAME_THEME", getIntent().getStringExtra("GAME_THEME"));
            intent.putExtra("GAME_MODE", getIntent().getIntExtra("GAME_MODE", 2));
            intent.putExtra("ASSIGNMENTS", (HashMap<String, GameRole>) getIntent().getSerializableExtra("ASSIGNMENTS"));
            intent.putExtra("WOLF_COUNT", wolfCount);
            startActivity(intent);
            finish();
        });
    }

    // 変更箇所: 役職説明をテーマによって分岐させるようにメソッドを書き換え(DataSourceから引っ張ってもいい）
    private void displayRoleExplanations() {
        if ("MBTI".equals(theme)) {
            // --- MBTIのテーマの場合 ---
            role1_image.setImageResource(R.drawable.mbti_analyst);
            role1Title.setText("分析家");
            role1Description.setText("分析家は想像力が豊かで、知的好奇心が旺盛");

            role2_image.setImageResource(R.drawable.mbti_diplomat);
            role2Title.setText("外交官");
            role2Description.setText("外交官は人と付き合うことが得意で、仲介役やリーダー役に進んで手を挙げる");

            role3_image.setImageResource(R.drawable.mbti_guardian);
            role3Title.setText("番人");
            role3Description.setText("番人は空想よりも事実にもとづいた思考を好む");

            role4_image.setImageResource(R.drawable.mbti_explorer);
            role4Title.setText("探検家");
            role4Description.setText("探検家はエネルギッシュで、退屈することを極端に嫌う");

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



    private void initializeHints() {
        if ("MBTI".equals(theme)){
            setupMasterHintList_mbti();
        }
        else{
            setupMasterHintList_lovetype();
        }
        selectRandomHints();
    }

    private void setupMasterHintList_mbti() {
        masterHintList.add("ウルフは嘘をついている可能性が高い。");
        masterHintList.add("議論にあまり参加しない人は怪しいかもしれない。");
        masterHintList.add("誰かを不自然にかばっている人はいないか？");
        masterHintList.add("MBTIタイプだけで判断するのは危険だ。");
        masterHintList.add("最初のテーマと話題がズレてきていないか注意しよう。");
        masterHintList.add("他の人の意見にすぐに便乗する人は、自分の意見がないのかもしれない。");
        masterHintList.add("矛盾した発言をしている人はいないか、よく思い出そう。");
    }

    private void setupMasterHintList_lovetype() {
        masterHintList.add("過去の大失敗は？");
        masterHintList.add("過去の大失敗は？");
        masterHintList.add("過去の大失敗は？");
        masterHintList.add("過去の大失敗は？");
        masterHintList.add("過去の大失敗は？");
        masterHintList.add("過去の大失敗は？");
        masterHintList.add("過去の大失敗は？");
    }

    private void selectRandomHints() {
        // ボタンの状態を初期状態に戻す
        hintButton1.setText("ヒント1を表示");
        hintButton1.setClickable(true);
        hintButton2.setText("ヒント2を表示");
        hintButton2.setClickable(true);
        hintButton3.setText("ヒント3を表示");
        hintButton3.setClickable(true);

        // ランダムにヒントを選ぶ処理
        List<String> shuffledList = new ArrayList<>(masterHintList);
        Collections.shuffle(shuffledList);
        selectedHints.clear();
        for (int i = 0; i < 3 && i < shuffledList.size(); i++) {
            selectedHints.add(shuffledList.get(i));
        }
    }

    // ★ タイマー表示を更新する共通メソッド
    private void updateTimerDisplay() {
        long minutes = (timeRemainingInMillis / 1000) / 60;
        long seconds = (timeRemainingInMillis / 1000) % 60;
        timerTextView.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
    }

//    private void displayRoleList(ArrayList<GameRole> roles) {
//        if (roles == null || roles.isEmpty()) {
//            return;
//        }
//
//        // 既存の表示をクリア
//        roleListLayout.removeAllViews();
//
//        for (GameRole role : roles) {
//            // 役職名を表示するTextViewを動的に作成
//            TextView roleNameTextView = new TextView(this);
//            roleNameTextView.setText("・" + role.getName());
//            roleNameTextView.setTextSize(20);
//            roleNameTextView.setPadding(8, 16, 8, 16);
//
//            // タップされた時の処理を設定
//            roleNameTextView.setOnClickListener(v -> {
//                // ポップアップ（AlertDialog）で説明文を表示
//                new AlertDialog.Builder(this)
//                        .setTitle(role.getName()) // ポップアップのタイトル
//                        .setMessage(role.getDescription()) // ポップアップの本文
//                        .setPositiveButton("OK", null) // OKボタン
//                        .show();
//            });
//
//            // レイアウトに作成したTextViewを追加
//            roleListLayout.addView(roleNameTextView);
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(timerRunnable);
    }
}