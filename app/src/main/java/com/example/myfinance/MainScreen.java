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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class MainScreen extends AppCompatActivity {
    ArrayList barArraylist;
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    final static String TAG = "My finance";
    DatabaseReference dbRef;
    FirebaseAuth auth;
    ArrayList<Payment> listPayments;
    String currentUserUid;
    String firstDay;
    String lastDay;

    TextView categoryTV;
    private TextView startDateTV;
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
        dbRef = database.getReference("payments").child("newPayment");
        currentUserUid = auth.getCurrentUser().getUid().toString();
        Button moveToPaymentScreen = (Button) findViewById(R.id.createNewPayment);
        Button moveToCalenderScreen = (Button) findViewById(R.id.calender);
        LocalDate firstOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate lastOfMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        firstDay = formatter.format(firstOfMonth);
        lastDay = formatter.format(lastOfMonth);
        startDateTV = findViewById(R.id.startDateTV);
        endDateTV = findViewById(R.id.endDateTV);
        categoryTV = findViewById(R.id.categoryView);

        if (startDateTV.getText().toString().isEmpty()) {
            startDateTV.setText(firstDay);
        }
        if (endDateTV.getText().toString().isEmpty()) {
            endDateTV.setText(lastDay);
        }
        getUpdates();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listPayments = new ArrayList<>();
        myAdapter = new MyAdapter(this, listPayments);
        recyclerView.setAdapter(myAdapter);
//        getDataByCategory();

        getDataByDate(startDateTV.getText().toString().trim(),
                endDateTV.getText().toString().trim(),
                categoryTV.getText().toString().trim());

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
                Intent intent = new Intent(MainScreen.this, FilterActivity.class);
                startActivity(intent);

            }
        });

        barArraylist = new ArrayList<>();
        updateBarChart(totalHome, totalShop, totalFood, totalOther);

    }


    private void getDataByDate(String startDate, String endDate, String category) {

        String startDateFixed = Util.fixDateFormat(startDate);
        startDateFixed = Util.fixDateFormatTolexicographicOrder(startDateFixed);

        String endDateFixed = Util.fixDateFormat(endDate);
        endDateFixed = Util.fixDateFormatTolexicographicOrder(endDateFixed);

        String finalCategory = category;
        dbRef.child(currentUserUid)
                .orderByChild("dateFormatted")
                .startAt(startDateFixed)
                .endAt(endDateFixed)

                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listPayments.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Log.d(TAG, "onDataChange: " + dataSnapshot.child("category").getValue());
                            if (category.equals("all categories")) {
                                Payment payment = dataSnapshot.getValue(Payment.class);
                                if (payment == null) {
                                    return;
                                }
                                payment.setId(dataSnapshot.getKey());

                                listPayments.add(payment);
                                String currentCategory =
                                        dataSnapshot.child("category").getValue() != null ?
                                                dataSnapshot.child("category").getValue().toString() :
                                                "";

                                String costStr = dataSnapshot.child("cost").getValue().toString();
                                int cost = Integer.parseInt(costStr);

                                switch (currentCategory) {
                                    case "food":
                                        totalFood += cost;
                                        break;
                                    case "home":
                                        totalHome += cost;
                                        break;
                                    case "shopping":
                                        totalShop += cost;
                                        break;
                                    case "other":
                                        totalOther += cost;
                                        break;
                                }
                            } else {
                                if (dataSnapshot.child("category").getValue().toString().contains(finalCategory)) {

                                    Payment payment = dataSnapshot.getValue(Payment.class);
                                    if (payment == null) {
                                        return;
                                    }
                                    payment.setId(dataSnapshot.getKey());

                                    listPayments.add(payment);
                                    String currentCategory =
                                            dataSnapshot.child("category").getValue() != null ?
                                                    dataSnapshot.child("category").getValue().toString() :
                                                    "";

                                    String costStr = dataSnapshot.child("cost").getValue().toString();
                                    int cost = Integer.parseInt(costStr);

                                    switch (currentCategory) {
                                        case "food":
                                            totalFood += cost;
                                            break;
                                        case "home":
                                            totalHome += cost;
                                            break;
                                        case "shopping":
                                            totalShop += cost;
                                            break;
                                        case "other":
                                            totalOther += cost;
                                            break;
                                    }
                                }
                            }

                            updateBarChart(totalHome, totalShop, totalFood, totalOther);
                            myAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void getDataByCategory() {
//        if (Objects.equals(cat, "all categories"))

        dbRef.child(currentUserUid).orderByChild("category").startAt("shopping").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Payment payment = dataSnapshot.getValue(Payment.class);
                    listPayments.add(payment);
                    String currentCategory = dataSnapshot.child("category").getValue().toString();

                    String costStr = dataSnapshot.child("cost").getValue().toString();
                    int cost = Integer.parseInt(costStr);


                    switch (currentCategory) {
                        case "food":
                            totalFood += cost;
                            break;
                        case "home":
                            totalHome += cost;
                            break;
                        case "shopping":
                            totalShop += cost;
                            break;
                        case "other":
                            totalOther += cost;
                            break;
                    }
                }

                updateBarChart(totalHome, totalShop, totalFood, totalOther);
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


        if (!Objects.equals(newStart, firstDay) && newStart != null) {
            startDateTV.setText(newStart);
        }
        if (!Objects.equals(newEnd, lastDay) && newEnd != null) {
            endDateTV.setText(newEnd);
        }
        categoryTV.setText(FilterActivity.getSearchByFilter().get(2));

    }


    public void updateBarChart(int totalHome, int totalShop, int totalFood, int totalOther) {
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
}