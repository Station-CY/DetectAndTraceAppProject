package com.example.detectandtraceapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CardView cardView1 = (CardView) findViewById(R.id.first_activity);
        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activity1 = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(activity1);
            }
        });

        CardView cardView2 = (CardView) findViewById(R.id.second_activity);
        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activity2 = new Intent(getBaseContext(), RegisterActivity.class);
                startActivity(activity2);
            }
        });
    }
}
