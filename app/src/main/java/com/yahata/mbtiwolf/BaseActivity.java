package com.yahata.mbtiwolf;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        SharedPreferences prefs = getSharedPreferences("app_theme_prefs", MODE_PRIVATE);
        String theme = prefs.getString("selected_theme", "MBTI");

        int backgroundResId;
        if ("LOVE_TYPE".equals(theme)) {
            backgroundResId = R.drawable.background_lovetype;
        } else {
            backgroundResId = R.drawable.background_mbti;
        }

        getWindow().getDecorView().setBackgroundResource(backgroundResId);
    }
}