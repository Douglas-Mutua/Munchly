package com.example.munchly;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.munchly.data.MunchlyRepository;
import com.example.munchly.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etPhone, etEmail, etPassword;
    private Button btnRegister;
    private TextView tvLoginLink;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvLoginLink);

        btnRegister.setOnClickListener(v -> {
            String name = etFullName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all registration fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!phone.matches("^254[0-9]{9}$")) {
                Toast.makeText(this, "Enter valid Kenyan phone number starting with 254 (e.g. 254712345678)", Toast.LENGTH_LONG).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        String userId = authResult.getUser() != null ? authResult.getUser().getUid() : "u_" + System.currentTimeMillis();
                        User newUser = new User(userId, name, email, phone, password, "customer");

                        db.collection("users").document(userId)
                                .set(newUser)
                                .addOnSuccessListener(aVoid -> {
                                    MunchlyRepository.getInstance().setCurrentUser(newUser);
                                    Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(this, MainActivity.class));
                                    finishAffinity();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Failed to save profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Registration Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });

        tvLoginLink.setOnClickListener(v -> finish());
    }
}