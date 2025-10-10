package com.yahata.mbtiwolf;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast; // 必要に応じて追加

public class Title extends BaseActivity {

    private Button gamestartButton;
    private Button gameexplanationButton;
    private Button creditButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title); // ★変更: activity_title.xml を読み込む
        findViewById(android.R.id.content).getRootView().setBackgroundResource(R.drawable.background_title);

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

        // ★★★ ここを修正します ★★★
        // 「ゲーム説明」ボタンのリスナー
        gameexplanationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ゲーム説明画面（ModeExplanationActivity）へ遷移
                Intent intent = new Intent(Title.this, ModeExplanation_Activity.class); // ModeExplanationActivityを指定
                startActivity(intent);
            }
        });

        // 「クレジット」ボタンのリスナー
        creditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // クレジット画面（CreditActivity）へ遷移
                Intent intent = new Intent(Title.this, Creditactivity.class);
                startActivity(intent);
            }
        });
    }
}