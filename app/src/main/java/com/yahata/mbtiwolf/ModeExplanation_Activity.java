package com.yahata.mbtiwolf;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
// import android.widget.RadioButton; // RadioButtonを直接参照する場合にインポート

public class ModeExplanation_Activity extends AppCompatActivity { // クラス名もModeExplanationActivityに修正

    private RadioGroup modeSelectionRadioGroup;
    private LinearLayout mode12ExplanationBlock;
    private LinearLayout mode3ExplanationBlock;
    private Button backToTitleButton; // backToTitleButtonを宣言

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_explanation);

        // UI要素の取得
        modeSelectionRadioGroup = findViewById(R.id.modeSelectionRadioGroup);
        mode12ExplanationBlock = findViewById(R.id.mode12ExplanationBlock);
        mode3ExplanationBlock = findViewById(R.id.mode3ExplanationBlock);
        backToTitleButton = findViewById(R.id.backToTitleButton); // ★★★ ここを追加します ★★★

        // ★★★ デフォルト表示の設定 ★★★
        // XMLで「協力/演じて」のRadioButtonにandroid:checked="true"が設定されているため
        // mode12ExplanationBlockをVISIBLE、mode3ExplanationBlockをGONEにする
        mode12ExplanationBlock.setVisibility(View.VISIBLE);
        mode3ExplanationBlock.setVisibility(View.GONE);

        // ★★★ ラジオボタンの選択変更リスナーの設定 ★★★
        modeSelectionRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.mode1RadioButton) {
                    // 「協力/演じて」ラジオボタンが選択された場合
                    mode12ExplanationBlock.setVisibility(View.VISIBLE);
                    mode3ExplanationBlock.setVisibility(View.GONE);
                } else if (checkedId == R.id.mode3RadioButton) {
                    // 「人狼」ラジオボタンが選択された場合
                    mode12ExplanationBlock.setVisibility(View.GONE);
                    mode3ExplanationBlock.setVisibility(View.VISIBLE);
                }
            }
        });

        // ★★★ 戻るボタンのクリックリスナーの設定 ★★★
        // backToTitleButtonは上記でfindViewByIdされているため、nullチェックは必須ではありませんが、
        // レイアウトにボタンがない場合に備えて残すのは安全です。
        if (backToTitleButton != null) {
            backToTitleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish(); // 現在のActivityを終了し、起動元のActivity（Title）に戻る
                }
            });
        }
    }
}