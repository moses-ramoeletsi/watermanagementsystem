package com.example.watermanagementsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class residentsHomepage extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private TextView avatarText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.residentshomepage);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        avatarText = findViewById(R.id.avatar_text);
        setupAvatar();
        setupCardListeners();
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
        findViewById(R.id.card_report_issue).setOnClickListener(v -> {
            Intent intent = new Intent(this, ReportWaterIssues.class);
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


        findViewById(R.id.card_track_report).setOnClickListener(v -> {
            Intent intent =  new Intent(this, TrackReport.class);
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
    }

}