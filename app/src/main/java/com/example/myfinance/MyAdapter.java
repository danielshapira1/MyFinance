package com.example.myfinance;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.myfinance.models.Payment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.PaymentViewholder> {

    Context context;
    ArrayList<Payment> list;

    public MyAdapter(Context context, ArrayList<Payment> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public PaymentViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.payment_row, parent, false);
        return new PaymentViewholder(view);
    }




    @Override
    public void onBindViewHolder(@NonNull PaymentViewholder holder, int position) {
        Payment payment = list.get(position);
        holder.cost.setText(payment.getCost());
        holder.description.setText(payment.getDescription());
        holder.date.setText(payment.getDate());
        holder.category.setText(payment.getCategory());
        holder.tableRow.setBackgroundResource(getBackgroundColor(payment.getCategory()));
        holder.deleteButton.setOnClickListener(view -> {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {

                if (which == DialogInterface.BUTTON_POSITIVE) {

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    database.getReference("payments/newPayment/" + payment.getUid() + "/" + payment.getId())
                            .removeValue();
                }
                else if (which == DialogInterface.BUTTON_NEGATIVE) {
                    dialog.cancel();
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setTitle("Checking");
            builder.setMessage("Are you sure you what to delete this payment?").
                    setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener);

            AlertDialog dialog = builder.create();

            dialog.show();
        });
    }

    private int getBackgroundColor(String category){
        switch (category){
            case "food":
                return R.color.yellow;
            case "home":
                return R.color.DeepSkyBlue;
            case "shopping":
                return R.color.Violet;
            case "other":
                return R.color.PaleGreen;
        }
        return R.color.white;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class PaymentViewholder extends RecyclerView.ViewHolder {
        View layout;
        TextView cost;
        TextView description;
        TextView date;
        TextView category;
        FloatingActionButton deleteButton;
        TableRow tableRow;

        public PaymentViewholder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.singel_ayment_layout);
            cost = itemView.findViewById(R.id.costFiled);
            description = itemView.findViewById(R.id.descriptionFiled);
            date = itemView.findViewById(R.id.dateFiled);
            category = itemView.findViewById(R.id.categoryFiled);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            tableRow = itemView.findViewById(R.id.tableRow);
        }
    }
}
