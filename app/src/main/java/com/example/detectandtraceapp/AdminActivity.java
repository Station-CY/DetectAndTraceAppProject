package com.example.detectandtraceapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;

public class AdminActivity extends AppCompatActivity {

    private static final String TAG = "";
    TextView textViewData, campusLabel, displayDate, clear, alert;
    DatePickerDialog.OnDateSetListener mDateSetListener;

    String emailList = "";
    String holderEmail;

    //For dropdown list
    String [] filterCampusItems = {"All", "MIT Mangere", "MIT Manukau", "MIT Otara", "New Zealand Maritime School"};
    ArrayAdapter<String> adapter;

    Spinner mFilterByCampusType;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectUsers = db.collection("locationRecord");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        textViewData = findViewById(R.id.text_view_data);

        clear = findViewById(R.id.clearLabel);
        alert = findViewById(R.id.alertLabel);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveItems();
                clear.setVisibility(View.GONE);
                alert.setVisibility(View.GONE);
                mFilterByCampusType.setSelection(0);
                displayDate.setText("Select Date:");
                displayDate.setVisibility(View.INVISIBLE);
            }
        });

        alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                composeEmail(emailList);
                emailList = "";
                holderEmail = "";
                Toast.makeText(AdminActivity.this,
                        "User(s) within date have been alerted.", Toast.LENGTH_SHORT).show();
            }
        });

        displayDate = findViewById(R.id.filterByDateLabel);
        displayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(AdminActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Log.d(TAG, "onDateSet: dd/mm/yyyy: " + dayOfMonth + "/" + (month + 1) + "/" + year);
                if(dayOfMonth <= 9 && month <= 9)
                {
                    final String testCampus = mFilterByCampusType.getSelectedItem().toString();
                    final String date = "0" + dayOfMonth + "/" + "0" + (month + 1) + "/" + year;
                    displayDate.setText(date);
                    filterItemsByDate(date, testCampus);
                    clear.setVisibility(View.VISIBLE);
                    alert.setVisibility(View.VISIBLE);
                }
                else if (dayOfMonth <= 9)
                {
                    final String testCampus = mFilterByCampusType.getSelectedItem().toString();
                    String date = "0" + dayOfMonth + "/" + (month + 1) + "/" + year;
                    displayDate.setText(date);
                    filterItemsByDate(date, testCampus);
                    clear.setVisibility(View.VISIBLE);
                    alert.setVisibility(View.VISIBLE);
                }
                else if (month <= 9)
                {
                    final String testCampus = mFilterByCampusType.getSelectedItem().toString();
                    String date = dayOfMonth + "/" + "0" + (month + 1) + "/" + year;
                    displayDate.setText(date);
                    filterItemsByDate(date, testCampus);
                    clear.setVisibility(View.VISIBLE);
                    alert.setVisibility(View.VISIBLE);
                }
                else
                {
                    final String testCampus = mFilterByCampusType.getSelectedItem().toString();
                    String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                    displayDate.setText(date);
                    filterItemsByDate(date, testCampus);
                    clear.setVisibility(View.VISIBLE);
                    alert.setVisibility(View.VISIBLE);
                }
            }
        };

        mFilterByCampusType = findViewById(R.id.type_campus_spinner);
        campusLabel = findViewById(R.id.filterByCampusTypeLabel);

        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, filterCampusItems);
        mFilterByCampusType.setAdapter(adapter);

        mFilterByCampusType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String campus;
                String dateHolder = displayDate.getText().toString();
                switch (position) {
                    case 0:
                        alert.setVisibility(View.GONE);
                        clear.setVisibility(View.GONE);
                        displayDate.setText("Select Date:");
                        displayDate.setVisibility(View.INVISIBLE);
                        retrieveItems();
                        break;
                    case 1:
                        campus = "MIT Mangere";
                        if(dateHolder.equals("Select Date:"))
                        {
                            filterItemsByCampus(campus);
                        }
                        else {
                            filterItemsByDate(dateHolder, campus);
                        }

                        displayDate.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        campus = "MIT Manukau";
                        if(dateHolder.equals("Select Date:"))
                        {
                            filterItemsByCampus(campus);
                        }
                        else {
                            filterItemsByDate(dateHolder, campus);
                        }
                        displayDate.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        campus = "MIT Otara";
                        if(dateHolder.equals("Select Date:"))
                        {
                            filterItemsByCampus(campus);
                        }
                        else {
                            filterItemsByDate(dateHolder, campus);
                        }
                        displayDate.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        campus = "New Zealand Maritime School";
                        if(dateHolder.equals("Select Date:"))
                        {
                            filterItemsByCampus(campus);
                        }
                        else {
                            filterItemsByDate(dateHolder, campus);
                        }
                        displayDate.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void retrieveItems()
    {
        collectUsers.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                String data = "";

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                    GetData getData = documentSnapshot.toObject(GetData.class);

                    String firstName = getData.getfName();
                    String lastName = getData.getlName();
                    String email = getData.getEmail();
                    String date = getData.getDate();
                    String campus = getData.getCampus();
                    String phone = getData.getPhone();

                    data += "First Name: " + firstName + "\nLast Name: " + lastName + "\nEmail: "
                            + email + "\nDate: " + date + "\nCampus: " + campus + "\nPhone: " + phone + "\n\n";
                }

                textViewData.setText(data);
            }
        });
    }


    protected void composeEmail(final String email) {
        Log.i("Send email", "");

        String recipientList = email;
        String[] recipients = recipientList.split(",");

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");


        emailIntent.putExtra(Intent.EXTRA_EMAIL, recipients);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "MIT Tracing App Alert!");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "You may have been in contact with someone who has contracted COVID19.\n" +
                "Please visit health government website regarding COVID-19 and follow government procedures regarding self " +
                "isolation.");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.i("Finished sending email", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(AdminActivity.this,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public void filterItemsByCampus(final String cType) {
        final String chosenCampus = cType;
        collectUsers.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                String data = "";

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                    GetData getData = documentSnapshot.toObject(GetData.class);

                    String firstName = getData.getfName();
                    String lastName = getData.getlName();
                    String email = getData.getEmail();
                    String date = getData.getDate();
                    String campus = getData.getCampus();
                    String phone = getData.getPhone();

                    if(chosenCampus.equals(campus)) {
                        data += "First Name: " + firstName + "\nLast Name: " + lastName + "\nEmail: "
                                + email + "\nDate: " + date + "\nCampus: " + campus + "\nPhone: " + phone + "\n\n";
                    }
                    else {

                    }
                }

                textViewData.setText(data);
            }
        });
    }

    public void filterItemsByDate(final String date, final String tCampus) {
        final String chosenDate = date;
        final String chosenCampus = tCampus;
        collectUsers.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                String data = "";

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                    GetData getData = documentSnapshot.toObject(GetData.class);

                    String firstName = getData.getfName();
                    String lastName = getData.getlName();
                    String email = getData.getEmail();
                    String date = getData.getDate();
                    String campus = getData.getCampus();
                    String phone = getData.getPhone();

                    if(chosenDate.equals(date) && chosenCampus.equals(campus)) {
                        data += "First Name: " + firstName + "\nLast Name: " + lastName + "\nEmail: "
                                + email + "\nDate: " + date + "\nCampus: " + campus + "\nPhone: " + phone + "\n\n";

                        if(email.equals(holderEmail))
                        {
                            //Does not repeat recipient already in the list
                        }else
                        {
                            holderEmail = email;
                            emailList += email + ",";
                        }
                    }
                    else {
                    }
                }
                textViewData.setText(data);
            }
        });
    }
}

