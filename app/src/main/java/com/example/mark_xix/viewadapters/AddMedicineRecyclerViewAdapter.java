package com.example.mark_xix.viewadapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mark_xix.R;
import com.example.mark_xix.models.Medicine;
import com.google.firebase.storage.FirebaseStorage;

import java.io.Serializable;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

//Add medicine recyclerview adapter.
public class AddMedicineRecyclerViewAdapter extends RecyclerView.Adapter<AddMedicineRecyclerViewAdapter.AddMedicineRecyclerViewHolder> {

    private Context context;
    private List<Medicine> medicineList;

    private final FirebaseStorage storage = FirebaseStorage.getInstance();


    public AddMedicineRecyclerViewAdapter(Context context, List<Medicine> medicineList) {
        this.context = context;
        this.medicineList = medicineList;
    }


    @NonNull
    @Override
    public AddMedicineRecyclerViewAdapter.AddMedicineRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_medicine_item, parent, false);
        return new AddMedicineRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddMedicineRecyclerViewAdapter.AddMedicineRecyclerViewHolder holder, int position) {
        final Medicine medicine = medicineList.get(position);

        Glide.with(context)
                .load(storage.getReferenceFromUrl(medicine.getImage_link()))
                .transition(withCrossFade())
                .fitCenter()
                .error(R.drawable.error_loading)
                .fallback(R.drawable.error_loading)
                .into(holder.imageView);

        holder.textViewName.setText(medicine.getName());
        holder.textViewPrice.setText(String.valueOf(medicine.getPrice()) + " LKR");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("id", medicine.getId());
                bundle.putSerializable("medicineList", (Serializable) medicineList);
                Navigation.findNavController(v).navigate(R.id.navigation_add_medicine_edit, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    public class AddMedicineRecyclerViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView textViewName;
        TextView textViewPrice;

        public AddMedicineRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imgThumb_medicine);
            textViewName = itemView.findViewById(R.id.txt_name);
            textViewPrice = itemView.findViewById(R.id.txt_price);
        }
    }
}
