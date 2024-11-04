package com.example.watermanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class WaterAuthorityPage extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private TextView avatarText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_water_authority_page);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        avatarText = findViewById(R.id.avatar_text);
        setupAvatar();
        setupCardListeners();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupAvatar() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("email");
                            if (name != null && !name.isEmpty()) {
                                avatarText.setText(String.valueOf(name.charAt(0)).toUpperCase());
                            }
                        }
                    });
        }
    }

    private void setupCardListeners() {
        findViewById(R.id.card_reported_issue).setOnClickListener(v -> {
            Intent intent = new Intent(this, ReportedWaterIssues.class);
            startActivity(intent);
        });

        findViewById(R.id.card_user_feedback).setOnClickListener(v -> {
            Intent intent = new Intent(this, UserFeedBack.class);
            startActivity(intent);
        });

        findViewById(R.id.card_water_sources).setOnClickListener(v -> {
            Intent intent = new Intent(this, CleanWaterSource.class);
            startActivity(intent);
        });

        findViewById(R.id.card_contact_authorities).setOnClickListener(v -> {
            Intent intent = new Intent(this, ContactAuthoritiesActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.card_user_profile).setOnClickListener(v -> {
            Intent intent = new Intent(this, UserProfile.class);
            startActivity(intent);
        });

        findViewById(R.id.card_data_insights).setOnClickListener(v -> {
            Intent intent = new Intent(this, DataInsightsActivity.class);
            startActivity(intent);
        });
    }

}