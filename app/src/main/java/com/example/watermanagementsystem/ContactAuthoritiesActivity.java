package com.example.watermanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ContactAuthoritiesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AuthoritiesAdapter adapter;
    private TextView errorMessageTextView;
    private ProgressBar progressBar;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contact_authorities);
        db = FirebaseFirestore.getInstance();

        // Initialize views
        recyclerView = findViewById(R.id.authoritiesRecyclerView);
        errorMessageTextView = findViewById(R.id.errorMessageTextView);
        progressBar = findViewById(R.id.progressBar);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AuthoritiesAdapter(this, new ArrayList<>(), authority -> {
            Intent intent = new Intent(this, ContactFormActivity.class);
            intent.putExtra("selected_authority", authority);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        // Load authorities
        loadWaterAuthorities();
        Button viewMessagesButton = findViewById(R.id.viewMessagesButton);
        viewMessagesButton.setOnClickListener(v ->


                startActivity(new Intent(this, SentMessagesActivity.class))
        );
    }


    private void loadWaterAuthorities() {
        showProgress();

        db.collection("users")
                .whereEqualTo("role", "waterAuthority")
                .get()
                .addOnCompleteListener(task -> {
                    hideProgress();

                    if (task.isSuccessful()) {
                        List<UserDetails> authorities = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            UserDetails authority = document.toObject(UserDetails.class);
                            authority.setUserId(document.getId()); // Set the document ID as userId
                            authorities.add(authority);
                        }

                        if (authorities.isEmpty()) {
                            showError();
                        } else {
                            showAuthorities(authorities);
                        }
                    } else {
                        showError();
                        Log.e("ContactAuthorities", "Error getting authorities", task.getException());
                    }
                });
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        errorMessageTextView.setVisibility(View.GONE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    private void showError() {
        errorMessageTextView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void showAuthorities(List<UserDetails> authorities) {
        recyclerView.setVisibility(View.VISIBLE);
        errorMessageTextView.setVisibility(View.GONE);
        adapter.updateAuthorities(authorities);
    }
}