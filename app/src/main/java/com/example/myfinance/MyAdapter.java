package com.example.myfinance;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
        holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(context, "hello", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
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

        public PaymentViewholder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.singel_ayment_layout);
            cost = itemView.findViewById(R.id.costFiled);
            description = itemView.findViewById(R.id.descriptionFiled);
            date = itemView.findViewById(R.id.dateFiled);
            category = itemView.findViewById(R.id.categoryFiled);
        }
    }

    deleteUserDtn.setOnClickListener(v -> {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            if (which == DialogInterface.BUTTON_POSITIVE)
                ServerRequestsService.getInstance().deleteUser(()-> requireActivity().runOnUiThread(()->
                        Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_loginFragment)
                ));
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.AlertDialog);

        builder.setMessage("Are you sure you what to delete your user?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.color.background);
        dialog.show();
    });


}
