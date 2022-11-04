package com.example.myfinance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.myfinance.models.Payment;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class MainScreen extends AppCompatActivity {
    ArrayList barArraylist;
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    final static String TAG = "My finance";
    private DatabaseReference myRef;
    private FirebaseAuth auth;
    private ArrayList<Payment> list;
    private String uid;
    private String date;
    private String startDate;
    private TextView startDateTV;
    private String endDate;
    private TextView endDateTV;
    int totalHome = 0;
    int totalShop = 0;
    int totalFood = 0;
    int totalOther = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        auth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("payments").child("newPayment");
        uid = auth.getCurrentUser().getUid().toString();
        Button moveToPaymentScreen = findViewById(R.id.createNewPayment);
        Button searchDate = findViewById(R.id.calender);
        startDateTV = findViewById(R.id.startDateTV);
        endDateTV = findViewById(R.id.endDateTV);


        Date cDate = new Date();
        String fDate = new SimpleDateFormat("dd/MM/yyyy").format(cDate);

        date = fDate;
        if (startDateTV.getText().toString().isEmpty() || endDateTV.getText().toString().isEmpty()) {
            startDateTV.setText(date);
            endDateTV.setText(date);

        }
        getUpdates();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        myAdapter = new MyAdapter(this, list);
        recyclerView.setAdapter(myAdapter);

        getDataByDate(startDateTV.getText().toString().trim(), endDateTV.getText().toString().trim());


        moveToPaymentScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainScreen.this, NewPaymentScreen.class);
                startActivity(intent);

            }
        });

        searchDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainScreen.this, FilterActivity.class);
                startActivity(intent);

            }
        });


        barArraylist = new ArrayList<>();
        barArraylist.add(new BarEntry(1, totalFood));
        barArraylist.add(new BarEntry(2, totalHome));
        barArraylist.add(new BarEntry(3, totalShop));
        barArraylist.add(new BarEntry(4, totalOther));
        BarChart barChart = findViewById(R.id.chart);
        BarDataSet barDataSet = new BarDataSet(barArraylist, "my spendings");
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        XAxis xAxisRight = barChart.getXAxis();
        xAxisRight.setEnabled(false);

    }

    private void filterData(int min, int max){
        myRef.child(uid).orderByChild("cost").startAt(min).endAt(max).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Payment payment = dataSnapshot.getValue(Payment.class);
                    list.add(payment);
                    String curr = dataSnapshot.child("category").getValue().toString();

                    if (curr.equals("food")) {

                        String toNum = dataSnapshot.child("cost").getValue().toString();
                        int parse = Integer.parseInt(toNum);
                        totalFood += parse;
                    }

                    if (curr.equals("home")) {

                        String toNum = dataSnapshot.child("cost").getValue().toString();
                        int parse = Integer.parseInt(toNum);
                        totalHome += parse;
                    }

                    if (curr.equals("shopping")) {

                        String toNum = dataSnapshot.child("cost").getValue().toString();
                        int parse = Integer.parseInt(toNum);
                        totalShop += parse;
                    }

                    if (curr.equals("other")) {

                        String toNum = dataSnapshot.child("cost").getValue().toString();
                        int parse = Integer.parseInt(toNum);
                        totalOther += parse;
                    }
                }
                Log.d(TAG, "onDataChange: " + totalFood);
                barArraylist = new ArrayList<>();
                barArraylist.add(new BarEntry(1, totalFood));
                barArraylist.add(new BarEntry(2, totalHome));
                barArraylist.add(new BarEntry(3, totalShop));
                barArraylist.add(new BarEntry(4, totalOther));
                BarChart barChart = findViewById(R.id.chart);
                BarDataSet barDataSet = new BarDataSet(barArraylist, "my spendings");
                BarData barData = new BarData(barDataSet);
                barChart.setData(barData);
                barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                barDataSet.setValueTextColor(Color.BLACK);
                XAxis xAxisRight = barChart.getXAxis();
                xAxisRight.setEnabled(false);
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getDataByDate(String startDate, String endDate) {
        myRef.child(uid).orderByChild("date").startAfter(startDate).endBefore(endDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Payment payment = dataSnapshot.getValue(Payment.class);
                    list.add(payment);
                    String curr = dataSnapshot.child("category").getValue().toString();

                    if (curr.equals("food")) {

                        String toNum = dataSnapshot.child("cost").getValue().toString();
                        int parse = Integer.parseInt(toNum);
                        totalFood += parse;
                    }

                    if (curr.equals("home")) {

                        String toNum = dataSnapshot.child("cost").getValue().toString();
                        int parse = Integer.parseInt(toNum);
                        totalHome += parse;
                    }

                    if (curr.equals("shopping")) {

                        String toNum = dataSnapshot.child("cost").getValue().toString();
                        int parse = Integer.parseInt(toNum);
                        totalShop += parse;
                    }

                    if (curr.equals("other")) {

                        String toNum = dataSnapshot.child("cost").getValue().toString();
                        int parse = Integer.parseInt(toNum);
                        totalOther += parse;
                    }
                }
                Log.d(TAG, "onDataChange: " + totalFood);
                barArraylist = new ArrayList<>();
                barArraylist.add(new BarEntry(1, totalFood));
                barArraylist.add(new BarEntry(2, totalHome));
                barArraylist.add(new BarEntry(3, totalShop));
                barArraylist.add(new BarEntry(4, totalOther));
                BarChart barChart = findViewById(R.id.chart);
                BarDataSet barDataSet = new BarDataSet(barArraylist, "my spendings");
                BarData barData = new BarData(barDataSet);
                barChart.setData(barData);
                barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                barDataSet.setValueTextColor(Color.BLACK);
                XAxis xAxisRight = barChart.getXAxis();
                xAxisRight.setEnabled(false);
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void getUpdates() {
        String newStart = FilterActivity.getSearchByFilter().get(0);
        String newEnd = FilterActivity.getSearchByFilter().get(1);

        if (!Objects.equals(newStart, date) && newStart != null && !Objects.equals(newEnd, date) && newEnd != null) {

            startDateTV.setText(newStart);
            endDateTV.setText(newEnd);
        }
    }



}