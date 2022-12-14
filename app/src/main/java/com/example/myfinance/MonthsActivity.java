package com.example.myfinance;

import static com.example.myfinance.MainScreen.TAG;
import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.myfinance.models.Payment;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MonthsActivity extends AppCompatActivity {
    private static enum DisplayData {
        FOOD,
        HOME,
        SHOPPING,
        OTHER,
        ALL,
        SUM
    }

    ArrayList barArraylist;
    DatabaseReference dbRef;
    FirebaseAuth auth;
    String currentUserUid;
    DisplayData displayData;
    String startDateFixed;
    String endDateFixed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_months);
        View back = findViewById(R.id.backToMain);
        auth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("payments").child("newPayment");
        currentUserUid = auth.getCurrentUser().getUid().toString();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MonthsActivity.this, MainScreen.class);
                startActivity(intent);
            }
        });
        LocalDate dateOne = LocalDate.now();
        String firstDay = dateOne.with(firstDayOfMonth()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")); // 2015-01-01
        String lastDay = dateOne.with(lastDayOfMonth()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        LocalDate dateTwo = dateOne.minusMonths(1);
        String firstDayMonthAgo = dateTwo.with(firstDayOfMonth()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String lastDayMonthAgo = dateTwo.with(lastDayOfMonth()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        Log.d(TAG, "onCreate: " + firstDay);
        Log.d(TAG, "onCreate: " + lastDay);
        startDateFixed = Util.fixDateFormatToLexicographicOrder(firstDay);
        endDateFixed = Util.fixDateFormatToLexicographicOrder(lastDay);

//        dbRef.child(currentUserUid)
//                .orderByChild("dateFormatted")
//                .startAt(startDateFixed)
//                .endAt(endDateFixed)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        listPayments.clear();
//                        totalFood = 0;
//                        totalHome = 0;
//                        totalOther = 0;
//                        totalShop = 0;
//                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                            if (Integer.parseInt((String) dataSnapshot.child("cost").getValue()) >
//                                    Integer.parseInt(textMinTV.getText().toString()) &&
//                                    Integer.parseInt((String) dataSnapshot.child("cost").getValue()) <
//                                            Integer.parseInt(textMaxTV.getText().toString())) {
//
//                                if (category.equals("all categories")) {
//                                    Payment payment = dataSnapshot.getValue(Payment.class);
//                                    if (payment == null) {
//                                        return;
//                                    }
//                                    payment.setId(dataSnapshot.getKey());
//
//                                    listPayments.add(payment);
//                                    String currentCategory =
//                                            dataSnapshot.child("category").getValue() != null ?
//                                                    dataSnapshot.child("category").getValue().toString() :
//                                                    "";
//
//                                    String costStr = dataSnapshot.child("cost").getValue().toString();
//                                    int cost = Integer.parseInt(costStr);
//
//                                    switch (currentCategory) {
//                                        case "food":
//                                            totalFood += cost;
//                                            break;
//                                        case "home":
//                                            totalHome += cost;
//                                            break;
//                                        case "shopping":
//                                            totalShop += cost;
//                                            break;
//                                        case "other":
//                                            totalOther += cost;
//                                            break;
//                                    }
//                                }
//                            }
//                        }
//
//                        updateBarChart(totalHome, totalShop, totalFood, totalOther);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });


    }

//    public void fetchData(){
//        dbRef.child(currentUserUid)
//                .orderByChild("dateFormatted")
//                .startAt(firstDay)
//                .endAt(lastDay)
//                .addValueEventListener
//    }

    public void updateBarChart(int totalHome, int totalShop, int totalFood, int totalOther) {
        barArraylist = new ArrayList<>();
        barArraylist.add(new BarEntry(1, totalFood));
        barArraylist.add(new BarEntry(2, totalHome));
        barArraylist.add(new BarEntry(3, totalShop));
        barArraylist.add(new BarEntry(4, totalOther));
        BarChart barChart = findViewById(R.id.chart);
        BarDataSet barDataSet = new BarDataSet(barArraylist, "My spending's");
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barDataSet.setColors(gColor(R.color.yellow),
                gColor(R.color.DeepSkyBlue),
                gColor(R.color.Violet),
                gColor(R.color.PaleGreen));
        barDataSet.setValueTextColor(Color.BLACK);
        barChart.getDescription().setEnabled(false);
        XAxis xAxisRight = barChart.getXAxis();
        xAxisRight.setEnabled(false);
        barChart.invalidate();
    }

    // This function is for readable purpose to prevent DRY code
    private int gColor(int color) {
        return ContextCompat.getColor(this, color);
    }

    private void getPayment(Map<Integer, List<Payment>> paymentsMap) {
        LocalDate now = LocalDate.now();
        for (int i = 0; i < 6; i++) {
            paymentsMap.put(now.getMonth().getValue(), new ArrayList<>());
            now = now.minusMonths(1);
        }
        dbRef.child(currentUserUid)
                .orderByChild("dateFormatted")
                .startAt(startDateFixed)
                .endAt(endDateFixed)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        paymentsMap.forEach((m, payments) -> payments.clear());
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Payment payment = dataSnapshot.getValue(Payment.class);
                            if (payment == null) {
                                return;
                            }
                            payment.setId(dataSnapshot.getKey());
                            int paymentMonth = 0;
                            // לבדוק את החודש של ההוצאה ולהוסיף אותה לרשימה המתאימה
                            Objects.requireNonNull(paymentsMap.get(paymentMonth)).add(payment);
                            String currentCategory =
                                    dataSnapshot.child("category").getValue() != null ?
                                            dataSnapshot.child("category").getValue().toString() :
                                            "";

                            String costStr = dataSnapshot.child("cost").getValue().toString();
                            int cost = Integer.parseInt(costStr);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}