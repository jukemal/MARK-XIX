package com.example.mark_xix.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mark_xix.R;
import com.example.mark_xix.api.ApiService;
import com.example.mark_xix.api.ApiServiceGenerator;
import com.example.mark_xix.models.Medicine;
import com.example.mark_xix.models.MedicineSelecter;
import com.example.mark_xix.utils._ResponseBody;
import com.example.mark_xix.viewadapters.HomeRecyclerViewAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class HomeFragment extends Fragment {

    private static final String TAG = "Home";

    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final CollectionReference collectionReferenceMedicine = db.collection("medicines");

    private HomeRecyclerViewAdapter homeRecyclerViewAdapter;

    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final ProgressBar progressBar = root.findViewById(R.id.progress_bar);

        final RecyclerView recyclerView = root.findViewById(R.id.recyclerview_home);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        collectionReferenceMedicine
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<MedicineSelecter> medicineSelecterList=new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                Medicine medicine = document.toObject(Medicine.class);

                                MedicineSelecter medicineSelecter=MedicineSelecter.builder()
                                        .id(medicine.getId())
                                        .medicine(medicine)
                                        .isSelected(false)
                                        .build();

                                medicineSelecterList.add(medicineSelecter);
                            }

                            if (task.isComplete()) {
                                homeRecyclerViewAdapter = new HomeRecyclerViewAdapter(getContext(), medicineSelecterList);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(homeRecyclerViewAdapter);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        Button buttonOrder=root.findViewById(R.id.btnOrder_home);

        buttonOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (homeRecyclerViewAdapter.getSelectedCount()>0){
                    List<MedicineSelecter> selectedList=homeRecyclerViewAdapter.getSelectedItemList();

                    List<Medicine> selectedMedicineList= (List<Medicine>) CollectionUtils.collect(selectedList, new Transformer<MedicineSelecter, Medicine>() {
                        @Override
                        public Medicine transform(MedicineSelecter input) {
                            return input.getMedicine();
                        }
                    });

                    View viewPopup=inflater.inflate(R.layout.layout_order_popup,null);

                    LinearLayout linearLayout=viewPopup.findViewById(R.id.linear_layout);

                    for (Medicine medicine:selectedMedicineList){
                        View viewPopupItem=inflater.inflate(R.layout.layout_order_popup_item,null);

                        ImageView imageView=viewPopupItem.findViewById(R.id.imgThumb_medicine);
                        TextView textViewName=viewPopupItem.findViewById(R.id.txt_name);
                        TextView textViewPrice=viewPopupItem.findViewById(R.id.txt_price);

                        Glide.with(getContext())
                                .load(storage.getReferenceFromUrl(medicine.getImage_link()))
                                .transition(withCrossFade())
                                .fitCenter()
                                .error(R.drawable.error_loading)
                                .fallback(R.drawable.error_loading)
                                .into(imageView);

                        textViewName.setText(medicine.getName());
                        textViewPrice.setText(String.valueOf(medicine.getPrice()) + " LKR");

                        linearLayout.addView(viewPopupItem);
                    }

                    MaterialAlertDialogBuilder materialAlertDialogBuilder=new MaterialAlertDialogBuilder(getContext());
                    materialAlertDialogBuilder.setView(viewPopup);
                    final AlertDialog alertDialog=materialAlertDialogBuilder.show();

                    Button buttonConfirm=viewPopup.findViewById(R.id.btnConfirm);

                    buttonConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ApiService service= ApiServiceGenerator.createService(ApiService.class);

                            Call<_ResponseBody> call=service.sendMedicineList(selectedMedicineList);

                            call.enqueue(new Callback<_ResponseBody>() {
                                @Override
                                public void onResponse(Call<_ResponseBody> call, Response<_ResponseBody> response) {
                                    Toast.makeText(getContext(),"Successful.",Toast.LENGTH_SHORT).show();
                                    Log.e("response",response.toString());
                                    showProgressDialog();
                                    alertDialog.dismiss();
                                }

                                @Override
                                public void onFailure(Call<_ResponseBody> call, Throwable t) {
                                    Toast.makeText(getContext(),"Error Connecting to Server.",Toast.LENGTH_SHORT).show();
                                    Log.e("err",t.toString());
                                    alertDialog.dismiss();
                                }
                            });
                        }
                    });

                    Button buttonCancel=viewPopup.findViewById(R.id.btnCancel);

                    buttonCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });

                    ImageButton imageButtonClose=viewPopup.findViewById(R.id.close);

                    imageButtonClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
                }
            }
        });


        return root;
    }

    private void showProgressDialog(){
        View viewPopup=LayoutInflater.from(getContext()).inflate(R.layout.layout_progress_popup,null);

        MaterialAlertDialogBuilder materialAlertDialogBuilder=new MaterialAlertDialogBuilder(getContext());
        materialAlertDialogBuilder.setView(viewPopup);
        final AlertDialog alertDialog=materialAlertDialogBuilder.show();

        ImageButton imageButtonClose=viewPopup.findViewById(R.id.close);

        imageButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }
}
