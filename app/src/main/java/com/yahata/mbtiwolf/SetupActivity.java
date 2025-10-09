package com.yahata.mbtiwolf;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class SetupActivity extends AppCompatActivity {

    private final ArrayList<String> playerList = new ArrayList<>();
    // 人狼の人数を管理する変数。モード3のデフォルトは1人
    private int wolfCount = 1;
    private ChipGroup playerChipGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        // --- UI要素の取得 ---
        EditText playerNameEditText = findViewById(R.id.playerNameEditText);
        Button addPlayerButton = findViewById(R.id.addPlayerButton);
        //TextView playerListTextView = findViewById(R.id.playerListTextView);
        Button confirmButton = findViewById(R.id.confirmButton);
        playerChipGroup = findViewById(R.id.playerChipGroup);
        // 新しいUI要素を取得
        LinearLayout wolfCountLayout = findViewById(R.id.wolfCountLayout);
        Button minusButton = findViewById(R.id.minusButton);
        Button plusButton = findViewById(R.id.plusButton);
        TextView wolfCountTextView = findViewById(R.id.wolfCountTextView);

        // --- モードに応じて人狼設定UIの表示を切り替え ---
        int mode = getIntent().getIntExtra("GAME_MODE", 1);
        if (mode == 3) {
            wolfCountLayout.setVisibility(View.VISIBLE);
        } else {
            wolfCountLayout.setVisibility(View.GONE);
            wolfCount = 0; // モード3以外は人狼0人で確定
        }

        // 人数表示を初期化
        wolfCountTextView.setText(String.valueOf(wolfCount));

        // --- ボタンのクリックリスナー設定 ---

        // (変更あり) 名前入力処理
        addPlayerButton.setOnClickListener(v -> {
            String playerName = playerNameEditText.getText().toString().trim();

            // 最初に名前が空でないかチェック
            if (playerName.isEmpty()) {
                Toast.makeText(SetupActivity.this, "名前を入力してください", Toast.LENGTH_SHORT).show();
                return; // ここで処理を中断
            }

            // ▼▼▼【変更点】名前の文字数チェックを追加 ▼▼▼
            if (playerName.length() > 6) {
                Toast.makeText(SetupActivity.this, "名前は6文字以内で入力してください", Toast.LENGTH_SHORT).show();
                return; // ここで処理を中断
            }
            // ▲▲▲【変更点】ここまで ▲▲▲

            // 名前の重複チェック
            if (playerList.contains(playerName)) {
                Toast.makeText(SetupActivity.this, "この名前は既に使用されています", Toast.LENGTH_SHORT).show();
                return; // ここで処理を中断
            }

            // 全てのチェックをクリアしたらリストに追加
            playerList.add(playerName);
            //updatePlayerListView(playerListTextView);
            updatePlayerTags();
            playerNameEditText.setText("");
        });

        // 「+」ボタンの処理
        plusButton.setOnClickListener(v -> {
            // プレイヤーは3人以上
            if (playerList.size() < 3) {
                Toast.makeText(SetupActivity.this, "先にプレイヤーを追加してください", Toast.LENGTH_SHORT).show();
                return;
            }

            //人狼の
            int playerCount = playerList.size();
            int maxWolfCount;
            if (playerCount % 2 == 0) {
                maxWolfCount = (playerCount / 2) - 1;
            } else {
                maxWolfCount = playerCount / 2;
            }

            if (wolfCount < maxWolfCount ) {
                wolfCount++;
                wolfCountTextView.setText(String.valueOf(wolfCount));
            } else {
                Toast.makeText(SetupActivity.this, "人狼が多すぎます", Toast.LENGTH_SHORT).show();
            }
        });

        // 「-」ボタンの処理
        minusButton.setOnClickListener(v -> {
            // 人狼は最低1人
            if (wolfCount > 1) {
                wolfCount--;
                wolfCountTextView.setText(String.valueOf(wolfCount));
            } else {
                Toast.makeText(SetupActivity.this, "人狼は最低1人です", Toast.LENGTH_SHORT).show();
            }
        });

        // 確定ボタンの処理
        confirmButton.setOnClickListener(v -> {
            if (playerList.size() < 2 && mode == 1) {
                Toast.makeText(SetupActivity.this, "プレイヤーを2人以上追加してください", Toast.LENGTH_SHORT).show();
                return;
            }
            else if (playerList.size() < 2 && mode == 2) {
                Toast.makeText(SetupActivity.this, "プレイヤーを2人以上追加してください", Toast.LENGTH_SHORT).show();
                return;
            }
            else if (playerList.size() < 3 && mode == 3) {
                Toast.makeText(SetupActivity.this, "プレイヤーを3人以上追加してください", Toast.LENGTH_SHORT).show();
                return;
            }


            // モード3の場合、プレイヤー数が人狼の数より多いか最終チェック
            if (mode == 3 && playerList.size() <= wolfCount) {
                Toast.makeText(SetupActivity.this, "プレイヤー数が人狼の数より多くなるようにしてください", Toast.LENGTH_SHORT).show();
                return;
            }

            // --- Intentの準備 (以前のコードから流用) ---
            String theme = getIntent().getStringExtra("GAME_THEME");
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
            intent.putExtra("WOLF_COUNT", wolfCount);
            startActivity(intent);
        });
    }

    private void updatePlayerTags() {
        // 1. 一旦ChipGroup内のすべてのタグを削除して、表示をリセットする
        playerChipGroup.removeAllViews();

        // 2. playerListの全プレイヤー名に対してループ処理を行う
        for (String playerName : playerList) {
            // 3. 新しいChip（タグ）を生成
            Chip chip = new Chip(this);
            chip.setText(playerName); // タグにプレイヤー名を設定
            chip.setCloseIconVisible(true); // 閉じるボタン（×）を表示

            // 4. 閉じるボタンがクリックされた時の処理を設定
            chip.setOnCloseIconClickListener(v -> {
                playerList.remove(playerName); // リストから該当プレイヤーを削除
                updatePlayerTags(); // タグ表示を再更新
            });

            // 5. 完成したChipをChipGroupに追加
            playerChipGroup.addView(chip);
        }
    }
//    private void updatePlayerListView(TextView playerListTextView) {
//        StringBuilder text = new StringBuilder();
//        for (String player : playerList) {
//            text.append(player).append("\n");
//        }
//        playerListTextView.setText(text.toString());
//    }
}