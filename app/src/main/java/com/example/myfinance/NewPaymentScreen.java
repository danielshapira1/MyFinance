package com.example.myfinance;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.myfinance.databinding.ActivityLoginBinding;
import com.example.myfinance.databinding.ActivityNewPaymentBinding;
import com.example.myfinance.models.Payment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Calendar;

public class NewPaymentScreen extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String[] categories = {"food", "home", "shopping", "Other"};


    private ActivityNewPaymentBinding binding;
    EditText cost;
    EditText description;
    Spinner category;
    Button addbtn;
    DatePickerDialog picker;
    EditText date;
    DatabaseReference myRef;
    FirebaseAuth auth;
    String uid;
    String uEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewPaymentBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_new_payment);
        addbtn = (Button) findViewById(R.id.save);
        auth = FirebaseAuth.getInstance();

        cost = findViewById(R.id.costAmount);
        date = findViewById(R.id.text_date_display);
        description = findViewById(R.id.description);
        category = findViewById(R.id.category);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("payments").child("newPayment");
        uid = auth.getCurrentUser().getUid().toString();
        uEmail = auth.getCurrentUser().getEmail().toString();

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewPayment();

            }
        });
        Button button = (Button) findViewById(R.id.backToMainScreen);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(NewPaymentScreen.this, MainScreen.class);
                startActivity(intent);

            }
        });

        date.setInputType(InputType.TYPE_NULL);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(NewPaymentScreen.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, categories);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        category.setAdapter(aa);

    }

    private void createNewPayment() {
        String cCost = cost.getText().toString();
        String cDate = date.getText().toString();
        String cCategory = category.getSelectedItem().toString();
        String cDesc = description.getText().toString();
        String cUid = uid;
        String cEmail = uEmail;
        if (TextUtils.isEmpty(cCost)) {
            cost.setError("cost cannot be empty");

        } else if (TextUtils.isEmpty(cDate)) {
            date.setError("date cannot be empty");
        } else {
            Payment payment = new Payment(cCost, cDate, cCategory, cDesc, cUid, cEmail);
            myRef.child(uid).push().setValue(payment);
            Intent intent = new Intent(NewPaymentScreen.this, MainScreen.class);
            startActivity(intent);
        }


    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}