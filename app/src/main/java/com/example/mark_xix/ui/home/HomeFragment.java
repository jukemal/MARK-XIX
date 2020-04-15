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
import com.example.mark_xix.models.EnumSlot;
import com.example.mark_xix.models.Medicine;
import com.example.mark_xix.models.MedicineSelecter;
import com.example.mark_xix.viewadapters.HomeRecyclerViewAdapter;
import com.github.javafaker.Faker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
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

    private RecyclerView recyclerView;

    private ProgressBar progressBar;

    private Disposable disposable;

    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        progressBar = root.findViewById(R.id.progress_bar);

        recyclerView = root.findViewById(R.id.recyclerview_home);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

//        offlineRun();

        collectionReferenceMedicine
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<MedicineSelecter> medicineSelecterList = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                Medicine medicine = document.toObject(Medicine.class);

                                MedicineSelecter medicineSelecter = MedicineSelecter.builder()
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

        Button buttonOrder = root.findViewById(R.id.btnOrder_home);

        buttonOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (homeRecyclerViewAdapter.getSelectedCount() > 0) {
                    List<MedicineSelecter> selectedList = homeRecyclerViewAdapter.getSelectedItemList();

                    List<Medicine> selectedMedicineList = (List<Medicine>) CollectionUtils.collect(selectedList, new Transformer<MedicineSelecter, Medicine>() {
                        @Override
                        public Medicine transform(MedicineSelecter input) {
                            return input.getMedicine();
                        }
                    });

                    View viewPopup = inflater.inflate(R.layout.layout_order_popup, null);

                    LinearLayout linearLayout = viewPopup.findViewById(R.id.linear_layout);

                    for (Medicine medicine : selectedMedicineList) {
                        View viewPopupItem = inflater.inflate(R.layout.layout_order_popup_item, null);

                        ImageView imageView = viewPopupItem.findViewById(R.id.imgThumb_medicine);
                        TextView textViewName = viewPopupItem.findViewById(R.id.txt_name);
                        TextView textViewPrice = viewPopupItem.findViewById(R.id.txt_price);

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

                    MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getContext());
                    materialAlertDialogBuilder.setView(viewPopup);
                    final AlertDialog alertDialog = materialAlertDialogBuilder.show();

                    Button buttonConfirm = viewPopup.findViewById(R.id.btnConfirm);

                    buttonConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            buttonConfirm.setEnabled(false);

                            AlertDialog progressDialog = showProgressDialog();
                            alertDialog.dismiss();

                            ApiService service = ApiServiceGenerator.createService(ApiService.class);

                            Call<Object> call = service.sendMedicineList(selectedMedicineList);

                            call.enqueue(new Callback<Object>() {
                                @Override
                                public void onResponse(Call<Object> call, Response<Object> response) {
                                    Toast.makeText(getContext(), "Successful.", Toast.LENGTH_SHORT).show();

                                    Gson gson = new Gson();

                                    if (response.isSuccessful()) {

                                        

                                        disposable = Observable.interval(2, TimeUnit.SECONDS)
                                                .take(20)
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new Consumer<Long>() {
                                                    @Override
                                                    public void accept(Long aLong) throws Exception {
                                                        Call<Object> progress = service.getProgress();

                                                        progress.enqueue(new Callback<Object>() {
                                                            @Override
                                                            public void onResponse(Call<Object> call, Response<Object> response) {
                                                                String json = gson.toJson(response.body());

                                                                Map<String, Object> map = gson.fromJson(json, new TypeToken<Map<String, Object>>() {
                                                                }.getType());

                                                                Log.e("response_object", (String) map.get("progress"));

                                                                if (String.valueOf(map.get("progress")).equals("done")) {
                                                                    progressDialog.dismiss();
                                                                    Toast.makeText(getContext(), "Order Successfully Completed.", Toast.LENGTH_SHORT).show();
                                                                    disposable.dispose();
                                                                }


                                                            }

                                                            @Override
                                                            public void onFailure(Call<Object> call, Throwable t) {
                                                                Toast.makeText(getContext(), "Error Connecting to Server.", Toast.LENGTH_SHORT).show();
                                                                disposable.dispose();
                                                            }
                                                        });
                                                    }
                                                });

                                    }else {
                                        progressDialog.dismiss();
                                        Toast.makeText(getContext(), "Error Connecting to Server.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Object> call, Throwable t) {
                                    Toast.makeText(getContext(), "Error Connecting to Server.", Toast.LENGTH_SHORT).show();
                                    Log.e("response_object_err", t.toString());

                                    buttonConfirm.setEnabled(true);
                                    alertDialog.dismiss();
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    });

                    Button buttonCancel = viewPopup.findViewById(R.id.btnCancel);

                    buttonCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });

                    ImageButton imageButtonClose = viewPopup.findViewById(R.id.close);

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

    private AlertDialog showProgressDialog() {
        View viewPopup = LayoutInflater.from(getContext()).inflate(R.layout.layout_progress_popup, null);

        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getContext());
        materialAlertDialogBuilder.setView(viewPopup);
        final AlertDialog alertDialog = materialAlertDialogBuilder.show();

        ImageButton imageButtonClose = viewPopup.findViewById(R.id.close);

        imageButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        return alertDialog;
    }

    private void offlineRun() {

        List<MedicineSelecter> medicineSelecterList = new ArrayList<>();

        final List<String> picture_list = new ArrayList<>();
        picture_list.add("gs://mark-xix.appspot.com/medicine_1.jpg");
        picture_list.add("gs://mark-xix.appspot.com/medicine_2.jpg");
        picture_list.add("gs://mark-xix.appspot.com/medicine_3.jpg");
        picture_list.add("gs://mark-xix.appspot.com/medicine_4.jpg");
        picture_list.add("gs://mark-xix.appspot.com/medicine_5.jpg");

        final Faker faker = new Faker();
        final Random random = new Random();
        Calendar calendar = Calendar.getInstance();

        for (EnumSlot slot : EnumSlot.values()) {

            Medicine medicine = Medicine.builder()
                    .id(String.valueOf(random.nextInt(100000 + 1) + 10000))
                    .name(faker.lorem().word())
                    .price(random.nextInt(900 + 1) + 100)
                    .description(faker.lorem().paragraph(1))
                    .slot(slot)
                    .image_link(picture_list.get(random.nextInt(5)))
                    .timestamp(calendar.getTime())
                    .build();

            MedicineSelecter medicineSelecter = MedicineSelecter.builder()
                    .id(medicine.getId())
                    .medicine(medicine)
                    .isSelected(false)
                    .build();

            medicineSelecterList.add(medicineSelecter);
        }

        homeRecyclerViewAdapter = new HomeRecyclerViewAdapter(getContext(), medicineSelecterList);
        progressBar.setVisibility(View.GONE);
        recyclerView.setAdapter(homeRecyclerViewAdapter);
    }
}
