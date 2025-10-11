package com.yahata.mbtiwolf;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import android.text.style.RelativeSizeSpan;

public class Mode1InputActivity extends BaseActivity {

    private TextView playerTurnTextView;
    private Spinner typeSpinner;
    private Button confirmTypeButton;
    private ImageView roleImageView;
    private TextView roleDescriptionTextView;

    private ArrayList<String> playerList;
    private String theme;
    private int currentPlayerIndex = 0;
    private ArrayList<GameRole> roleList;

    private HashMap<String, String> playerSelections = new HashMap<>();

    private Map<String, String> displayDescriptions = new HashMap<>();
    private Map<String, Integer> displayImageResIds = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode1_input);

        playerTurnTextView = findViewById(R.id.playerTurnTextView);
        typeSpinner = findViewById(R.id.typeSpinner);
        confirmTypeButton = findViewById(R.id.confirmTypeButton);
        roleImageView = findViewById(R.id.ImageView);
        roleDescriptionTextView = findViewById(R.id.RoleDescription);

        playerList = getIntent().getStringArrayListExtra("PLAYER_LIST");
        theme = getIntent().getStringExtra("GAME_THEME");
        roleList = (ArrayList<GameRole>) getIntent().getSerializableExtra("ROLE_LIST");

        initializeDisplayData();

        setupSpinner(theme);
        updateTurnView();

        typeSpinner.post(new Runnable() {
            @Override
            public void run() {
                if (typeSpinner.getSelectedItem() != null) {
                    String initialSelectedRoleName = typeSpinner.getSelectedItem().toString();
                    displayRoleDetails(initialSelectedRoleName);
                }
            }
        });

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedRoleName = parent.getItemAtPosition(position).toString();
                displayRoleDetails(selectedRoleName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                roleImageView.setVisibility(View.GONE);
                roleDescriptionTextView.setVisibility(View.GONE);
            }
        });


        confirmTypeButton.setOnClickListener(v -> {
            String selectedType = typeSpinner.getSelectedItem().toString();
            String currentPlayer = playerList.get(currentPlayerIndex);
            playerSelections.put(currentPlayer, selectedType);

            currentPlayerIndex++;

            if (currentPlayerIndex < playerList.size()) {
                updateTurnView();
                typeSpinner.setSelection(0);

            } else {
                goToDiscussionScreen();
            }
        });
    }

    private void goToDiscussionScreen() {
        Toast.makeText(this, "全員の入力が完了しました！", Toast.LENGTH_SHORT).show();

        HashMap<String, GameRole> assignments = createAssignmentsFromSelections();

        Intent intent = new Intent(Mode1InputActivity.this, DiscussionActivity.class);
        intent.putStringArrayListExtra("PLAYER_LIST", playerList);
        intent.putExtra("GAME_THEME", theme);
        intent.putExtra("GAME_MODE", 1);
        intent.putExtra("ASSIGNMENTS", assignments);
        intent.putExtra("ROLE_LIST", roleList);

        startActivity(intent);
        finish();
    }

    private HashMap<String, GameRole> createAssignmentsFromSelections() {
        HashMap<String, GameRole> assignmentsMap = new HashMap<>();

        for (Map.Entry<String, String> entry : playerSelections.entrySet()) {
            String playerName = entry.getKey();
            String roleName = entry.getValue();


            for (GameRole role : roleList) {
                if (role.getName().equals(roleName)) {
                    assignmentsMap.put(playerName, role);
                    break;
                }
            }
        }
        return assignmentsMap;
    }

    private void setupSpinner(String theme) {
        List<GameRole> roles = "MBTI".equals(theme) ? DataSource.getMbtiRoles() : DataSource.getLoveTypeRoles();
        List<String> roleNames = roles.stream().map(GameRole::getName).collect(Collectors.toList());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roleNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);
    }

    private void updateTurnView() {
        String playerName = playerList.get(currentPlayerIndex);
        String message = "\nさんの番です";

        // SpannableStringBuilderを使ってテキストを組み立てる
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

        playerTurnTextView.setText(ssb);

        if (currentPlayerIndex == playerList.size() - 1) {
            confirmTypeButton.setText("決定して議論を開始");
        } else {
            confirmTypeButton.setText("決定して次の人へ");
        }
    }

    private void initializeDisplayData() {
        displayDescriptions.clear();
        displayImageResIds.clear();

        if ("MBTI".equals(theme)) {
            displayDescriptions.put("分析家", "想像力が豊かで、知的好奇心が旺盛");
            displayDescriptions.put("外交官", "人と付き合うことが得意で、仲介役やリーダー役に進んで手を挙げる");
            displayDescriptions.put("番人", "空想よりも事実にもとづいた思考を好む");
            displayDescriptions.put("探検家", "エネルギッシュで、退屈することを極端に嫌う");


            displayImageResIds.put("分析家", R.drawable.mbti_analyst);
            displayImageResIds.put("外交官", R.drawable.mbti_diplomat);
            displayImageResIds.put("番人", R.drawable.mbti_guardian);
            displayImageResIds.put("探検家", R.drawable.mbti_explorer);

        } else { // LOVETYPEテーマ
            displayDescriptions.put("L×C（主導×甘えたい）", "外向的で頼れる印象を与えながら、実はパートナーに安心感や愛情を求めやすい");
            displayDescriptions.put("L×A（主導×受け止めたい）", "リーダーシップを発揮しつつ、相手の感情や立場を尊重できるため、信頼感を与えやすい");
            displayDescriptions.put("F×C（協調×甘えたい）", "自分から引っ張るよりは相手にリードしてほしいと感じやすく、安心できる相手と出会うと素直に甘えられる");
            displayDescriptions.put("F×A（協調×受け止めたい）", "聞き役やサポート役になることが多く、誠実で安心感のある関係を築きやすい");


            displayImageResIds.put("L×C（主導×甘えたい）", R.drawable.lovetype_lc);
            displayImageResIds.put("L×A（主導×受け止めたい）", R.drawable.lovetype_la);
            displayImageResIds.put("F×C（協調×甘えたい）", R.drawable.lovetype_fc);
            displayImageResIds.put("F×A（協調×受け止めたい）", R.drawable.lovetype_fa);
        }

    }

    private void displayRoleDetails(String roleName) {

        String descriptionToDisplay = displayDescriptions.get(roleName);

        Integer imageResId = displayImageResIds.get(roleName);


        if (descriptionToDisplay != null) {
            roleDescriptionTextView.setText(descriptionToDisplay);
            roleDescriptionTextView.setVisibility(View.VISIBLE);
        } else {
            roleDescriptionTextView.setVisibility(View.GONE);
        }


        if (imageResId != null && imageResId != 0) {
            roleImageView.setImageResource(imageResId);
            roleImageView.setVisibility(View.VISIBLE);
        } else {
            roleImageView.setVisibility(View.GONE);
        }
    }
}