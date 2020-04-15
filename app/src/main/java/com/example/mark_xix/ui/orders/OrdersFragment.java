package com.example.mark_xix.ui.orders;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mark_xix.R;
import com.example.mark_xix.models.Medicine;
import com.example.mark_xix.models.MedicineSelecter;
import com.example.mark_xix.models.OrderHistory;
import com.example.mark_xix.viewadapters.HomeRecyclerViewAdapter;
import com.example.mark_xix.viewadapters.OrderRecyclerViewAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment {

    private static final String TAG = "Home";

    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final CollectionReference collectionReferenceMedicine = db.collection("medicines");

    private final CollectionReference collectionReferenceOrderHistory = db.collection("order_history");

    private OrderRecyclerViewAdapter orderRecyclerViewAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_orders, container, false);

        ProgressBar progressBar = root.findViewById(R.id.progress_bar);

        RecyclerView recyclerView = root.findViewById(R.id.recyclerview_order);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        collectionReferenceOrderHistory
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<OrderHistory> orderHistoryList = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                OrderHistory orderHistory = document.toObject(OrderHistory.class);

                                orderHistoryList.add(orderHistory);
                            }

                            if (task.isComplete()) {
                                orderRecyclerViewAdapter = new OrderRecyclerViewAdapter(getContext(), orderHistoryList);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(orderRecyclerViewAdapter);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        return root;
    }
}
