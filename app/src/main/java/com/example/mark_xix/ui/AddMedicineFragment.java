package com.example.mark_xix.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mark_xix.R;
import com.example.mark_xix.models.Medicine;
import com.example.mark_xix.viewadapters.AddMedicineRecyclerViewAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Add / Edit Medicine
 */
public class AddMedicineFragment extends Fragment {

    private static final String TAG = "Add_Medicine";

    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final CollectionReference collectionReferenceMedicine = db.collection("medicines");

    private AddMedicineRecyclerViewAdapter addMedicineRecyclerViewAdapter;

    private RecyclerView recyclerView;

    private ProgressBar progressBar;

    private Button buttonAdd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_medicine, container, false);

        progressBar = root.findViewById(R.id.progress_bar);

        buttonAdd=root.findViewById(R.id.btnAdd_order);

        recyclerView = root.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        /*
         * Fetching medicine list from firebase and showing it in a recyclerview.
         */
        collectionReferenceMedicine
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Medicine> medicineList = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                Medicine medicine = document.toObject(Medicine.class);

                                medicineList.add(medicine);
                            }

                            if (task.isComplete()) {
                                Collections.sort(medicineList, new Comparator<Medicine>() {
                                    @Override
                                    public int compare(Medicine o1, Medicine o2) {
                                        return o1.getSlot().compareTo(o2.getSlot());
                                    }
                                });

                                addMedicineRecyclerViewAdapter = new AddMedicineRecyclerViewAdapter(getContext(), medicineList);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(addMedicineRecyclerViewAdapter);

                                if (addMedicineRecyclerViewAdapter.getItemCount()<18){
                                    buttonAdd.setEnabled(true);

                                    //Add button
                                    buttonAdd.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //Calling "AddMedicineEdit" Fragment

                                            Bundle bundle = new Bundle();
                                            bundle.putString("id", "");
                                            bundle.putSerializable("medicineList", (Serializable) medicineList);
                                            Navigation.findNavController(root).navigate(R.id.navigation_add_medicine_edit, bundle);
                                        }
                                    });
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        return root;
    }

    //On Resume re fetching the list
    @Override
    public void onResume() {
        super.onResume();

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(null);

        collectionReferenceMedicine
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Medicine> medicineList = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                Medicine medicine = document.toObject(Medicine.class);

                                medicineList.add(medicine);
                            }

                            if (task.isComplete()) {
                                Collections.sort(medicineList, new Comparator<Medicine>() {
                                    @Override
                                    public int compare(Medicine o1, Medicine o2) {
                                        return o1.getSlot().compareTo(o2.getSlot());
                                    }
                                });

                                addMedicineRecyclerViewAdapter = new AddMedicineRecyclerViewAdapter(getContext(), medicineList);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(addMedicineRecyclerViewAdapter);

                                if (addMedicineRecyclerViewAdapter.getItemCount()<18){
                                    buttonAdd.setEnabled(true);

                                    buttonAdd.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Bundle bundle = new Bundle();
                                            bundle.putString("id", "");
                                            bundle.putSerializable("medicineList", (Serializable) medicineList);
                                            Navigation.findNavController(v).navigate(R.id.navigation_add_medicine_edit, bundle);
                                        }
                                    });
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
