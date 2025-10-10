package com.yahata.mbtiwolf;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View; // ★追加: Viewクラスをインポート
import android.widget.AdapterView; // ★追加: AdapterViewをインポート
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView; // ★追加: ImageViewをインポート
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Mode1InputActivity extends AppCompatActivity {

    private TextView playerTurnTextView;
    private Spinner typeSpinner;
    private Button confirmTypeButton;
    private ImageView roleImageView; // ★追加: 画像表示用ImageView
    private TextView roleDescriptionTextView; // ★追加: 説明文表示用TextView

    private ArrayList<String> playerList;
    private String theme;
    private int currentPlayerIndex = 0;
    private ArrayList<GameRole> roleList; // SetupActivityから渡された全ロールのデータ

    private HashMap<String, String> playerSelections = new HashMap<>();

    // ★★★ 変更点1: 短縮された説明文と画像リソースIDを直接記述するHashMap ★★★
    private Map<String, String> displayDescriptions = new HashMap<>();
    private Map<String, Integer> displayImageResIds = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode1_input);

        playerTurnTextView = findViewById(R.id.playerTurnTextView);
        typeSpinner = findViewById(R.id.typeSpinner);
        confirmTypeButton = findViewById(R.id.confirmTypeButton);
        roleImageView = findViewById(R.id.ImageView); // ★変更点2: ImageViewを取得
        roleDescriptionTextView = findViewById(R.id.RoleDescription); // ★変更点2: TextViewを取得

        playerList = getIntent().getStringArrayListExtra("PLAYER_LIST");
        theme = getIntent().getStringExtra("GAME_THEME");
        roleList = (ArrayList<GameRole>) getIntent().getSerializableExtra("ROLE_LIST");

        // ★★★ 変更点3: 説明文と画像リソースIDのマップを初期化 ★★★
        initializeDisplayData(); // 新しい初期化メソッドを呼び出し

        setupSpinner(theme);
        updateTurnView();

        // ★★★ 変更点4: 初期表示のため、スピナー選択時に表示を更新（UI描画後に実行） ★★★
        typeSpinner.post(new Runnable() {
            @Override
            public void run() {
                String initialSelectedRoleName = typeSpinner.getSelectedItem().toString();
                displayRoleDetails(initialSelectedRoleName);
            }
        });

        // ★★★ 変更点5: Spinnerの項目選択リスナーを設定 ★★★
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedRoleName = parent.getItemAtPosition(position).toString();
                displayRoleDetails(selectedRoleName); // 選択された役割の詳細を表示する
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 何も選択されていない場合の処理（今回は特に何もしなくても良い）
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
                // 次のプレイヤーのためにスピナーの選択をリセットします。
                typeSpinner.setSelection(0);
                // ★変更点6: 画像と説明文を非表示に戻す ★
//                roleImageView.setVisibility(View.GONE);
//                roleDescriptionTextView.setVisibility(View.GONE);

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

    // ★★ プレイヤーの選択(String)から、役職データ(GameRole)のマップを作成するメソッド ★★
    private HashMap<String, GameRole> createAssignmentsFromSelections() {
        HashMap<String, GameRole> assignmentsMap = new HashMap<>();
        // roleList (全ロールのリスト) を直接使用
        // List<GameRole> allRoles = "MBTI".equals(theme) ? DataSource.getMbtiRoles() : DataSource.getLoveTypeRoles(); // 変更なし

        for (Map.Entry<String, String> entry : playerSelections.entrySet()) {
            String playerName = entry.getKey();
            String roleName = entry.getValue();

            // 名前に一致するGameRoleオブジェクトを探す
            for (GameRole role : roleList) { // roleListを使用
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
        playerTurnTextView.setText(playerList.get(currentPlayerIndex) + "さんの番です");
        if (currentPlayerIndex == playerList.size() - 1) {
            confirmTypeButton.setText("決定して議論を開始");
        }
    }

    // ★★★ 変更点7: 新規追加メソッド: 説明文と画像リソースIDのマップを初期化 ★★★
    private void initializeDisplayData() {
        displayDescriptions.clear(); // 念のためクリア
        displayImageResIds.clear(); // 念のためクリア

        if ("MBTI".equals(theme)) {
            displayDescriptions.put("分析家", "想像力が豊かで、知的好奇心が旺盛");
            displayDescriptions.put("外交官", "人と付き合うことが得意で、仲介役やリーダー役に進んで手を挙げる");
            displayDescriptions.put("番人", "空想よりも事実にもとづいた思考を好む");
            displayDescriptions.put("探検家", "エネルギッシュで、退屈することを極端に嫌う");

            // 画像リソースIDを直接指定
            displayImageResIds.put("分析家", R.drawable.mbti_analyst1); // ★res/drawable/mbti_analyst.pngが存在することを前提
            displayImageResIds.put("外交官", R.drawable.mbti_diplomat1);
            displayImageResIds.put("番人", R.drawable.mbti_guardian1);
            displayImageResIds.put("探検家", R.drawable.mbti_explorer1);

        } else { // LOVETYPEテーマ
            displayDescriptions.put("L×C（主導×甘えたい）", "外向的で頼れる印象を与えながら、実はパートナーに安心感や愛情を求めやすい");
            displayDescriptions.put("L×A（主導×受け止めたい）", "リーダーシップを発揮しつつ、相手の感情や立場を尊重できるため、信頼感を与えやすい");
            displayDescriptions.put("F×C（協調×甘えたい）", "自分から引っ張るよりは相手にリードしてほしいと感じやすく、安心できる相手と出会うと素直に甘えられる");
            displayDescriptions.put("F×A（協調×受け止めたい）", "聞き役やサポート役になることが多く、誠実で安心感のある関係を築きやすい");

            // 画像リソースIDを直接指定
            displayImageResIds.put("L×C（主導×甘えたい）", R.drawable.lovetype_lc);
            displayImageResIds.put("L×A（主導×受け止めたい）", R.drawable.lovetype_la);
            displayImageResIds.put("F×C（協調×甘えたい）", R.drawable.lovetype_fc);
            displayImageResIds.put("F×A（協調×受け止めたい）", R.drawable.lovetype_fa);
        }
        // 人狼の役割はスピナーに表示されないため、ここでは画像・説明文は不要。
        // 必要であれば追加。
    }

    // ★★★ 変更点8: 新規追加メソッド: 選択された役割の詳細を表示 ★★★
    private void displayRoleDetails(String roleName) {
        // 説明文を取得
        String descriptionToDisplay = displayDescriptions.get(roleName);
        // 画像リソースIDを取得
        Integer imageResId = displayImageResIds.get(roleName); // Integerを使うことでnullチェックが可能

        // 説明文の表示
        if (descriptionToDisplay != null) {
            roleDescriptionTextView.setText(descriptionToDisplay);
            roleDescriptionTextView.setVisibility(View.VISIBLE);
        } else {
            roleDescriptionTextView.setVisibility(View.GONE);
        }

        // 画像の表示
        if (imageResId != null && imageResId != 0) { // nullチェックと0チェック
            roleImageView.setImageResource(imageResId);
            roleImageView.setVisibility(View.VISIBLE);
        } else {
            roleImageView.setVisibility(View.GONE);
        }
    }
}