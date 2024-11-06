package com.example.watermanagementsystem;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class WaterAuthorityMessages extends AppCompatActivity {
    private RecyclerView messagesRecyclerView;
    private AuthorityMessageAdapter messagesAdapter;
    private TextView errorMessageTextView;
    private ProgressBar progressBar;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_water_authority_messages);

        // Initialize FirebaseFirestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        messagesRecyclerView = findViewById(R.id.authoritiesRecyclerView);
        errorMessageTextView = findViewById(R.id.errorMessageTextView);
        progressBar = findViewById(R.id.progressBar);

        // Setup RecyclerView
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesAdapter = new AuthorityMessageAdapter(this, new ArrayList<>());
        messagesRecyclerView.setAdapter(messagesAdapter);

        // Load messages
        loadMessages();
    }

    private void loadMessages() {
        showProgress();

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("messages")
                .whereEqualTo("recipientId", currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    hideProgress();

                    if (task.isSuccessful()) {
                        List<Message> messages = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Message message = document.toObject(Message.class);
                            messages.add(message);
                        }

                        if (messages.isEmpty()) {
                            showError();
                        } else {
                            showMessages(messages);
                        }
                    } else {
                        showError();
                        Log.e("WaterAuthorityMessages", "Error getting messages", task.getException());
                    }
                });
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        messagesRecyclerView.setVisibility(View.GONE);
        errorMessageTextView.setVisibility(View.GONE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    private void showError() {
        errorMessageTextView.setVisibility(View.VISIBLE);
        messagesRecyclerView.setVisibility(View.GONE);
    }

    private void showMessages(List<Message> messages) {
        messagesRecyclerView.setVisibility(View.VISIBLE);
        errorMessageTextView.setVisibility(View.GONE);
        messagesAdapter.updateMessages(messages);
    }
}