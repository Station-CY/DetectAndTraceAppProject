package com.example.detectandtraceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    //Create the variables according to the data type
    EditText mFirstName, mLastName, mPhone, mAddress, mEmail, mPassword, mReasonForVisiting;
    Button mRegisterBtn;
    CardView mLoginBtn;
    Button mBackToHomeScreenBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userID;

    //For dropdown list
    String [] items = {"Student", "Staff", "Visitor"};
    ArrayAdapter<String> adapter;

    Spinner mPersonType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Assign the variables with its id
        mFirstName = findViewById(R.id.firstNameField);
        mLastName = findViewById(R.id.lastNameField);
        mPhone = findViewById(R.id.phoneField);
        mAddress = findViewById(R.id.addressField);
        mEmail = findViewById(R.id.emailText);
        mPassword = findViewById(R.id.passwordText);

        mReasonForVisiting = findViewById(R.id.reasonForVisitingTextField);
        mRegisterBtn = findViewById(R.id.registerButton);
        mLoginBtn = findViewById(R.id.navigateToLogin);
        mBackToHomeScreenBtn = findViewById(R.id.backToHomeScreenBtn);

        mPersonType = findViewById(R.id.type_of_user_spinner);
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, items);
        mPersonType.setAdapter(adapter);

        mPersonType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mReasonForVisiting.setVisibility(View.GONE);//Student
                        break;
                    case 1:
                        mReasonForVisiting.setVisibility(View.GONE);//Staff
                        break;
                    case 2:
                        mReasonForVisiting.setVisibility(View.VISIBLE);//Visitor
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //Get the current instance of the database to take the assigned values for execution
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        progressBar = findViewById(R.id.progressBar);

        //User Validation: check if the user is currently logged in. If logged in then skip login process
        //Note: When user is logged in, they cannot register new user.
        //When logged out, then registering new user is available.

        //When the register button is clicked, we need to handle how the button is operated in terms of functionality.
        //In this case, we need a ViewOnClickListener for this

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String firstName = mFirstName.getText().toString();
                final String lastName = mLastName.getText().toString();
                final String phone = mPhone.getText().toString();
                String address = mAddress.getText().toString();
                final String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                final String personType = mPersonType.getSelectedItem().toString();

                //User Validation: If a user tries to login without adding anything to the text fields
                //, this would show an alert message (error).

                if (TextUtils.isEmpty(firstName)) {
                    mFirstName.setError("First Name is required");
                    return;
                }
                if (TextUtils.isEmpty(lastName)) {
                    mLastName.setError("Last Name is required");
                    return;
                }
                if (TextUtils.isEmpty(phone)) {
                    mPhone.setError("Phone is required");
                    return;
                }
                if (TextUtils.isEmpty(address)) {
                    mAddress.setError("Address is required");
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is required");//Email must be a proper email, with @ and .com included
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is required");
                    return;
                }
                //User validation should be at least 6 more more characters long
                if (password.length() < 6) {
                    mPassword.setError("Password must be 6 characters long");
                    return;
                }

                //Once all conditions have been met, set the progress bar to visible
                progressBar.setVisibility(View.VISIBLE);

                //Next, we register the user based on the initialized values above to firebase
                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                            //Save the user ID of the current user
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);

                            //Store the data into the database using Map
                            Map<String, Object> user = new HashMap<>();
                            user.put("fName", firstName);
                            user.put("lName", lastName);
                            user.put("phone", phone);
                            user.put("email", email);
                            user.put("personType", personType);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: user Profile is created for " + userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());
                                }
                            });
                            Intent activity1 = new Intent(getBaseContext(), LoginActivity.class);
                            startActivity(activity1);

                        } else {
                            Toast.makeText(RegisterActivity.this, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
           }
       });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
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