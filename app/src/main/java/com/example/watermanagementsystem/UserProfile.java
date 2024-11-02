package com.example.watermanagementsystem;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserProfile extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private TextView avatarText;
    private TextView userEmail, phoneNumber, address, nationalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        avatarText = findViewById(R.id.avatar_text);
        initViews();
        setupUserDetails();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        userEmail = findViewById(R.id.userEmail);
        phoneNumber = findViewById(R.id.phoneNumber);
        address = findViewById(R.id.address);
        nationalId = findViewById(R.id.nationalId);
    }

    private void setupUserDetails() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(this::populateUserDetails)
                    .addOnFailureListener(e -> {

                    });
        }
    }

    private void populateUserDetails(DocumentSnapshot documentSnapshot) {
        if (documentSnapshot.exists()) {
            String email = documentSnapshot.getString("email");
            String phone = documentSnapshot.getString("contacts");
            String addr = documentSnapshot.getString("residentialAddress");
            String nationalIdVal = documentSnapshot.getString("nationalId");

            if (email != null && !email.isEmpty()) {
                avatarText.setText(String.valueOf(email.charAt(0)).toUpperCase());
                userEmail.setText(email);
            }

            if (phone != null) {
                phoneNumber.setText(phone);
            }
            if (addr != null) {
                address.setText(addr);
            }
            if (nationalIdVal != null) {
                nationalId.setText(nationalIdVal);
            }
        }
    }
}
