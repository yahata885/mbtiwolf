package com.yahata.mbtiwolf;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class DiscussionActivity extends AppCompatActivity {

    private TextView timerTextView;
    private TextView playerListTextView;
    private Button goToVoteButton;
    private Handler timerHandler = new Handler(Looper.getMainLooper());
    private long startTime = 0;
    private ArrayList<String> playerList;

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            timerTextView.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
            timerHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion);

        timerTextView = findViewById(R.id.timerTextView);
        playerListTextView = findViewById(R.id.playerListTextView);
        goToVoteButton = findViewById(R.id.goToVoteButton);

        goToVoteButton.setEnabled(true);
        playerList = getIntent().getStringArrayListExtra("PLAYER_LIST");
        displayPlayerList(playerList);

        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);

        goToVoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerHandler.removeCallbacks(timerRunnable);

                Intent intent = new Intent(DiscussionActivity.this, AnswerInputActivity.class);

                // ★★★ 受け取った情報を全て次の画面に渡す ★★★
                intent.putStringArrayListExtra("PLAYER_LIST", playerList);
                intent.putExtra("GAME_THEME", getIntent().getStringExtra("GAME_THEME"));
                intent.putExtra("GAME_MODE", getIntent().getIntExtra("GAME_MODE", 2));
                intent.putExtra("ASSIGNMENTS", (HashMap<String, GameRole>) getIntent().getSerializableExtra("ASSIGNMENTS"));

                startActivity(intent);
                finish();
            }
        });
    }

    private void displayPlayerList(ArrayList<String> players) {
        if (players != null) {
            StringBuilder text = new StringBuilder();
            for (String player : players) {
                text.append(player).append("\n");
            }
            playerListTextView.setText(text.toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(timerRunnable);
    }
}