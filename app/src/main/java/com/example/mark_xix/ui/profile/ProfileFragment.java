package com.example.mark_xix.ui.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mark_xix.R;
import com.example.mark_xix.SplashScreen;
import com.example.mark_xix.models.EnumSlot;
import com.example.mark_xix.models.Medicine;
import com.github.javafaker.Faker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class ProfileFragment extends Fragment {

    private static final String TAG = "Profile";
    private final FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    private final FirebaseFirestore db=FirebaseFirestore.getInstance();

    private final CollectionReference collectionReferenceMedicine=db.collection("medicines");

    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        Button buttonLogout=root.findViewById(R.id.btnLogOut);

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());

                builder.setMessage("Do you want to Log Off?")
                        .setTitle("Log Off")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                firebaseAuth.signOut();
                                Intent intent = new Intent(getActivity(), SplashScreen.class);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                builder.show();
            }
        });

        Button buttonAdd=root.findViewById(R.id.btnAdd);

        final Faker faker=new Faker();
        final Random random=new Random();

        final List<String> picture_list=new ArrayList<>();
        picture_list.add("gs://mark-xix.appspot.com/medicine_1.jpg");
        picture_list.add("gs://mark-xix.appspot.com/medicine_2.jpg");
        picture_list.add("gs://mark-xix.appspot.com/medicine_3.jpg");
        picture_list.add("gs://mark-xix.appspot.com/medicine_4.jpg");
        picture_list.add("gs://mark-xix.appspot.com/medicine_5.jpg");

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (EnumSlot slot:EnumSlot.values()){
                    Medicine medicine=Medicine.builder()
                            .name(faker.lorem().word())
                            .price(random.nextInt(900+1)+100)
                            .description(faker.lorem().paragraph(6))
                            .slot(slot)
                            .image_link(picture_list.get(random.nextInt(5)))
                            .build();

                    Log.e("Medicine",medicine.toString());

                    collectionReferenceMedicine
                            .add(medicine)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.e(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "Error adding document", e);
                                }
                            });

                }
            }
        });

        Button buttonDelete=root.findViewById(R.id.btnDelete);

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectionReferenceMedicine
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());

                                        collectionReferenceMedicine
                                                .document(document.getId())
                                                .delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error deleting document", e);
                                                    }
                                                });
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });

        return root;
    }
}
