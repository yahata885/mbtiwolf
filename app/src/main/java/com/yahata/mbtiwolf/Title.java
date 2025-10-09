package com.yahata.mbtiwolf;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast; // 必要に応じて追加

public class Title extends AppCompatActivity {

    private Button gamestartButton;
    private Button gameexplanationButton;
    private Button creditButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title); // ★変更: activity_title.xml を読み込む

        // UI要素の取得
        gamestartButton = findViewById(R.id.gamestartButton);
        gameexplanationButton = findViewById(R.id.gameexplanationButton);
        creditButton = findViewById(R.id.creditButton);

        // 「ゲームスタート」ボタンのリスナー
        gamestartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // MainActivityへ遷移
                Intent intent = new Intent(Title.this, MainActivity.class);
                startActivity(intent);
                // Title Activityを終了する場合は以下を追加
                // finish();
            }
        });

        // 「ゲーム説明」ボタンのリスナー
        gameexplanationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ゲーム説明画面（GameExplanationActivity）へ遷移
                // GameExplanationActivityはまだ存在しないため、Toastで仮表示
                Toast.makeText(Title.this, "ゲーム説明画面へ（まだ作成していません）", Toast.LENGTH_SHORT).show();
                // 実際には以下のように遷移
                // Intent intent = new Intent(Title.this, GameExplanationActivity.class);
                // startActivity(intent);
            }
        });

        // 「クレジット」ボタンのリスナー（必要であれば）
        creditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // クレジット画面へ遷移、またはToastで仮表示
                Toast.makeText(Title.this, "クレジット画面へ（必要であれば実装）", Toast.LENGTH_SHORT).show();
            }
        });
    }
}