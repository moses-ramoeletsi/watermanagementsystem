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

public class AdminPage extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private TextView avatarText;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_admin_page);


        db = FirebaseFirestore.getInstance ();
        mAuth = FirebaseAuth.getInstance ();

        avatarText = findViewById (R.id.avatar_text);
        setupAvatar ();
        setupCardListeners ();
    }

    private void setupAvatar () {
        FirebaseUser currentUser = mAuth.getCurrentUser ();
        if ( currentUser != null ) {
            db.collection ("users").document (currentUser.getUid ())
                    .get ()
                    .addOnSuccessListener (documentSnapshot -> {
                        if ( documentSnapshot.exists () ) {
                            String name = documentSnapshot.getString ("name");
                            if ( name != null && ! name.isEmpty () ) {
                                avatarText.setText (String.valueOf (name.charAt (0)).toUpperCase ());
                            }
                        }
                    });
        }
    }

    private void setupCardListeners () {
        findViewById (R.id.card_water_issue).setOnClickListener (v -> {
            Intent intent = new Intent (this, ViewWaterReports.class);
            startActivity (intent);
        });

        findViewById (R.id.card_user_management).setOnClickListener (v -> {
            Intent intent = new Intent (this, UserManagement.class);
            startActivity (intent);
        });

        findViewById (R.id.card_user_profile).setOnClickListener (v -> {
            Intent intent = new Intent (this, UserProfile.class);
            startActivity (intent);
        });
    }
}