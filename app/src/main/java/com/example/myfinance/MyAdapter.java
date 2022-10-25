package com.example.myfinance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.myfinance.models.Payment;

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
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class PaymentViewholder extends RecyclerView.ViewHolder {
        TextView cost;
        TextView description;
        TextView date;
        TextView category;

        public PaymentViewholder(@NonNull View itemView) {
            super(itemView);

            cost = itemView.findViewById(R.id.costFiled);
            description = itemView.findViewById(R.id.descriptionFiled);
            date = itemView.findViewById(R.id.dateFiled);
            category = itemView.findViewById(R.id.categoryFiled);
        }
    }


}
