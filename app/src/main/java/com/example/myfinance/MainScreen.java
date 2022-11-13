package com.example.myfinance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.example.myfinance.databinding.ActivityMainScreenBinding;
import com.example.myfinance.models.Payment;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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
    DatabaseReference dbRef;
    FirebaseAuth auth;
    ArrayList<Payment> listPayments;
    String currentUserUid;
    String currentDateString;
    String dateString;
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
        Date currentDate = new Date();
        currentDateString = new SimpleDateFormat("dd/MM/yyyy").format(currentDate);
        startDateTV = findViewById(R.id.startDateTV);
        endDateTV = findViewById(R.id.endDateTV);
        categoryTV = findViewById(R.id.categoryView);

        //date = currentDateString;
        Log.d(TAG, "onCreate: " + currentDateString);
        //getUpdatedDate();
        Log.d(TAG, "onUpdate: " + currentDateString);
        dateString = currentDateString;
        if (startDateTV.getText().toString().isEmpty()) {
            startDateTV.setText(currentDateString);
        }
        if (endDateTV.getText().toString().isEmpty()){
            endDateTV.setText(currentDateString);
        }
        getUpdates();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listPayments = new ArrayList<>();
        myAdapter = new MyAdapter(this, listPayments);
        recyclerView.setAdapter(myAdapter);

        getDataByDate(startDateTV.getText().toString().trim(), endDateTV.getText().toString().trim());

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



    private void getDataByDate(String startDate, String endDate) {
        Log.d(TAG, "$$ dates: " + startDate + " - " + endDate);
        dbRef.child(currentUserUid)
            .orderByChild("date")
            .startAt(startDate)
            .endAt(endDate)
            .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listPayments.clear();
//                int paymentIndex = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Payment payment = dataSnapshot.getValue(Payment.class);
                    if (payment==null) {
                        return;
                    }
                    payment.setId(dataSnapshot.getKey());
//                    Log.d(TAG, "$$ Payment " + paymentIndex + " from firebase:\n" + payment);
//                    paymentIndex++;
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
               
                updateBarChart(totalHome, totalShop, totalFood, totalOther);
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        
    }

    private void getDataByDate(String startDate, long endDate) {
       dbRef.child(currentUserUid).orderByKey().startAfter(startDate).addValueEventListener(new ValueEventListener() {
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

    private void getDataByCategory(long startDate, long endDate) {
        dbRef.child(currentUserUid).orderByChild("category").equalTo("food").addValueEventListener(new ValueEventListener() {
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



        if (!Objects.equals(newStart, dateString) && newStart != null) {
            startDateTV.setText(newStart);
        }
        if (!Objects.equals(newEnd, dateString) && newEnd != null){
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