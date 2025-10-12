package com.yahata.mbtiwolf;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResultMode2Activity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_mode2); // XMLファイル名を確認してください

        TextView topPlayerTextView = findViewById(R.id.topPlayerTextView);
        LinearLayout playerRolesLayout = findViewById(R.id.playerRolesLayout);
        LinearLayout resultsLayout = findViewById(R.id.resultsLayout);
        Button playAgainButton = findViewById(R.id.playAgainButton);

        ArrayList<String> playerList = (ArrayList<String>) getIntent().getSerializableExtra("PLAYER_LIST");
        HashMap<String, GameRole> assignments = (HashMap<String, GameRole>) getIntent().getSerializableExtra("ASSIGNMENTS");
        HashMap<String, HashMap<String, String>> allAnswers = (HashMap<String, HashMap<String, String>>) getIntent().getSerializableExtra("ALL_ANSWERS");

        // 各プレイヤーの役割を表示
        displayPlayerRoles(playerRolesLayout, assignments);

        resultsLayout.removeAllViews();
        TextView resultsTitle = new TextView(this);
        resultsTitle.setText("予想の結果");
        resultsTitle.setTextSize(22);
        resultsTitle.setTypeface(null, Typeface.BOLD);
        resultsTitle.setGravity(Gravity.CENTER_HORIZONTAL); // 「予想の結果」タイトルを中央配置
        resultsLayout.addView(resultsTitle);

        HashMap<String, Integer> playerScores = new HashMap<>();
        for (String guesser : playerList) {

            SpannableStringBuilder guesserTitleSpannable = new SpannableStringBuilder("▼ " + guesser + "さんの予想結果");
            guesserTitleSpannable.setSpan(new StyleSpan(Typeface.BOLD), 2, 2 + guesser.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            TextView guesserTitle = new TextView(this);
            guesserTitle.setText(guesserTitleSpannable);
            guesserTitle.setTextSize(20);
            guesserTitle.setPadding(0, 24, 0, 8);
            resultsLayout.addView(guesserTitle);

            int score = 0;
            HashMap<String, String> guesses = allAnswers.get(guesser);

            if (guesses != null) {
                // ★★★ このforループの中身を修正します ★★★
                for (Map.Entry<String, String> entry : guesses.entrySet()) {
                    String target = entry.getKey();
                    String guess = entry.getValue(); // これが予想した役割名
                    String correctAnswer = assignments.get(target).getName();

                    // 一つのSpannableStringBuilderで全ての情報を構築
                    SpannableStringBuilder resultLineSpannable = new SpannableStringBuilder();

                    // 1. 〇✖マークと色付け
                    if (guess.equals(correctAnswer)) {
                        score++;
                        resultLineSpannable.append("〇 ");
                        resultLineSpannable.setSpan(new ForegroundColorSpan(Color.RED), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else {
                        resultLineSpannable.append("✖ ");
                        resultLineSpannable.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    // 2. ターゲット名を太字で追加（スペースを空けずに「さん」と結合）
                    int targetNameStart = resultLineSpannable.length();
                    resultLineSpannable.append(target);
                    int targetNameEnd = resultLineSpannable.length();
                    resultLineSpannable.setSpan(new StyleSpan(Typeface.BOLD), targetNameStart, targetNameEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    // 3. 「さん」と「役割名」を続けて追加
                    // ここで「さん」と「役割名」の間に空白を入れるかどうか調整できます
                    resultLineSpannable.append("さん「" + guess + "」"); // 「さん」と「役割名」の間に半角スペース1つ

                    // 全てを一つのTextViewにセット
                    TextView resultLine = new TextView(this);
                    resultLine.setText(resultLineSpannable);
                    resultLine.setTextSize(16);

                    // レイアウトパラメータを設定（親の幅いっぱいに広げる）
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    resultLine.setLayoutParams(params);

                    // 必要であれば、TextView全体のパディングを調整
                    // 例: resultLine.setPadding(0, 4, 0, 4); // 上下に少しパディング
                    resultLine.setPadding(0, 0, 0, 0); // 完全にパディングなし

                    resultsLayout.addView(resultLine);
                }
            }
            playerScores.put(guesser, score);
        }

        // 1. playerScoresマップから最高スコアを特定
        int maxScore = 0;
        if (!playerScores.isEmpty()) {
            maxScore = playerScores.values().stream()
                    .mapToInt(Integer::intValue)
                    .max()
                    .orElse(0);
        }

        // 2. 最高スコアを持つプレイヤーを特定
        final int finalMaxScore = maxScore; // ラムダ式で使うためにfinal変数にする (修正点)
        List<String> topPlayers = playerScores.entrySet().stream()
                .filter(entry -> entry.getValue() == finalMaxScore) // finalMaxScore を使用 (修正点)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // 3. 結果を表示するロジック
        if (maxScore == 0 && !playerList.isEmpty()) {
            topPlayerTextView.setText("正解者はいませんでした！");
        } else if (topPlayers.size() == playerList.size() && !topPlayers.isEmpty()) {
            topPlayerTextView.setText("全員同点です！");
        } else if (!topPlayers.isEmpty()){
            SpannableStringBuilder topPlayerSpannable = new SpannableStringBuilder("最も成績が良かったのは ");
            for (int i = 0; i < topPlayers.size(); i++) {
                String player = topPlayers.get(i);
                int start = topPlayerSpannable.length();
                topPlayerSpannable.append(player); // プレイヤー名を追加
                int end = topPlayerSpannable.length();
                // プレイヤー名の部分だけを太字＆大きくする
                topPlayerSpannable.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                topPlayerSpannable.setSpan(new RelativeSizeSpan(1.2f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                // 「さん」や読点「、」を追加 (太字ではない)
                if (i < topPlayers.size() - 1) {
                    topPlayerSpannable.append("さん、");
                } else {
                    topPlayerSpannable.append("さん");
                }
            }
            topPlayerSpannable.append(" です！");
            topPlayerTextView.setText(topPlayerSpannable);
        }

        playAgainButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, Title.class); // 遷移先をTitle.classに修正
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void displayPlayerRoles(LinearLayout layout, HashMap<String, GameRole> assignments) {
        layout.removeAllViews();

        TextView title = new TextView(this);
        title.setText("各プレイヤーの役割");
        title.setTextSize(22);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        title.setPadding(0, 0, 0, 8);
        layout.addView(title);

        for (Map.Entry<String, GameRole> entry : assignments.entrySet()) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 8, 0, 8);
            row.setGravity(Gravity.CENTER_VERTICAL);

            String playerNameStr = entry.getKey();
            SpannableStringBuilder playerNameSpannable = new SpannableStringBuilder(playerNameStr + " さん");
            playerNameSpannable.setSpan(new StyleSpan(Typeface.BOLD), 0, playerNameStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            TextView playerNameView = new TextView(this);
            playerNameView.setText(playerNameSpannable);
            playerNameView.setTextSize(17);

            // ★★★ 修正点1: プレイヤー名側のweightを1.0fに設定 ★★★
            LinearLayout.LayoutParams playerParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            playerNameView.setLayoutParams(playerParams);

            TextView roleNameView = new TextView(this);
            roleNameView.setText(entry.getValue().getName());
            roleNameView.setTextSize(17);
            roleNameView.setGravity(Gravity.END);
//            // ★★★ 修正点2: 右側のパディングを少し追加 ★★★
//            roleNameView.setPadding(0, 0, 8, 0); // 右側に8dpのパディング

            // ★★★ 修正点2: 役職名側のweightを1.5fに設定 ★★★
            LinearLayout.LayoutParams roleParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.5f);
            roleNameView.setLayoutParams(roleParams);

            row.addView(playerNameView);
            row.addView(roleNameView);
            layout.addView(row);
        }
    }
}