package com.example.myfinance;

import static com.example.myfinance.MainScreen.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CalendarActivity extends AppCompatActivity {


    private CalendarView mCalendarView;
    private static String selectedDate;
    static Date cDate = new Date();
    static String fDate = new SimpleDateFormat("dd/MM/yyyy").format(cDate);
    static TextView dateTextBox ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        dateTextBox =(TextView) findViewById(R.id.datePicked);
        dateTextBox.setText(fDate);
        Button button = (Button) findViewById(R.id.back);
        Button saveDate = findViewById(R.id.saveDate);
        saveDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int monthOfYear, int dayOfMonth) {
                        selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                    }
                });
                dateTextBox.setText(selectedDate);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CalendarActivity.this, MainScreen.class);
                startActivity(intent);

            }
        });

        mCalendarView = (CalendarView) findViewById(R.id.calendarView);
        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int monthOfYear, int dayOfMonth) {
                selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
            }
        });

    }

    public static String getSelectedDate() {
        if (selectedDate != fDate){
        Log.d(TAG, "getSelectedDate: "+ selectedDate);
        return selectedDate;

        }
        else return fDate;
    }


}