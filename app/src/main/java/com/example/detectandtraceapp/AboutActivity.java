package com.example.detectandtraceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    TextView tAndCView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        tAndCView = findViewById(R.id.aboutDescrption3Text);
        tAndCView.setMovementMethod(LinkMovementMethod.getInstance());

    }
}
