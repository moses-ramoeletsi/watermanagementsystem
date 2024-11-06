package com.example.watermanagementsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class logIn extends AppCompatActivity {
    private EditText useremail, userpassword;
    private Button loginButton;
    private TextView register;
    private ProgressDialog loadingBar;
    private FirebaseAuth appAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Initialize Firebase instances once
        appAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        useremail = findViewById(R.id.userEmail);
        userpassword = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        register = findViewById(R.id.signupRedirectText);

        loadingBar = new ProgressDialog(this);
        loadingBar.setCanceledOnTouchOutside(false);
    }

    private void setupClickListeners() {
        loginButton.setOnClickListener(view -> loginUser());
        register.setOnClickListener(view -> {
            startActivity(new Intent(logIn.this, registration.class));
        });
    }

    private void loginUser() {
        String email = useremail.getText().toString().trim();
        String password = userpassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            useremail.setError("Email is required");
            useremail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            userpassword.setError("Password is required");
            userpassword.requestFocus();
            return;
        }

        loadingBar.setTitle("Logging In");
        loadingBar.setMessage("Please wait...");
        loadingBar.show();

        // Perform login and role check in parallel
        appAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user != null) {
                        db.collection("users").document(user.getUid())
                                .get()
                                .addOnSuccessListener(document -> {
                                    loadingBar.dismiss();
                                    if (document.exists()) {
                                        String role = document.getString("role");
                                        navigateBasedOnRole(role);
                                    } else {
                                        showError("User document not found");
                                    }
                                })
                                .addOnFailureListener(e -> showError("Failed to fetch user role: " + e.getMessage()));
                    }
                })
                .addOnFailureListener(e -> showError("Login failed: " + e.getMessage()));
    }

    private void navigateBasedOnRole(String role) {
        Intent intent = null;
        if (role == null) {
            showError("User role not found");
            return;
        }

        switch (role) {
            case "Admin":
                intent = new Intent(this, AdminPage.class);
                break;
            case "waterAuthority":
                intent = new Intent(this, WaterAuthorityPage.class);
                break;
            case "user":
                intent = new Intent(this, residentsHomepage.class);
                break;
            default:
                showError("Unknown user role: " + role);
                return;
        }

        if (intent != null) {
            startActivity(intent);
            finish();
        }
    }

    private void showError(String message) {
        loadingBar.dismiss();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}