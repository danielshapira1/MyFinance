package com.example.myfinance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.example.myfinance.models.Payment;
import com.github.mikephil.charting.charts.BarChart;
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
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainScreen extends AppCompatActivity {
    ArrayList<BarEntry> barArraylist;
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
    TextView minTV;
    TextView maxTV;
    TextView textCategoryTV;
    TextView textMinTV;
    TextView textMaxTV;
    private TextView startDateTV;
    private TextView endDateTV;
    int totalHome;
    int totalShop;
    int totalFood;
    int totalOther;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        auth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("payments").child("newPayment");
        currentUserUid = auth.getCurrentUser().getUid().toString();
        View moveToPaymentScreen = findViewById(R.id.createNewPayment);
        View moveToMonthsScreen = findViewById(R.id.months);
        View moveToFilterScreen = findViewById(R.id.filters);
        LocalDate firstOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate lastOfMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        firstDay = formatter.format(firstOfMonth);
        lastDay = formatter.format(lastOfMonth);
        startDateTV = findViewById(R.id.startDateTV);
        endDateTV = findViewById(R.id.endDateTV);
        categoryTV = findViewById(R.id.categoryView);
        textCategoryTV = findViewById(R.id.selectedCategory);
        minTV = findViewById(R.id.minView);
        textMinTV = findViewById(R.id.minCost);
        maxTV = findViewById(R.id.maxView);
        textMaxTV = findViewById(R.id.maxCost);

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
                textCategoryTV.getText().toString().trim(),
                Integer.parseInt(textMinTV.getText().toString().trim()),
                Integer.parseInt(textMaxTV.getText().toString().trim()));

        moveToPaymentScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainScreen.this, NewPaymentScreen.class);
                startActivity(intent);

            }
        });

        moveToMonthsScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainScreen.this, MonthsActivity.class);
                startActivity(intent);

            }
        });

        moveToFilterScreen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainScreen.this, FilterActivity.class);
                startActivity(intent);
            }
        });

        barArraylist = new ArrayList<>();
        updateBarChart(totalHome, totalShop, totalFood, totalOther);

    }


    private void getDataByDate(String startDate, String endDate, String category, int min, int max) {

        String startDateFixed = Util.fixDateFormat(startDate);
        startDateFixed = Util.fixDateFormatToLexicographicOrder(startDateFixed);

        String endDateFixed = Util.fixDateFormat(endDate);
        endDateFixed = Util.fixDateFormatToLexicographicOrder(endDateFixed);

        dbRef.child(currentUserUid)
                .orderByChild("dateFormatted")
                .startAt(startDateFixed)
                .endAt(endDateFixed)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listPayments.clear();
                        totalFood = 0;
                        totalHome = 0;
                        totalOther = 0;
                        totalShop = 0;
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (Integer.parseInt((String) dataSnapshot.child("cost").getValue()) >
                                    Integer.parseInt(textMinTV.getText().toString()) &&
                                    Integer.parseInt((String) dataSnapshot.child("cost").getValue()) <
                                            Integer.parseInt(textMaxTV.getText().toString())) {

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
                                    if (dataSnapshot.child("category").getValue().toString().contains(category)) {

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
        textCategoryTV.setText(FilterActivity.getSearchByFilter().get(2));

        textMinTV.setText(FilterActivity.getSearchByFilter().get(3));
        textMaxTV.setText(FilterActivity.getSearchByFilter().get(4));

    }

    public void updateBarChart(int totalHome, int totalShop, int totalFood, int totalOther) {
        BarChart barChart = findViewById(R.id.chart);
        barChart.getDescription().setEnabled(false);

        HashMap<Integer, List<Integer>> paymentTotalMap = new HashMap<>();
        ArrayList<Integer> paymentListFood = new ArrayList<>();
        paymentListFood.add(totalFood);
        paymentTotalMap.put(0, paymentListFood);
        ArrayList<Integer> paymentListHome = new ArrayList<>();
        paymentListHome.add(totalHome);
        paymentTotalMap.put(1, paymentListHome);
        ArrayList<Integer> paymentListShop = new ArrayList<>();
        paymentListShop.add(totalShop);
        paymentTotalMap.put(2, paymentListShop);
        ArrayList<Integer> paymentListOther = new ArrayList<>();
        paymentListOther.add(totalOther);
        paymentTotalMap.put(3, paymentListOther);

        barArraylist = new ArrayList<>();
        for(Map.Entry<Integer, List<Integer>> entry:paymentTotalMap.entrySet()){
            float[] arr = new float[4];
            for (Integer p : entry.getValue()){
                switch (entry.getKey()) {
                    case 0:
                        arr[0]+=p;
                        break;
                    case 1:
                        arr[1]+=p;
                        break;
                    case 2:
                        arr[2]+=p;
                        break;
                    case 3:
                        arr[3]+=p;
                        break;
                }
            }
            barArraylist.add(new BarEntry(entry.getKey(),arr));
        }

        BarDataSet barDataSet = new BarDataSet(barArraylist, "");
//        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setColors(Util.getColors(this));
        barDataSet.setStackLabels(new String[]{"Food", "Home", "Shopping", "Other"});
        BarData data = new BarData(barDataSet);
//            data.setValueFormatter(new MyValueFormatter());
//            data.setValueTextColor(Color.BLACK);
        barChart.setData(data);
        barChart.getXAxis().setEnabled(false);
        barChart.getAxisLeft().setAxisMinimum(0);
        barChart.getAxisRight().setEnabled(false);
        barChart.setDrawValueAboveBar(true);
        barChart.getData().setValueTextSize(15);
        barChart.invalidate();
        barChart.animateY(600);
    }

    public void onBackPressed() {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                finishAffinity();
                System.exit(0);
            } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                dialog.cancel();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Exit app");
        builder.setMessage("Are you sure you what to exit?").
                setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener);

        AlertDialog dialog = builder.create();

        dialog.show();
    }
}