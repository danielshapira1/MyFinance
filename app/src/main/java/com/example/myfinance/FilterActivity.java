package com.example.myfinance;

import static com.example.myfinance.MainScreen.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FilterActivity extends AppCompatActivity {


    private static ArrayList<String> returnValues;
    static Date cDate = new Date();
    static String fDate = new SimpleDateFormat("yyyy/MM/dd").format(cDate);

    private String date;
    private String startDate;
    private TextView startDateTV;
    private static String startDateVal;
    private static String endDateVal;
    private String endDate;
    private TextView endDateTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);


        Button button = findViewById(R.id.back);
        Button searchDate = findViewById(R.id.searchDate);
        startDateTV = findViewById(R.id.startDateTV);
        endDateTV = findViewById(R.id.endDateTV);

        date = fDate;
        if (startDateTV.getText().toString().isEmpty() && startDateVal == null || endDateTV.getText().toString().isEmpty() && endDateVal == null) {
            endDateTV.setText(date);
            startDateTV.setText(date);
        } else {
            endDateTV.setText(endDateVal);
            startDateTV.setText(startDateVal);
        }
        searchDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();

                builder.setTitleText("select dates for search");

                MaterialDatePicker<Pair<Long, Long>> materialDatePicker = builder.build();

                searchDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        materialDatePicker.show(getSupportFragmentManager(), "date picker");
                    }
                });

                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {

                        startDate = String.valueOf(selection.first);
                        startDateVal = new SimpleDateFormat("yyyy/MM/dd").format(selection.first);
                        endDate = String.valueOf(selection.second);
                        endDateVal = new SimpleDateFormat("yyyy/MM/dd").format(selection.second);

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
                startActivity(intent);

            }
        });


    }

    public static ArrayList<String> getSearchByFilter() {
        returnValues = new ArrayList<>();
        if (startDateVal == null)
            returnValues.add(fDate);
        else
            returnValues.add(startDateVal);
        if (endDateVal == null)
            returnValues.add(fDate);
        else
            returnValues.add(endDateVal);


        Log.d(TAG, "getSearchByFilter: " + returnValues);
        return returnValues;
    }


}