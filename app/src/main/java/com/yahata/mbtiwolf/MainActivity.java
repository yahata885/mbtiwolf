package com.yahata.mbtiwolf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.content.SharedPreferences;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseActivity {

//    private RadioGroup themeRadioGroup;
    private final List<String> themeNames = Arrays.asList("MBTI", "LOVE_TYPE");
    private final List<String> themeDisplayNames = Arrays.asList("MBTI", "ラブタイプ");
    private final List<Integer> themeImages = Arrays.asList(R.drawable.mbti_image, R.drawable.lovetype_image);
    private ViewPager2 themeViewPager;
    private View mainActivityLayout;
//    private TabLayout themeTabLayout;
    private ImageButton buttonPrevious;
    private ImageButton buttonNext;
//    private ImageView indicatorLeft;
//    private ImageView indicatorRight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        themeRadioGroup = findViewById(R.id.themeRadioGroup);
        themeViewPager = findViewById(R.id.themeViewPager);
        buttonPrevious = findViewById(R.id.buttonPrevious);
        buttonNext = findViewById(R.id.buttonNext);

        mainActivityLayout = findViewById(R.id.main_activity_layout);

        Button mode1Button = findViewById(R.id.mode1Button);
        Button mode2Button = findViewById(R.id.mode2Button);
        Button mode3Button = findViewById(R.id.mode3Button);

        // Adapterを作成してViewPager2にセット
        ThemeAdapter adapter = new ThemeAdapter(themeImages);
        themeViewPager.setAdapter(adapter);


        //画像切り替え
        buttonNext.setOnClickListener(v -> {
            themeViewPager.setCurrentItem(1); // 2ページ目へ
        });
        buttonPrevious.setOnClickListener(v -> {
            themeViewPager.setCurrentItem(0); // 1ページ目へ
        });

        final TextView themeNameTextView = findViewById(R.id.themeNameTextView);

        themeViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                themeNameTextView.setText(themeNames.get(position));
                String displayText = "テーマ : " + themeDisplayNames.get(position);
                themeNameTextView.setText(displayText);

                // --- ここでインジケーターの色を切り替える ---
                if (position == 0) {
                    // 1ページ目が選択された場合
                    mainActivityLayout.setBackgroundResource(R.drawable.background_mbti);
                    buttonNext.setVisibility(View.VISIBLE);
                    buttonPrevious.setVisibility(View.GONE);
                } else {
                    // 2ページ目が選択された場合
                    mainActivityLayout.setBackgroundResource(R.drawable.background_lovetype);
                    buttonNext.setVisibility(View.GONE);
                    buttonPrevious.setVisibility(View.VISIBLE);
                }
            }
        });

        //初期設定
        mainActivityLayout.setBackgroundResource(R.drawable.background_mbti);
        String initialDisplayText = "テーマ : " + themeDisplayNames.get(0);
        themeNameTextView.setText(initialDisplayText);

//        themeNameTextView.setText(themeNames.get(0));
        buttonNext.setVisibility(View.VISIBLE);
        buttonPrevious.setVisibility(View.GONE);

        mode1Button.setOnClickListener(v -> startGame(1));
        mode2Button.setOnClickListener(v -> startGame(2));
        mode3Button.setOnClickListener(v -> startGame(3));
    }

    private void startGame(int mode) {
//        String selectedTheme = "MBTI";
//        if (themeRadioGroup.getCheckedRadioButtonId() == R.id.loveTypeRadioButton) {
//            selectedTheme = "LOVE_TYPE";
//        }
        int currentPosition = themeViewPager.getCurrentItem();

        // ページの番号に対応するテーマ名をリストから取得
        String selectedTheme = themeNames.get(currentPosition);

        SharedPreferences prefs = getSharedPreferences("app_theme_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("selected_theme", selectedTheme);
        editor.apply();

        Intent intent = new Intent(MainActivity.this, SetupActivity.class);
        intent.putExtra("GAME_THEME", selectedTheme);
        intent.putExtra("GAME_MODE", mode);
        startActivity(intent);
    }
}