package com.example.compaq.myapplication;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import com.example.compaq.myapplication.R;
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{

    ArrayList<Transaction> transactions;
    public RecyclerAdapter(ArrayList<Transaction> transactions)
    {
        this.transactions=transactions;
    }

    public RecyclerAdapter(FragmentActivity activity) {
    }


    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view, parent,false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;


    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.place.setText(transactions.get(position).place);
        holder.amount.setText(String.valueOf(transactions.get(position).amount));
        holder.date.setText(transactions.get(position).date);

    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        protected TextView place,amount,date,type;
        protected LinearLayout linearLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            place =  (TextView) itemView.findViewById(R.id.place);
            amount=(TextView) itemView.findViewById(R.id.amount);
            date=(TextView)itemView.findViewById(R.id.date);

        }


    }
}
