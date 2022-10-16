package com.example.myfinance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class MainScreen extends AppCompatActivity {
    ArrayList barArraylist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        Button moveToPaymentScreen = (Button) findViewById(R.id.createNew);
        Button moveToCalenderScreen = (Button) findViewById(R.id.calender);
        getData();
        BarChart barChart = findViewById(R.id.chart);
        BarDataSet barDataSet = new BarDataSet(barArraylist, "my spendings");
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setValueTextColor(Color.RED);
        barChart.getDescription().setEnabled(true);


        moveToPaymentScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainScreen.this, NewPaymentScreen.class);
                startActivity(intent);

            }
        });

        moveToCalenderScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainScreen.this, CalendarActivity.class);
                startActivity(intent);

            }
        });

    }

    private void getData() {
        barArraylist = new ArrayList<>();
        barArraylist.add(new BarEntry(2f, 10));
        barArraylist.add(new BarEntry(3f, 20));
        barArraylist.add(new BarEntry(4f, 30));
        barArraylist.add(new BarEntry(5f, 40));
    };
}