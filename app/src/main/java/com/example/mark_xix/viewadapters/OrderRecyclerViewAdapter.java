package com.example.mark_xix.viewadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mark_xix.R;
import com.example.mark_xix.models.Medicine;
import com.example.mark_xix.models.OrderHistory;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class OrderRecyclerViewAdapter extends RecyclerView.Adapter<OrderRecyclerViewAdapter.OrderRecyclerViewHolder> {

    private Context context;
    private List<OrderHistory> orderHistoryList;

    private final FirebaseStorage storage = FirebaseStorage.getInstance();


    public OrderRecyclerViewAdapter(Context context, List<OrderHistory> orderHistoryList) {
        this.context = context;
        this.orderHistoryList = orderHistoryList;
    }


    @NonNull
    @Override
    public OrderRecyclerViewAdapter.OrderRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_order_history,parent,false);

        return new OrderRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderRecyclerViewAdapter.OrderRecyclerViewHolder holder, int position) {
            final OrderHistory orderHistory=orderHistoryList.get(position);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MMM-dd HH:mm", Locale.ENGLISH);
        String date = simpleDateFormat.format(orderHistory.getTimestamp());

        holder.textViewDate.setText(date);

        holder.textViewTotal.setText(String.valueOf(orderHistory.getTotal()));

        holder.linearLayout.removeAllViews();

        for (Medicine medicine:orderHistory.getMedicineList()){
            View viewPopupItem = LayoutInflater.from(context).inflate(R.layout.layout_order_popup_item, null);

            ImageView imageView = viewPopupItem.findViewById(R.id.imgThumb_medicine);
            TextView textViewName = viewPopupItem.findViewById(R.id.txt_name);
            TextView textViewPrice = viewPopupItem.findViewById(R.id.txt_price);

            Glide.with(context)
                    .load(storage.getReferenceFromUrl(medicine.getImage_link()))
                    .transition(withCrossFade())
                    .fitCenter()
                    .error(R.drawable.error_loading)
                    .fallback(R.drawable.error_loading)
                    .into(imageView);

            textViewName.setText(medicine.getName());
            textViewPrice.setText(String.valueOf(medicine.getPrice()) + " LKR");

            holder.linearLayout.addView(viewPopupItem);
        }
    }

    @Override
    public int getItemCount() {
        return orderHistoryList.size();
    }

    public class OrderRecyclerViewHolder extends RecyclerView.ViewHolder{

        TextView textViewTotal;
        TextView textViewDate;
        LinearLayout linearLayout;

        public OrderRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewTotal=itemView.findViewById(R.id.txt_total_order_history);
            textViewDate=itemView.findViewById(R.id.txt_date_order_history);
            linearLayout=itemView.findViewById(R.id.linear_layout);
        }
    }
}
