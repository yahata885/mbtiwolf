package com.yahata.mbtiwolf;

import androidx.appcompat.app.AlertDialog;
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
import android.widget.EditText;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class DiscussionActivity extends AppCompatActivity {

    //時間入力用UI
    private Button addMinuteButton;
    private Button subtractMinuteButton;
    private Button startButton;

    //役職リスト表示用
    private LinearLayout roleListLayout;

    private TextView timerTextView;
    private TextView playerListTextView;
    private Button goToVoteButton;
    private Handler timerHandler = new Handler(Looper.getMainLooper());
//    private long startTime = 0;
    private ArrayList<String> playerList;
    private long timeRemainingInMillis = 0;

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


        roleListLayout = findViewById(R.id.roleListLayout);
        ArrayList<GameRole> roleList = (ArrayList<GameRole>) getIntent().getSerializableExtra("ROLE_LIST");
        displayRoleList(roleList);

        goToVoteButton.setEnabled(true);

        timeRemainingInMillis = 3 * 60 * 1000;
        updateTimerDisplay();

        playerList = getIntent().getStringArrayListExtra("PLAYER_LIST");
//        displayPlayerList(playerList);

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
            startActivity(intent);
            finish();
        });
    }


    // ★ タイマー表示を更新する共通メソッド
    private void updateTimerDisplay() {
        long minutes = (timeRemainingInMillis / 1000) / 60;
        long seconds = (timeRemainingInMillis / 1000) % 60;
        timerTextView.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
    }

    private void displayRoleList(ArrayList<GameRole> roles) {
        if (roles == null || roles.isEmpty()) {
            return;
        }

        // 既存の表示をクリア
        roleListLayout.removeAllViews();

        for (GameRole role : roles) {
            // 役職名を表示するTextViewを動的に作成
            TextView roleNameTextView = new TextView(this);
            roleNameTextView.setText("・" + role.getName());
            roleNameTextView.setTextSize(20);
            roleNameTextView.setPadding(8, 16, 8, 16);

            // タップされた時の処理を設定
            roleNameTextView.setOnClickListener(v -> {
                // ポップアップ（AlertDialog）で説明文を表示
                new AlertDialog.Builder(this)
                        .setTitle(role.getName()) // ポップアップのタイトル
                        .setMessage(role.getDescription()) // ポップアップの本文
                        .setPositiveButton("OK", null) // OKボタン
                        .show();
            });

            // レイアウトに作成したTextViewを追加
            roleListLayout.addView(roleNameTextView);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(timerRunnable);
    }
}