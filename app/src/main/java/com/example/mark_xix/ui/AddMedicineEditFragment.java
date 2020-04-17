package com.example.mark_xix.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.mark_xix.BuildConfig;
import com.example.mark_xix.R;
import com.example.mark_xix.models.EnumSlot;
import com.example.mark_xix.models.Medicine;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.SneakyThrows;

import static android.app.Activity.RESULT_OK;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/*
Add / Edit Medicine
 */
public class AddMedicineEditFragment extends Fragment {

    private static final int REQUEST_CODE_CHOOSE = 102;
    private static final String TAG = "Edit Product";

    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final CollectionReference collectionReferenceMedicine = db.collection("medicines");

    private ImageView imageViewImage;

    private Uri photoUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_medicine_edit, container, false);

        //Selected medicine id
        final String id = getArguments().getString("id");
        Log.e("ID", id);

        //Medicine list
        final List<Medicine> med = (List<Medicine>) getArguments().getSerializable("medicineList");

        ProgressBar progressBar = root.findViewById(R.id.progress_bar);
        ProgressBar progressBarAdd = root.findViewById(R.id.progress_bar_add);
        CardView cardView = root.findViewById(R.id.card_view);

        TextInputLayout textInputLayoutName = root.findViewById(R.id.txtNameLayout);
        TextInputEditText textInputEditTextName = root.findViewById(R.id.txtName);

        TextInputLayout textInputLayoutPrice = root.findViewById(R.id.txtPriceLayout);
        TextInputEditText textInputEditTextPrice = root.findViewById(R.id.txtPrice);

        TextInputLayout textInputLayoutDescription = root.findViewById(R.id.txtDescriptionLayout);
        TextInputEditText textInputEditTextDescription = root.findViewById(R.id.txtDescription);

        Spinner spinner = root.findViewById(R.id.spinner);

        imageViewImage = root.findViewById(R.id.imgUploadedImage);

        Button buttonChoosePicture = root.findViewById(R.id.btnImageUpdate);

        Button buttonUpdate = root.findViewById(R.id.btnUpdte);

        Button buttonDelete = root.findViewById(R.id.btnDelete);

        Button buttonAdd = root.findViewById(R.id.btnAdd);

        LinearLayout linearLayoutUpdate = root.findViewById(R.id.linear_layout_update);
        LinearLayout linearLayoutAdd = root.findViewById(R.id.linear_layout_add);

        List<String> alreadyOccupiedSlotList = new ArrayList<>();

        //Currently occupied slots
        for (Medicine medicine : med) {
            alreadyOccupiedSlotList.add(medicine.getSlot().toString());
        }

        List<String> slotList = new ArrayList<>();

        //Empty slots
        for (EnumSlot slot : EnumSlot.values()) {
            if (!alreadyOccupiedSlotList.contains(slot.toString())) {
                slotList.add(slot.toString());
            }
        }

        /*
        If id is empty it means new medicine is been added.
        Else updating or deleting existing medicines.
         */
        if (id.isEmpty()) {
            //Add medicine

            //Setting spinner to slot list
            spinner.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, slotList));

            //Add button
            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonUpdate.setEnabled(false);

                    String name = textInputEditTextName.getText().toString();
                    String price = textInputEditTextPrice.getText().toString();
                    String description = textInputEditTextDescription.getText().toString();

                    //Input validation
                    if (name.isEmpty() || price.isEmpty() || description.isEmpty() || photoUri == null) {
                        if (name.isEmpty()) {
                            textInputLayoutName.setError("Enter Name");
                        } else {
                            textInputLayoutName.setError(null);
                        }
                        if (price.isEmpty()) {
                            textInputLayoutPrice.setError("Enter Valid Price");
                        } else {
                            textInputLayoutPrice.setError(null);
                        }
                        if (description.isEmpty()) {
                            textInputLayoutDescription.setError("Enter Valid Description");
                        } else {
                            textInputLayoutDescription.setError(null);
                        }

                        TextView textViewError = root.findViewById(R.id.imgUploadedImage_err);

                        if (photoUri == null) {
                            textViewError.setVisibility(View.VISIBLE);
                        } else {
                            textViewError.setVisibility(View.GONE);
                        }
                    } else {
                        progressBarAdd.setVisibility(View.VISIBLE);

                        String path = "gs://mark-xix.appspot.com/" + photoUri.getLastPathSegment();

                        StorageReference reference = storageRef.child("/" + photoUri.getLastPathSegment());

                        //Uploading image to the firebase storage.
                        reference
                                .putFile(photoUri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("name", name);
                                        map.put("price", Integer.parseInt(price));
                                        map.put("description", description);
                                        map.put("slot", spinner.getSelectedItem().toString());
                                        map.put("image_link", path);

                                        //Adding medicine to the firebase.
                                        collectionReferenceMedicine
                                                .add(map)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                                        Toast.makeText(getContext(), "Successfully Added.", Toast.LENGTH_SHORT).show();
                                                        Navigation.findNavController(root).navigateUp();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error adding document", e);
                                                        Toast.makeText(getContext(), "Error Adding.", Toast.LENGTH_SHORT).show();
                                                        buttonUpdate.setEnabled(true);
                                                        progressBarAdd.setVisibility(View.GONE);
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "Error Updating.", Toast.LENGTH_SHORT).show();
                                        buttonUpdate.setEnabled(true);
                                        progressBarAdd.setVisibility(View.GONE);
                                    }
                                });
                    }
                }
            });

            progressBar.setVisibility(View.GONE);
            linearLayoutAdd.setVisibility(View.VISIBLE);
        } else {
            //Update / Edit Medicine

            cardView.setVisibility(View.GONE);

            //Fetching selected medicine data from firebase
            collectionReferenceMedicine
                    .document(id)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @SneakyThrows
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Medicine medicine = document.toObject(Medicine.class);

                                    //Setting fields

                                    textInputEditTextName.setText(medicine.getName());
                                    textInputEditTextPrice.setText(String.valueOf(medicine.getPrice()));
                                    textInputEditTextDescription.setText(medicine.getDescription());

                                    Glide.with(getContext())
                                            .load(storage.getReferenceFromUrl(medicine.getImage_link()))
                                            .transition(withCrossFade())
                                            .fitCenter()
                                            .error(R.drawable.error_loading)
                                            .fallback(R.drawable.error_loading)
                                            .into(imageViewImage);

                                    slotList.add(medicine.getSlot().toString());

                                    spinner.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, slotList));

                                    int i = 0;
                                    for (String s : slotList) {
                                        if (s.equals(medicine.getSlot().toString())) {
                                            break;
                                        }
                                        i++;
                                    }

                                    spinner.setSelection(i);

                                    //Update button
                                    buttonUpdate.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            buttonUpdate.setEnabled(false);

                                            String name = textInputEditTextName.getText().toString();
                                            String price = textInputEditTextPrice.getText().toString();
                                            String description = textInputEditTextDescription.getText().toString();

                                            //Input validation
                                            if (name.isEmpty() || price.isEmpty() || description.isEmpty()) {
                                                if (name.isEmpty()) {
                                                    textInputLayoutName.setError("Enter Name");
                                                } else {
                                                    textInputLayoutName.setError(null);
                                                }
                                                if (price.isEmpty()) {
                                                    textInputLayoutPrice.setError("Enter Valid Price");
                                                } else {
                                                    textInputLayoutPrice.setError(null);
                                                }
                                                if (description.isEmpty()) {
                                                    textInputLayoutDescription.setError("Enter Valid Description");
                                                } else {
                                                    textInputLayoutDescription.setError(null);
                                                }
                                            } else {
                                                /*
                                                Checking whether a new image has been selected or not.
                                                 */
                                                if (photoUri == null) {
                                                    //If new image has not been selected

                                                    Map<String, Object> map = new HashMap<>();
                                                    map.put("name", name);
                                                    map.put("price", Integer.parseInt(price));
                                                    map.put("description", description);
                                                    map.put("slot", spinner.getSelectedItem().toString());


                                                    collectionReferenceMedicine
                                                            .document(medicine.getId())
                                                            .update((Map<String, Object>) map)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                                                                    Toast.makeText(getContext(), "Successfully Updated.", Toast.LENGTH_SHORT).show();
                                                                    Navigation.findNavController(root).navigateUp();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.w(TAG, "Error updating document", e);
                                                                    Toast.makeText(getContext(), "Error Updating.", Toast.LENGTH_SHORT).show();
                                                                    buttonUpdate.setEnabled(true);
                                                                }
                                                            });
                                                } else {
                                                    //If new image has been selected

                                                    progressBarAdd.setVisibility(View.VISIBLE);

                                                    String path = "gs://mark-xix.appspot.com/" + photoUri.getLastPathSegment();

                                                    StorageReference reference = storageRef.child("/" + photoUri.getLastPathSegment());

                                                    //Uploading photo to the firebase.
                                                    reference
                                                            .putFile(photoUri)
                                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                @Override
                                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                    Map<String, Object> map = new HashMap<>();
                                                                    map.put("name", name);
                                                                    map.put("price", Integer.parseInt(price));
                                                                    map.put("description", description);
                                                                    map.put("slot", spinner.getSelectedItem().toString());
                                                                    map.put("image_link", path);

                                                                    //Update medicine data.
                                                                    collectionReferenceMedicine
                                                                            .document(medicine.getId())
                                                                            .update((Map<String, Object>) map)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                                                                                    Toast.makeText(getContext(), "Successfully Updated.", Toast.LENGTH_SHORT).show();
                                                                                    Navigation.findNavController(root).navigateUp();
                                                                                }
                                                                            })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    Log.w(TAG, "Error updating document", e);
                                                                                    Toast.makeText(getContext(), "Error Updating.", Toast.LENGTH_SHORT).show();
                                                                                    buttonUpdate.setEnabled(true);
                                                                                    progressBarAdd.setVisibility(View.GONE);
                                                                                }
                                                                            });
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(getContext(), "Error Updating.", Toast.LENGTH_SHORT).show();
                                                                    buttonUpdate.setEnabled(true);
                                                                    progressBarAdd.setVisibility(View.GONE);
                                                                }
                                                            });
                                                }
                                            }
                                        }
                                    });


                                    //Delete button.
                                    buttonDelete.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());

                                            builder
                                                    .setTitle("Delete Medicine")
                                                    .setMessage("Confirm Delete Medicine")
                                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                            //Delete medicine from the firestore.
                                                            collectionReferenceMedicine
                                                                    .document(medicine.getId())
                                                                    .delete()
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                                                            Toast.makeText(getContext(), "Successfully Deleted", Toast.LENGTH_SHORT).show();
                                                                            Navigation.findNavController(root).navigateUp();
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Log.w(TAG, "Error deleting document", e);
                                                                            Toast.makeText(getContext(), "Error Deleting", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                        }
                                                    })
                                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                        }
                                                    });

                                            builder.show();
                                        }
                                    });

                                    progressBar.setVisibility(View.GONE);
                                    cardView.setVisibility(View.VISIBLE);
                                    linearLayoutUpdate.setVisibility(View.VISIBLE);
                                } else {
                                    Log.e(TAG, "No such document");
                                }
                            } else {
                                Log.e(TAG, "get failed with ", task.getException());
                            }
                        }
                    });
        }

        //Choose picture button.
        //Uses Matisse library to choose a picture from the gallery or take a new picture.
        buttonChoosePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Matisse.from(AddMedicineEditFragment.this)
                        .choose(MimeType.ofImage(), false)
                        .spanCount(3)
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                        .thumbnailScale(0.85f)
                        .imageEngine(new GlideEngine())
                        .capture(true)
                        .captureStrategy(new CaptureStrategy(true, BuildConfig.APPLICATION_ID + ".provider", "MARK-XIX"))
                        .forResult(REQUEST_CODE_CHOOSE);
            }
        });

        return root;
    }

    //Chosen photo details arrives here.
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            Log.e("Matisse", "Uris: " + Matisse.obtainResult(data));
            Log.e("Matisse", "Paths: " + Matisse.obtainPathResult(data));

            photoUri = Matisse.obtainResult(data).get(0);

            //Setting imageview
            imageViewImage.setImageURI(Matisse.obtainResult(data).get(0));
        }
    }
}
