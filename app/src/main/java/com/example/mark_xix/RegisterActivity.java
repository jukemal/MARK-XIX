package com.example.mark_xix;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mark_xix.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

//Register
public class RegisterActivity extends AppCompatActivity {

    private final FirebaseFirestore db= FirebaseFirestore.getInstance();
    private final FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();

    private final CollectionReference collectionReferenceUser=db.collection("users");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final TextInputLayout textInputLayoutEmail=findViewById(R.id.txtEmailLayout);
        final TextInputEditText textInputEditTextEmail=findViewById(R.id.txtEmail);
        final TextInputLayout textInputLayoutName=findViewById(R.id.txtNameLayout);
        final TextInputEditText textInputEditTextName=findViewById(R.id.txtName);
        final TextInputLayout textInputLayoutNumber=findViewById(R.id.txtNumberLayout);
        final TextInputEditText textInputEditTextNumber=findViewById(R.id.txtNumber);
        final TextInputLayout textInputLayoutPassword=findViewById(R.id.txtPasswordLayout);
        final TextInputEditText textInputEditTextPassword=findViewById(R.id.txtPassword);

        Button buttonRegister=findViewById(R.id.btnRegister);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            private static final String TAG = "Register Activity";

            @Override
            public void onClick(View v) {
                String email=textInputEditTextEmail.getText().toString();
                final String name=textInputEditTextName.getText().toString();
                final String number=textInputEditTextNumber.getText().toString();
                String password=textInputEditTextPassword.getText().toString();

                if (email.isEmpty()||name.isEmpty()||number.isEmpty()||password.isEmpty()){
                    if (email.isEmpty()){
                        textInputLayoutEmail.setError("Enter Your Email Address.");
                    }else {
                        textInputLayoutEmail.setError(number);
                    }
                    if (name.isEmpty()){
                        textInputLayoutName.setError("Enter Your Name.");
                    }else {
                        textInputLayoutName.setError(number);
                    }
                    if (number.isEmpty()){
                        textInputLayoutNumber.setError("Enter Your Telephone Number.");
                    }else {
                        textInputLayoutNumber.setError(number);
                    }
                    if (password.isEmpty()){
                        textInputLayoutPassword.setError("Enter a Valid Password.");
                    }else {
                        textInputLayoutPassword.setError(number);
                    }
                }else {
                    firebaseAuth.createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();

                                        User user=User.builder()
                                                .email(firebaseUser.getEmail())
                                                .name(name)
                                                .telephoneNumber(number)
                                                .build();

                                        collectionReferenceUser
                                                .document(firebaseUser.getUid())
                                                .set(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "DocumentSnapshot successfully written!");

                                                        Toast.makeText(getApplicationContext(), "Registration Successfully.", Toast.LENGTH_SHORT).show();

                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error writing document", e);

                                                        Toast.makeText(getApplicationContext(), "Registration Unsuccessful, Please Try Again", Toast.LENGTH_SHORT).show();

                                                    }
                                                });
                                    }else {
                                        Toast.makeText(getApplicationContext(), "Registration Unsuccessful, Please Try Again", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                }
            }
        });
    }
}
