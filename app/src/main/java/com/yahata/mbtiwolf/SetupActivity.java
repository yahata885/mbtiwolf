package com.yahata.mbtiwolf;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class SetupActivity extends AppCompatActivity {

    private final ArrayList<String> playerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        EditText playerNameEditText = findViewById(R.id.playerNameEditText);
        Button addPlayerButton = findViewById(R.id.addPlayerButton);
        TextView playerListTextView = findViewById(R.id.playerListTextView);
        Button confirmButton = findViewById(R.id.confirmButton);


        //名前の入力処理
        addPlayerButton.setOnClickListener(v -> {
            //入力された名前をゲット
            String playerName = playerNameEditText.getText().toString().trim();
            //名前が空白以外の場合
            if (!playerName.isEmpty()) {
                playerList.add(playerName);
                updatePlayerListView(playerListTextView);
                playerNameEditText.setText("");
            //その他
            } else {
                Toast.makeText(SetupActivity.this, "名前を入力してください", Toast.LENGTH_SHORT).show();
            }
        });

        //確定ボタン
        confirmButton.setOnClickListener(v -> {
            //プレイヤー数に応じる
            if (playerList.size() >= 2) {
                String theme = getIntent().getStringExtra("GAME_THEME");
                int mode = getIntent().getIntExtra("GAME_MODE", 1);

                java.util.List<GameRole> rolesToShow;
                if ("MBTI".equals(theme)) {
                    rolesToShow = DataSource.getMbtiRoles();
                } else {
                    rolesToShow = DataSource.getLoveTypeRoles();
                }

                Intent intent;
                if (mode == 1) {
                    intent = new Intent(SetupActivity.this, Mode1InputActivity.class);
                } else {
                    intent = new Intent(SetupActivity.this, Mode23InputActivity.class);
                }
                intent.putExtra("GAME_THEME", theme);
                intent.putExtra("GAME_MODE", mode);
                intent.putStringArrayListExtra("PLAYER_LIST", playerList);
                intent.putExtra("ROLE_LIST", new ArrayList<>(rolesToShow));
                startActivity(intent);

            } else {
                Toast.makeText(SetupActivity.this, "プレイヤーを2人以上追加してください", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePlayerListView(TextView playerListTextView) {
        StringBuilder text = new StringBuilder();
        for (String player : playerList) {
            text.append(player).append("\n");
        }
        playerListTextView.setText(text.toString());
    }
}