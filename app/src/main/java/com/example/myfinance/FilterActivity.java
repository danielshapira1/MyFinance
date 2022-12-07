package com.example.myfinance;

import static com.example.myfinance.MainScreen.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;

public class FilterActivity extends AppCompatActivity {
    static String[] categories = {"all categories", "food", "home", "shopping", "Other"};


    private static String firstDay;
    private static String lastDay;
    private TextView startDateTV;
    private static Spinner category;
    private static String startDateVal;
    private static String endDateVal;
    private String startDate;
    private String endDate;
    private TextView endDateTV;
    private static EditText min;
    private static EditText max;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);


        Button button = findViewById(R.id.back);
        Button save = findViewById(R.id.saveChange);
        Button searchDate = findViewById(R.id.searchDate);
        startDateTV = findViewById(R.id.startDateTV);
        endDateTV = findViewById(R.id.endDateTV);
        category = findViewById(R.id.category);
        min = findViewById(R.id.editTextNumberMin);
        max = findViewById(R.id.editTextNumberMax);

        LocalDate firstOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate lastOfMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        firstDay = formatter.format(firstOfMonth);
        lastDay = formatter.format(lastOfMonth);

        if (startDateTV.getText().toString().isEmpty() && startDateVal == null) {
            startDateTV.setText(firstDay);
        } else {
            startDateTV.setText(startDateVal);
        }
        if (endDateTV.getText().toString().isEmpty() && endDateVal == null) {
            endDateTV.setText(lastDay);
        } else {
            endDateTV.setText(endDateVal);
        }
        if (min.getText().toString().isEmpty()) {
            min.setText(String.valueOf(0));
        }  if (max.getText().toString().isEmpty()) {
            max.setText(String.valueOf(1000000));
        }
        searchDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();

                builder.setTitleText("select dates for search");

                MaterialDatePicker<Pair<Long, Long>> materialDatePicker = builder.build();

                materialDatePicker.show(getSupportFragmentManager(), "date picker");

                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {

                        startDate = String.valueOf(selection.first);
                        startDateVal = new SimpleDateFormat("dd/MM/yyyy").format(selection.first);
                        endDate = String.valueOf(selection.second);
                        endDateVal = new SimpleDateFormat("dd/MM/yyyy").format(selection.second);

                        startDateTV.setText(startDateVal);
                        endDateTV.setText(endDateVal);
                    }
                });
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FilterActivity.this, MainScreen.class);


                if (Integer.parseInt(min.getText().toString()) > Integer.parseInt(max.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "min cannot have larger value then max", Toast.LENGTH_SHORT).show();
                } else
                    startActivity(intent);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FilterActivity.this, MainScreen.class);

                if (min.getText().toString().isEmpty()) {
                    min.setText(0);
                } else if (max.getText().toString().isEmpty()) {
                    max.setText(Integer.MAX_VALUE);
                }

                if (Integer.parseInt(min.getText().toString()) > Integer.parseInt(max.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "min value can't be bigger then max", Toast.LENGTH_SHORT).show();
                } else
                    startActivity(intent);
            }
        });
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, categories);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(aa);

    }

    public static ArrayList<String> getSearchByFilter() {
        ArrayList<String> returnValues = new ArrayList<>();
        if (startDateVal == null)
            returnValues.add(firstDay);
        else
            returnValues.add(startDateVal);
        if (endDateVal == null)
            returnValues.add(lastDay);
        else
            returnValues.add(endDateVal);
        if (category == null)
            returnValues.add("all categories");
        else
            returnValues.add(category.getSelectedItem().toString());
        if (min == null) {
            returnValues.add("0");
        } else
            returnValues.add(min.getText().toString());
        if (max == null)
            returnValues.add(String.valueOf("1000000"));
        else
            returnValues.add(max.getText().toString());
        Log.d(TAG, "getSearchByFilter: " + returnValues);
        return returnValues;
    }


}