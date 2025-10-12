package com.yahata.mbtiwolf;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResultMode3Activity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_mode3);

        TextView wolfVoteResultTextView = findViewById(R.id.wolfVoteResultTextView);
        TextView winnerTextView = findViewById(R.id.winnerTextView);
        LinearLayout roleGuessResultsLayout = findViewById(R.id.roleGuessResultsLayout);
        Button playAgainButton = findViewById(R.id.playAgainButton);

        HashMap<String, GameRole> assignments = (HashMap<String, GameRole>) getIntent().getSerializableExtra("ASSIGNMENTS");
        HashMap<String, HashMap<String, String>> allAnswers = (HashMap<String, HashMap<String, String>>) getIntent().getSerializableExtra("ALL_ANSWERS");

        determineWinner(wolfVoteResultTextView, winnerTextView, assignments, allAnswers);
        displayFinalRoles(roleGuessResultsLayout, assignments);

        playAgainButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void determineWinner(TextView voteResultView, TextView winnerView, HashMap<String, GameRole> assignments, HashMap<String, HashMap<String, String>> allAnswers) {
        AnimationSet winnerAnimation = new AnimationSet(true);
        ScaleAnimation scale = new ScaleAnimation(
                0.8f, 1.0f, 0.8f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        scale.setDuration(600);
        winnerAnimation.addAnimation(scale);
        AlphaAnimation alpha = new AlphaAnimation(0.0f, 1.0f);
        alpha.setDuration(400);
        winnerAnimation.addAnimation(alpha);

        HashMap<String, String> finalVote = null;
        if (allAnswers != null && !allAnswers.isEmpty()) {
            finalVote = allAnswers.values().iterator().next();
        }

        if (finalVote == null || finalVote.isEmpty()) {
            voteResultView.setText("投票が行われませんでした。");
            winnerView.setText("🏆 人狼チームの勝利！ 🏆");
            winnerView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
            winnerView.startAnimation(winnerAnimation);
            return;
        }

        boolean wasWolfFound = false;
        for (Map.Entry<String, String> entry : finalVote.entrySet()) {
            String suspectedPlayer = entry.getKey();
            String guess = entry.getValue();
            GameRole actualRole = assignments.get(suspectedPlayer);

            if (actualRole != null && actualRole.getName().equals("人狼") && guess.equals("人狼")) {
                wasWolfFound = true;
                break;
            }
        }

        if (wasWolfFound) {
            voteResultView.setText("市民チームは人狼を正しく特定しました。");
            winnerView.setText("🏆 市民チームの勝利！ 🏆");
            winnerView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
        } else {
            voteResultView.setText("市民チームは人狼を特定できませんでした。");
            winnerView.setText("🏆 人狼チームの勝利！ 🏆");
            winnerView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        }
        winnerView.startAnimation(winnerAnimation);
    }

    private void displayFinalRoles(LinearLayout layout, HashMap<String, GameRole> assignments) {
        layout.removeAllViews();
        layout.setGravity(Gravity.TOP);

        Map<String, List<String>> rolesMap = new LinkedHashMap<>();
        for (Map.Entry<String, GameRole> entry : assignments.entrySet()) {
            String playerName = entry.getKey();
            String roleName = entry.getValue().getName();
            rolesMap.computeIfAbsent(roleName, k -> new ArrayList<>()).add(playerName);
        }

        for (Map.Entry<String, List<String>> entry : rolesMap.entrySet()) {
            String roleName = entry.getKey();
            List<String> playerNames = entry.getValue();
            String cardTitle;

            if (roleName.equals("人狼")) {
                cardTitle = "人狼チーム";
            } else {
                // ここを変更: 区切り文字を | に変更
                cardTitle = "市民チーム | " + roleName;
            }

            Map<String, List<String>> singleRoleMap = new LinkedHashMap<>();
            singleRoleMap.put(roleName, playerNames);

            createTeamCard(layout, cardTitle, singleRoleMap);
        }
    }

    private void createTeamCard(LinearLayout parentLayout, String teamTitle, Map<String, List<String>> rolesMap) {
        int padding_16dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
        int margin_8dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());

        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(margin_8dp, margin_8dp, margin_8dp, margin_8dp);
        cardView.setLayoutParams(cardParams);
        cardView.setRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics()));
        cardView.setCardElevation(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()));

        LinearLayout innerLayout = new LinearLayout(this);
        innerLayout.setOrientation(LinearLayout.VERTICAL);
        innerLayout.setPadding(padding_16dp, padding_16dp, padding_16dp, padding_16dp);

        if (teamTitle != null && !teamTitle.isEmpty()) {
            TextView titleView = new TextView(this);
            titleView.setText(teamTitle);
            titleView.setTextSize(14);
            titleView.setTextColor(Color.WHITE);
            titleView.setTypeface(null, Typeface.BOLD);

            GradientDrawable chipBackground = new GradientDrawable();
            chipBackground.setShape(GradientDrawable.RECTANGLE);

            if (teamTitle.startsWith("人狼")) {
                chipBackground.setColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
            } else {
                chipBackground.setColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
            }
            chipBackground.setCornerRadius(50f);
            titleView.setBackground(chipBackground);

            int paddingHorizontal = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics());
            int paddingVertical = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
            titleView.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical);

            LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            titleParams.setMargins(0, 0, 0, padding_16dp);
            titleView.setLayoutParams(titleParams);

            innerLayout.addView(titleView);
        }

        for (Map.Entry<String, List<String>> entry : rolesMap.entrySet()) {
            List<String> playerNames = entry.getValue();
            for (String playerName : playerNames) {
                TextView playerLine = new TextView(this);
                String text = "・" + playerName + " さん"; // ここにスペースを追加しました

                // SpannableStringを使って部分的にスタイルを適用
                SpannableString spannable = new SpannableString(text);
                // "・" の次の文字から、プレイヤー名の長さ分だけを太字にする
                spannable.setSpan(new StyleSpan(Typeface.BOLD), 1, 1 + playerName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                playerLine.setText(spannable);
                playerLine.setTextSize(18);
                playerLine.setPadding(padding_16dp, 0, 0, margin_8dp);
                innerLayout.addView(playerLine);
            }
        }

        cardView.addView(innerLayout);
        parentLayout.addView(cardView);
    }
}