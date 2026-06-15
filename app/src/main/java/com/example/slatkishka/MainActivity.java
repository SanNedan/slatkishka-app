package com.example.slatkishka;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // иницијализација на SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        // проверуваме дали под клучот username има зачувано корисник
        boolean isLoggedIn = sharedPreferences.contains("username");

        SessionManager session = new SessionManager(this);
        if (savedInstanceState == null) {
            if (session.isLoggedIn()) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new com.example.slatkishka.fragments.ListFragment())
                        .commit();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new com.example.slatkishka.fragments.LoginFragment())
                        .commit();
            }
        }
    }
}