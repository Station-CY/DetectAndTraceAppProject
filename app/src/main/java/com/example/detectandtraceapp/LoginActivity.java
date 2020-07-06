package com.example.detectandtraceapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.lang.ref.Reference;

public class LoginActivity extends AppCompatActivity {
    EditText mEmail, mPassword, mPhone, mAddress;
    Button mLoginBtn;
    CardView mCreateAccountBtn;
    Button mBackToHomeScreenBtn;

    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.emailText);
        mPassword = findViewById(R.id.passwordText);
        mPhone = findViewById(R.id.phoneField);
        mAddress = findViewById(R.id.addressField);
        mCreateAccountBtn = findViewById(R.id.createAccountText);
        mBackToHomeScreenBtn = findViewById(R.id.backToHomeScreenBtn);
        mLoginBtn = findViewById(R.id.loginToUserBtn);
        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);


        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                //User Validation: If a user tries to login without adding anything to the text field
                // (email and password), this would show an alert message (error).
                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is required");//Email must be a proper email, with @ and .com included
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is required");
                    return;
                }


                //Authenticate the user, if user is registered
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();

                            //Check the user's role and validates the view that will be displayed.
                            fStore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful() && task.getResult() != null){
                                        String personType = task.getResult().getString("personType");
                                        if(personType.equals("Staff"))
                                        {
                                            Intent activity1 = new Intent(getBaseContext(), StaffActivity.class);
                                            startActivity(activity1);
                                        }
                                        else
                                        {
                                            Intent activity2 = new Intent(getBaseContext(), UserActivity.class);
                                            startActivity(activity2);
                                        }
                                    }else{
                                        //deal with error
                                    }
                                }
                            });
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "The username and/or password is incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }


        });

        mCreateAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });

        mBackToHomeScreenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }
}
