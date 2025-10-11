package com.yahata.mbtiwolf;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// ★追加: Spannable関連のimport文
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.graphics.Typeface;
import android.text.style.RelativeSizeSpan;

public class Mode23InputActivity extends BaseActivity {

    private TextView playerNameTextView, roleNameTextView, roleDescriptionTextView;
    private Button revealButton, nextPlayerButton;
    private Map<String, GameRole> assignments;
    private ArrayList<String> playerList;
    private int currentPlayerIndex = 0;
    private ArrayList<GameRole> roleList;
    private ImageView roleImageView;
    private Map<String, Integer> roleImageMap = new HashMap<>();
    private int wolfCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_reveal);

        playerNameTextView = findViewById(R.id.playerNameTextView);
        roleNameTextView = findViewById(R.id.roleNameTextView);
        roleDescriptionTextView = findViewById(R.id.roleDescriptionTextView);
        revealButton = findViewById(R.id.revealButton);
        nextPlayerButton = findViewById(R.id.nextPlayerButton);
        roleImageView = findViewById(R.id.roleImageView);

        playerList = getIntent().getStringArrayListExtra("PLAYER_LIST");
        String theme = getIntent().getStringExtra("GAME_THEME");
        int mode = getIntent().getIntExtra("GAME_MODE", 2);
        roleList = (ArrayList<GameRole>) getIntent().getSerializableExtra("ROLE_LIST");

        // SetupActivityから渡された人狼の人数を受け取る (デフォルトは0人)
        wolfCount = getIntent().getIntExtra("WOLF_COUNT", 0); // private変数に代入

        initializeRoleImages();

        assignments = GameLogic.assignRoles(playerList, theme, wolfCount, mode);

        updateTurnView();

        revealButton.setOnClickListener(v -> showRole());

        nextPlayerButton.setOnClickListener(v -> {
            currentPlayerIndex++;
            if (currentPlayerIndex < playerList.size()) {
                updateTurnView();
            } else {
                Toast.makeText(Mode23InputActivity.this, "全員の役割確認が完了しました", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(Mode23InputActivity.this, DiscussionActivity.class);
                intent.putStringArrayListExtra("PLAYER_LIST", playerList);
                intent.putExtra("GAME_THEME", theme);
                intent.putExtra("GAME_MODE", mode);
                // 「正解データ」を次の画面に渡す
                intent.putExtra("ASSIGNMENTS", (HashMap<String, GameRole>) assignments);
                intent.putExtra("ROLE_LIST", roleList);
                intent.putExtra("WOLF_COUNT", wolfCount);
                startActivity(intent);
                finish();
            }
        });

    }

    private void initializeRoleImages() {

        roleImageMap.put("分析家", R.drawable.mbti_analyst);
        roleImageMap.put("外交官", R.drawable.mbti_diplomat);
        roleImageMap.put("番人", R.drawable.mbti_guardian);
        roleImageMap.put("探検家", R.drawable.mbti_explorer);

        // 【ラブタイプテーマ】
        roleImageMap.put("L×C（主導×甘えたい）", R.drawable.lovetype_lc);
        roleImageMap.put("L×A（主導×受け止めたい）", R.drawable.lovetype_la);
        roleImageMap.put("F×C（協調×甘えたい）", R.drawable.lovetype_fc);
        roleImageMap.put("F×A（協調×受け止めたい）", R.drawable.lovetype_fa);

        // 【共通の役職】
        roleImageMap.put("人狼", R.drawable.wolf_image); // 例えば wolf_image.png がある場合
        // 他にも市民など共通の役職があれば追加
    }

    private void updateTurnView() {
        revealButton.setVisibility(View.VISIBLE);
        nextPlayerButton.setVisibility(View.GONE);
        roleNameTextView.setVisibility(View.INVISIBLE);
        roleDescriptionTextView.setVisibility(View.INVISIBLE);
        roleImageView.setVisibility(View.INVISIBLE);

        // ★★★ 修正箇所: ここから ★★★
        String playerName = playerList.get(currentPlayerIndex);
        String message = "\nさんの番です";

        SpannableStringBuilder ssb = new SpannableStringBuilder(playerName + message);

        // 1. playerNameの部分に太字スタイルを適用
        ssb.setSpan(
                new StyleSpan(Typeface.BOLD),
                0,
                playerName.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        // 2. messageの部分にRelativeSizeSpanを適用して文字を小さくする
        ssb.setSpan(
                new RelativeSizeSpan(0.6f), // 元のサイズの70%の大きさに設定
                playerName.length(),
                ssb.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        playerNameTextView.setText(ssb);
        // ★★★ 修正箇所: ここまで ★★★

        if (currentPlayerIndex == playerList.size() - 1) {
            nextPlayerButton.setText("確認完了（議論を開始）");
        } else {
            nextPlayerButton.setText("確認しました（次の人へ）");
        }
    }

    private void showRole() {
        String currentPlayerName = playerList.get(currentPlayerIndex);
        GameRole assignedRole = assignments.get(currentPlayerName);

        if (assignedRole != null) {
            String roleName = assignedRole.getName(); // 例: 分析家
            String prefix = "あなたの役割は\n";
            String roleText = "【" + assignedRole.getName() + "】"; // 例: 【分析家】
            // String suffix = "です"; // もし「です」などを追加するなら

            SpannableStringBuilder ssb = new SpannableStringBuilder();

            // 1. 「あなたの役割は」の部分を小さく追加
            int startPrefix = ssb.length();
            ssb.append(prefix);
            int endPrefix = ssb.length();
            ssb.setSpan(new RelativeSizeSpan(0.7f), startPrefix, endPrefix, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // 例: 70%のサイズ

            // 2. 役割名を太字で追加
            int startRoleText = ssb.length();
            ssb.append(roleText);
            int endRoleText = ssb.length();
            ssb.setSpan(new StyleSpan(Typeface.BOLD), startRoleText, endRoleText, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            // もし「です」などを追加するなら
            // ssb.append(suffix);

            roleNameTextView.setText(ssb); // 設定

            roleDescriptionTextView.setText(assignedRole.getDescription());

            Integer imageResId = roleImageMap.get(roleName);
            if (imageResId != null) {
                roleImageView.setImageResource(imageResId);
                roleImageView.setVisibility(View.VISIBLE);
            } else {
                roleImageView.setVisibility(View.GONE); // 画像がない場合は非表示
            }

            roleNameTextView.setVisibility(View.VISIBLE);
            roleDescriptionTextView.setVisibility(View.VISIBLE);
            revealButton.setVisibility(View.GONE);
            nextPlayerButton.setVisibility(View.VISIBLE);
        }
    }
}