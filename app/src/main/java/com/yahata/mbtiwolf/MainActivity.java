package com.yahata.mbtiwolf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RadioGroup themeRadioGroup;
    private ViewPager2 themeViewPager;
    private TabLayout themeTabLayout;
    private final List<String> themeNames = Arrays.asList("MBTI", "LOVE_TYPE");
    private final List<Integer> themeImages = Arrays.asList(R.drawable.mbti_image, R.drawable.lovetype_image);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        themeRadioGroup = findViewById(R.id.themeRadioGroup);
        themeViewPager = findViewById(R.id.themeViewPager);
        themeTabLayout = findViewById(R.id.themeTabLayout);
        Button mode1Button = findViewById(R.id.mode1Button);
        Button mode2Button = findViewById(R.id.mode2Button);
        Button mode3Button = findViewById(R.id.mode3Button);

        // Adapterを作成してViewPager2にセット
        ThemeAdapter adapter = new ThemeAdapter(themeImages);
        themeViewPager.setAdapter(adapter);

        // TabLayoutとViewPager2を連携させる
        new TabLayoutMediator(themeTabLayout, themeViewPager, (tab, position) -> {
            // ここでタブのテキストなどを設定できるが、今回はインジケーターのみなので空でOK
            tab.setText(themeNames.get(position));
        }).attach();

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

        Intent intent = new Intent(MainActivity.this, SetupActivity.class);
        intent.putExtra("GAME_THEME", selectedTheme);
        intent.putExtra("GAME_MODE", mode);
        startActivity(intent);
    }
}