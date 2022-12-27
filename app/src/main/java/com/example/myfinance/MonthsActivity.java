package com.example.myfinance;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.myfinance.models.Payment;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonthsActivity extends AppCompatActivity {
    private static enum DisplayData {
        FOOD,
        HOME,
        SHOPPING,
        OTHER,
        ALL,
        SUM
    }

    int sumTotal;
    int maxMonth;
    String maxMonthName;
    int currentMonth;
    int maxCategory;
    String maxCategoryName;
    int countFood;
    int countHome;
    int countShopping;
    int countOther;

    DatabaseReference dbRef;
    FirebaseAuth auth;
    String currentUserUid;

    BarChart mChart1;
    Map<Integer, List<Payment>> paymentPerMonthMap;

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

        paymentPerMonthMap = new HashMap<>();
        getPayment(paymentPerMonthMap);

    }

    public void updateBarChart(){
        mChart1 = findViewById(R.id.chartOfMonths);

        mChart1.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart1.setMaxVisibleValueCount(40);

        // scaling can now only be done on x- and y-axis separately
        mChart1.setPinchZoom(false);

        mChart1.setDrawGridBackground(false);
        mChart1.setDrawBarShadow(false);

        mChart1.setDrawValueAboveBar(false);
        mChart1.setHighlightFullBarEnabled(false);

        // change the position of the y-labels
        YAxis leftAxis = mChart1.getAxisLeft();
//        leftAxis.setValueFormatter(new MyAxisValueFormatter());
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        mChart1.getAxisRight().setEnabled(false);

        XAxis xLabels = mChart1.getXAxis();
        xLabels.setPosition(XAxis.XAxisPosition.TOP);

        // setting data;

        Legend l = mChart1.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setFormSize(8f);
        l.setFormToTextSpace(4f);
        l.setXEntrySpace(6f);


        ArrayList<BarEntry> yVals1 = new ArrayList<>();
        for(Map.Entry<Integer,List<Payment>> entry:paymentPerMonthMap.entrySet()){
            float[] arr = new float[4];
            for (Payment p : entry.getValue()){
                switch (p.getCategory()) {
                    case "food":
                        arr[0]+=Float.parseFloat(p.getCost());
                        break;
                    case "home":
                        arr[1]+=Float.parseFloat(p.getCost());
                        break;
                    case "shopping":
                        arr[2]+=Float.parseFloat(p.getCost());
                        break;
                    case "other":
                        arr[3]+=Float.parseFloat(p.getCost());
                        break;
                }
            }
            yVals1.add(new BarEntry(entry.getKey(),arr));
        }

        BarDataSet set1;

        if (mChart1.getData() != null &&
                mChart1.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mChart1.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            mChart1.getData().notifyDataChanged();
            mChart1.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1,"");
            set1.setDrawIcons(false);
            set1.setColors(gColor(R.color.yellow),
                    gColor(R.color.DeepSkyBlue),
                    gColor(R.color.Violet),
                    gColor(R.color.PaleGreen));

            set1.setStackLabels(new String[]{"Food", "Home", "Shopping","Other"});

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
//            data.setValueFormatter(new MyValueFormatter());
//            data.setValueTextColor(Color.BLACK);

            mChart1.setData(data);
        }

        mChart1.setFitBars(true);
        mChart1.invalidate();
    }

    private int gColor(int color) { return ContextCompat.getColor(this, color); }

    private void getPayment(Map<Integer, List<Payment>> paymentsMap) {
        sumTotal = 0;
        maxMonth = 0;
        maxCategory = 0;
        countFood = 0;
        countHome = 0;
        countShopping = 0;
        countOther = 0;
        LocalDate now = LocalDate.now();
        for (int i = 0; i < 6; i++) {
            String firstDay = now.with(firstDayOfMonth()).format(DateTimeFormatter.ofPattern("yyyy/MM/dd")); // 2015-01-01
            String lastDay = now.with(lastDayOfMonth()).format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            ArrayList<Payment> payments = new ArrayList<>();
            paymentsMap.put(now.getMonth().getValue(), payments);
            LocalDate finalNow = now;
            dbRef.child(currentUserUid)
                    .orderByChild("dateFormatted")
                    .startAt(firstDay)
                    .endAt(lastDay)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            currentMonth = 0;
                            payments.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Payment payment = dataSnapshot.getValue(Payment.class);
                                if (payment == null) {
                                    return;
                                }
                                payment.setId(dataSnapshot.getKey());
                                sumTotal += Integer.parseInt(payment.getCost());
                                currentMonth += Integer.parseInt(payment.getCost());
                                if (maxMonth < currentMonth){
                                    maxMonth = currentMonth;
                                    maxMonthName = finalNow.getMonth().toString();
                                    maxMonthName = maxMonthName.charAt(0) +
                                            maxMonthName.substring(1).toLowerCase();
                                }
                                switch (payment.getCategory()) {
                                    case "food":
                                        countFood += Integer.parseInt(payment.getCost());
                                        break;
                                    case "home":
                                        countHome += Integer.parseInt(payment.getCost());
                                        break;
                                    case "shopping":
                                        countShopping += Integer.parseInt(payment.getCost());
                                        break;
                                    case "other":
                                        countOther += Integer.parseInt(payment.getCost());
                                        break;
                                }
                                payments.add(payment);
                            }
                            updateBarChart();
                            updateStats();
                        }

                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
            now = now.minusMonths(1);
        }
    }

    public void updateStats(){
        TextView sumTotalView = findViewById(R.id.sumTotal);
        sumTotalView.setText("* Total spending of 6 last months: " + sumTotal);
        TextView maxMonthView = findViewById(R.id.maxMonth);
        maxMonthView.setText("* The month with the most spending: " + maxMonthName + "\n" +
                "Which amounts to: " + maxMonth);
        maxCategory = Math.max(Math.max(countFood, countHome), Math.max(countShopping, countOther));
        if (countFood > countHome && countFood > countShopping && countFood > countOther)
            maxCategoryName = "Food";
        else if (countHome > countShopping && countHome > countOther)
            maxCategoryName = "Home";
        else if (countShopping > countOther)
            maxCategoryName = "Shopping";
        else
            maxCategoryName = "Other";
        TextView maxCategoryView = findViewById(R.id.maxCategory);
        maxCategoryView.setText("* Most spent category: " + maxCategoryName + "\n" +
                "Which amounts to: " + maxCategory);
    }
}