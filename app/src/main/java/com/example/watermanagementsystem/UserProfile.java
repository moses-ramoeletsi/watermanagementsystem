package com.example.watermanagementsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
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
    private Button logoutButton;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_user_profile);
        db = FirebaseFirestore.getInstance ();
        mAuth = FirebaseAuth.getInstance ();

        avatarText = findViewById (R.id.avatar_text);
        logoutButton = findViewById (R.id.logoutButton);

        logoutButton.setOnClickListener (v -> showLogoutConfirmationDialog ());
        initViews ();
        setupUserDetails ();

        ViewCompat.setOnApplyWindowInsetsListener (findViewById (R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets (WindowInsetsCompat.Type.systemBars ());
            v.setPadding (systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews () {
        userEmail = findViewById (R.id.userEmail);
        phoneNumber = findViewById (R.id.phoneNumber);
        address = findViewById (R.id.address);
        nationalId = findViewById (R.id.nationalId);
    }

    private void setupUserDetails () {
        FirebaseUser currentUser = mAuth.getCurrentUser ();
        if ( currentUser != null ) {
            db.collection ("users").document (currentUser.getUid ()).get ().addOnSuccessListener (this::populateUserDetails).addOnFailureListener (e -> {

            });
        }
    }

    private void populateUserDetails (DocumentSnapshot documentSnapshot) {
        if ( documentSnapshot.exists () ) {
            String name = documentSnapshot.getString ("name");
            String email = documentSnapshot.getString ("email");
            String phone = documentSnapshot.getString ("contacts");
            String addr = documentSnapshot.getString ("residentialAddress");
            String nationalIdVal = documentSnapshot.getString ("nationalId");

            if ( name != null && ! name.isEmpty () ) {
                avatarText.setText (String.valueOf (email.charAt (0)).toUpperCase ());
                userEmail.setText (name);
            }

            if ( phone != null ) {
                phoneNumber.setText (phone);
            }
            if ( addr != null ) {
                address.setText (addr);
            }
            if ( nationalIdVal != null ) {
                nationalId.setText (nationalIdVal);
            }
        }
    }

    private void showLogoutConfirmationDialog () {
        new AlertDialog.Builder (this).setTitle ("Logout").setMessage ("Are you sure you want to logout?").setPositiveButton ("Yes", (dialog, which) -> {
            logout ();
        }).setNegativeButton ("No", (dialog, which) -> {
            dialog.dismiss ();
        }).setIcon (android.R.drawable.ic_dialog_alert).show ();
    }

    private void logout () {
        ProgressDialog progressDialog = new ProgressDialog (this);
        progressDialog.setMessage ("Logging out...");
        progressDialog.setCancelable (false);
        progressDialog.show ();

        mAuth.signOut ();

        progressDialog.dismiss ();
        Intent intent = new Intent (this, logIn.class);
        intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity (intent);
        finish ();
    }
}
