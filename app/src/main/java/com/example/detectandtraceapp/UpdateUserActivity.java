package com.example.detectandtraceapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class UpdateUserActivity extends AppCompatActivity {
    public static final String TAG = "TAG";

    TextView currentUserEmail,  currentUserPhone;
    EditText  userEmailUpdate, userPhoneUpdate;
    Button updateEmailButton, updatePhoneButton;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);

        currentUserEmail = findViewById(R.id.currentEmailText);
        currentUserPhone = findViewById(R.id.currentPhoneText);
        userEmailUpdate = findViewById(R.id.updateEmailText);
        userPhoneUpdate = findViewById(R.id.updatePhoneText);
        updateEmailButton = findViewById(R.id.emailUpdateButton);
        updatePhoneButton = findViewById(R.id.phoneNumberUpdateButton);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userID = fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                currentUserEmail.setText(documentSnapshot.getString("email"));
                currentUserPhone.setText(documentSnapshot.getString("phone"));
            }
        });

        updateEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String updateCurrentEmail = userEmailUpdate.getText().toString();

                if (TextUtils.isEmpty(updateCurrentEmail)) {
                    userEmailUpdate.setError("Email is required");
                    return;
                }

                currentUser = fAuth.getCurrentUser();
                currentUser.updateEmail(updateCurrentEmail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User email updated!");
                                }
                            }
                        });

                DocumentReference emailUpdateReference = fStore.collection("users").document(userID);
                emailUpdateReference
                        .update("email", updateCurrentEmail)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(UpdateUserActivity.this, "Email Successfully Updated!", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                                userEmailUpdate.getText().clear();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating document", e);
                            }
                        });
            }
        });

        updatePhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String updatePhone = userPhoneUpdate.getText().toString();

                if (TextUtils.isEmpty(updatePhone)) {
                    userPhoneUpdate.setError("Phone number is required");
                    return;
                }

                DocumentReference emailUpdateReference = fStore.collection("users").document(userID);
                emailUpdateReference
                        .update("phone", updatePhone)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(UpdateUserActivity.this, "Phone Number Successfully Updated!", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                                userPhoneUpdate.getText().clear();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating document", e);
                            }
                        });
            }
        });
    }
}