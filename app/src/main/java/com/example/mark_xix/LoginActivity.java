package com.example.mark_xix;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

//Login
public class LoginActivity extends AppCompatActivity {

    private final FirebaseFirestore db= FirebaseFirestore.getInstance();
    private final FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final TextInputLayout textInputLayoutEmail=findViewById(R.id.txtEmailLayout);
        final TextInputEditText textInputEditTextEmail=findViewById(R.id.txtEmail);
        final TextInputLayout textInputLayoutPassword=findViewById(R.id.txtPasswordLayout);
        final TextInputEditText textInputEditTextPassword=findViewById(R.id.txtPassword);

        Button buttonLogin=findViewById(R.id.btnLogin);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=textInputEditTextEmail.getText().toString();
                String password=textInputEditTextPassword.getText().toString();

                //Input Validation
                if (email.isEmpty() || password.isEmpty()) {
                    if (email.isEmpty()) {
                        textInputLayoutEmail.setError("Please Enter Your Email.");
                    }else {
                        textInputLayoutEmail.setError(null);
                    }
                    if (password.isEmpty()) {
                        textInputLayoutPassword.setError("Please Enter Your Password.");
                    }else {
                        textInputLayoutPassword.setError(null);
                    }
                } else {
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Login Successful.", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Login Error, Please Login Again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        Button buttonRegister=findViewById(R.id.btnRegister);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

    }
}
