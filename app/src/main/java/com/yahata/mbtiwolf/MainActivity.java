package com.yahata.mbtiwolf;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity {

    private RadioGroup themeRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        themeRadioGroup = findViewById(R.id.themeRadioGroup);
        Button mode1Button = findViewById(R.id.mode1Button);
        Button mode2Button = findViewById(R.id.mode2Button);
        Button mode3Button = findViewById(R.id.mode3Button);

        mode1Button.setOnClickListener(v -> startGame(1));
        mode2Button.setOnClickListener(v -> startGame(2));
        mode3Button.setOnClickListener(v -> startGame(3));
    }

    private void startGame(int mode) {
        String selectedTheme = "MBTI";
        if (themeRadioGroup.getCheckedRadioButtonId() == R.id.loveTypeRadioButton) {
            selectedTheme = "LOVE_TYPE";
        }

        Intent intent = new Intent(MainActivity.this, SetupActivity.class);
        intent.putExtra("GAME_THEME", selectedTheme);
        intent.putExtra("GAME_MODE", mode);
        startActivity(intent);
    }
}