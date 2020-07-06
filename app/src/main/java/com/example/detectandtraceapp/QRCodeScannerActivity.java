package com.example.detectandtraceapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.os.Vibrator;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.auth.User;
import com.google.firestore.v1.FirestoreGrpc;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.sql.Ref;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class QRCodeScannerActivity extends AppCompatActivity {
    TextView textView, addData1, addData2, addData3, addData4;
    Button addButton;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    DatabaseReference ref;

    CodeScanner codeScanner;
    CodeScannerView cScannerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_code_scanner);

        cScannerView = findViewById(R.id.scannerView);
        codeScanner = new CodeScanner(this, cScannerView);
        textView = findViewById(R.id.qrTextView);

        addData1 = findViewById(R.id.myLabel1);
        addData2 = findViewById(R.id.myLabel2);
        addData3 = findViewById(R.id.myLabel3);
        addData4 = findViewById(R.id.myLabel4);
        addButton = findViewById(R.id.myButton);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        final FirebaseUser user = fAuth.getCurrentUser();
        userID = user.getUid();

        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                addData1.setText(documentSnapshot.getString("fName"));
                addData2.setText(documentSnapshot.getString("lName"));
                addData3.setText(documentSnapshot.getString("phone"));
                addData4.setText(documentSnapshot.getString("email"));
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String campus = textView.getText().toString();
                String fName = addData1.getText().toString();
                String lName = addData2.getText().toString();
                String phone = addData3.getText().toString();
                String email = addData4.getText().toString();
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date date = new Date();
                String strDate = dateFormat.format(date);


                Map<String, Object> user = new HashMap<>();
                user.put("campus", campus);
                user.put("fName", fName);
                user.put("lName", lName);
                user.put("phone", phone);
                user.put("email", email);
                user.put("date", strDate);

                fStore.collection("locationRecord").add(user);

                Toast.makeText(QRCodeScannerActivity.this, "Location Successfully Logged.", Toast.LENGTH_SHORT).show();

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
        });

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(result.getText());
                        addButton.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        cScannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestForCamera();
    }

    private void requestForCamera() {
        Dexter.withActivity(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                codeScanner.startPreview();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Toast.makeText(QRCodeScannerActivity.this, "Camera Permission is required.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }



}
