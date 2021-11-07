package com.example.bookapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.bookapp.R;
import com.example.bookapp.SessionManager;
import com.example.bookapp.buyer.BookDetailsActivity;
import com.example.bookapp.models.Book;
import com.example.bookapp.models.Order;

import java.util.List;

public class orderAdapter extends RecyclerView.Adapter<orderAdapter.MyViewHolder> {

    List<Order> list;
    Context context;
    SessionManager session;
    public orderAdapter(List<Order> list, Context context) {
        this.list = list;
        this.context = context;
        session = new SessionManager(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_order, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Order item = list.get(position);

        holder.visit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Book i = item.getBook();
                Intent intent = new Intent(context, BookDetailsActivity.class);
                intent.putExtra("Book",  i);
                intent.putExtra("orderBtn",  "hide");
                context.startActivity(intent);

            }
        });


        holder.order_date.setText(context.getString(R.string.order_date) + " " + item.getOrder_date());
        holder.order_status.setText(context.getString(R.string.order_status) + " " + item.getStatusText(context));
        holder.book_name.setText(item.getBook().getBookName() );
        holder.seller_name.setText(context.getString(R.string.seller_name) + " " + item.getSeller().getName());
        holder.seller_email.setText(context.getString(R.string.seller_email) + " " + item.getSeller().getEmail());
        holder.seller_phone.setText(context.getString(R.string.seller_address) + " " + item.getSeller().getAddress());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView book_name, seller_name, seller_phone, seller_email,order_status, order_date ;

        Button visit, cancelOrderBtn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            book_name = itemView.findViewById(R.id.book_name);
            seller_name = itemView.findViewById(R.id.bookOwner);
            seller_phone = itemView.findViewById(R.id.sellerPhone);
            seller_email = itemView.findViewById(R.id.sellerEmail);
            order_date = itemView.findViewById(R.id.order_date);
            order_status = itemView.findViewById(R.id.order_status);
            visit = itemView.findViewById(R.id.visitBtn);

        }
    }
}
