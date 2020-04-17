package com.example.mark_xix.viewadapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mark_xix.R;
import com.example.mark_xix.models.Medicine;
import com.example.mark_xix.models.MedicineSelecter;
import com.google.firebase.storage.FirebaseStorage;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.HomeRecyclerViewHolder> {

    private Context context;
    private List<MedicineSelecter> medicineSelecterList;

    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    public HomeRecyclerViewAdapter(Context context, List<MedicineSelecter> medicineSelecterList) {
        this.context = context;
        this.medicineSelecterList = medicineSelecterList;
    }


    @NonNull
    @Override
    public HomeRecyclerViewAdapter.HomeRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_medicine_item, parent, false);
        return new HomeRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeRecyclerViewAdapter.HomeRecyclerViewHolder holder, final int position) {
        final MedicineSelecter medicineSelecter = medicineSelecterList.get(position);
        final Medicine medicine = medicineSelecter.getMedicine();

        Log.e("Medicine", medicine.toString());

        Glide.with(context)
                .load(storage.getReferenceFromUrl(medicine.getImage_link()))
                .transition(withCrossFade())
                .fitCenter()
                .error(R.drawable.error_loading)
                .fallback(R.drawable.error_loading)
                .into(holder.imageView);

        holder.textViewName.setText(medicine.getName());
        holder.textViewPrice.setText(String.valueOf(medicine.getPrice()) + " LKR");

        if (medicineSelecter.isSelected()) {
            holder.constraintLayout.setBackgroundColor(Color.parseColor("#AAAAAA"));
        } else {
            holder.constraintLayout.setBackgroundColor(0);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getSelectedItemList().contains(medicineSelecter) && getSelectedCount() >3) {
                    if (getSelectedCount()==4){
                        Toast.makeText(context,"Maximum 4 Items can be Ordered.",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    medicineSelecterList.get(position).setSelected(!medicineSelecterList.get(position).isSelected());
                    if (medicineSelecterList.get(position).isSelected()) {
                        holder.constraintLayout.setBackgroundColor(Color.parseColor("#AAAAAA"));
                    } else {
                        holder.constraintLayout.setBackgroundColor(0);
                    }
                }

                Log.e("selectCount", String.valueOf(getSelectedCount()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return medicineSelecterList.size();
    }

    public class HomeRecyclerViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textViewName;
        TextView textViewPrice;

        ConstraintLayout constraintLayout;

        public HomeRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imgThumb_medicine);
            textViewName = itemView.findViewById(R.id.txt_name);
            textViewPrice = itemView.findViewById(R.id.txt_price);

            constraintLayout = itemView.findViewById(R.id.constraint_layout);
        }
    }

    public List<MedicineSelecter> getSelectedItemList() {
        List<MedicineSelecter> selectedList = new ArrayList<>(medicineSelecterList);

        CollectionUtils.filter(selectedList, new Predicate<MedicineSelecter>() {
            @Override
            public boolean evaluate(MedicineSelecter object) {
                return object.isSelected();
            }
        });

        return selectedList;
    }

    public int getSelectedCount() {
        List<MedicineSelecter> selectedList = new ArrayList<>(medicineSelecterList);

        CollectionUtils.filter(selectedList, new Predicate<MedicineSelecter>() {
            @Override
            public boolean evaluate(MedicineSelecter object) {
                return object.isSelected();
            }
        });

        return selectedList.size();
    }
}
