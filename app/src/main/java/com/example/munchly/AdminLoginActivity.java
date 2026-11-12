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

public class AdminLoginActivity extends AppCompatActivity {

    private EditText etAdminEmail, etAdminPassword;
    private Button btnAdminLogin;
    private TextView tvBackToCustomer;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        mAuth = FirebaseAuth.getInstance();

        etAdminEmail = findViewById(R.id.etAdminEmail);
        etAdminPassword = findViewById(R.id.etAdminPassword);
        btnAdminLogin = findViewById(R.id.btnAdminLogin);
        tvBackToCustomer = findViewById(R.id.tvBackToCustomer);

        btnAdminLogin.setOnClickListener(v -> {
            String email = etAdminEmail.getText().toString().trim();
            String password = etAdminPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter admin credentials", Toast.LENGTH_SHORT).show();
                return;
            }

            btnAdminLogin.setEnabled(false);

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        // Session is only set AFTER Firebase confirms the credentials.
                        User adminUser = new User(
                                "admin_" + System.currentTimeMillis(),
                                "System Administrator",
                                email,
                                "254700000000",
                                "",
                                "admin");
                        MunchlyRepository.getInstance().setCurrentUser(adminUser);

                        Toast.makeText(this, "Admin Login Successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, AdminDashboardActivity.class));
                        finishAffinity();
                    })
                    .addOnFailureListener(e -> {
                        btnAdminLogin.setEnabled(true);
                        Toast.makeText(this,
                                "Admin login failed: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    });
        });

        tvBackToCustomer.setOnClickListener(v -> finish());
    }
}