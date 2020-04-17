package com.example.mark_xix.ui.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.mark_xix.R;
import com.example.mark_xix.SplashScreen;
import com.example.mark_xix.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private static final String TAG = "Profile";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private final DocumentReference documentReferenceUser=db.collection("users")
            .document(firebaseAuth.getCurrentUser().getUid());

    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView name=root.findViewById(R.id.name_profile);
        TextView email=root.findViewById(R.id.email_profile);

        documentReferenceUser.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.e("User", "DocumentSnapshot data: " + document.getData());

                                User user=document.toObject(User.class);

                                name.setText(user.getName());
                                email.setText(user.getEmail());

                            } else {
                                Log.e("User", "No such document");
                            }
                        } else {
                            Log.e("User", "get failed with ", task.getException());
                            Toast.makeText(getContext(),"No Internet Connection. Try Again",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        Button btnEditMedicine=root.findViewById(R.id.btnEditMedicine);

        btnEditMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(container).navigate(R.id.navigation_add_medicine);
            }
        });

        Button buttonSettings=root.findViewById(R.id.btnSettings);

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(container).navigate(R.id.navigation_settings);
            }
        });

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

        return root;
    }
}
