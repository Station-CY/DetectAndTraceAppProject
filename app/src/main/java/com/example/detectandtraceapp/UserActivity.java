package com.example.detectandtraceapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class UserActivity extends AppCompatActivity {
    TextView firstName, role;
    RelativeLayout firstUserActivity, secondUserActivity, thirdUserActivity, fourthUserActivity;
    Button userLogout;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        firstName = findViewById(R.id.firstNameLabel);
        role = findViewById(R.id.roleHolder);

        firstUserActivity = findViewById(R.id.first_user_activity);
        secondUserActivity = findViewById(R.id.second_user_activity);
        thirdUserActivity = findViewById(R.id.third_user_activity);
        fourthUserActivity = findViewById(R.id.fourth_user_activity);

        userLogout = findViewById(R.id.logoutUserButton);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userID = fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                firstName.setText(documentSnapshot.getString("fName"));
            }
        });


        firstUserActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), QRCodeScannerActivity.class));
            }
        });

        secondUserActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AdminActivity.class));
            }
        });


        thirdUserActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UpdateUserActivity.class));
            }
        });

        fourthUserActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AboutActivity.class));
            }
        });

        userLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                finish();
                overridePendingTransition(0, 0);
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

    }

    //To make the user unable to press back button after successful login.
    @Override
    public void onBackPressed() {

    }

}
