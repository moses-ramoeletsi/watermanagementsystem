package com.example.watermanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewUserFeedBack extends AppCompatActivity implements FeedbackAdapter.FeedbackActionsListener {

    private RecyclerView recyclerView;
    private FeedbackAdapter adapter;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private List<FeedBack> feedbackList;
    private boolean isAdmin = false;

    private interface OnAdminCheckComplete {
        void onResult (boolean isAdmin);
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_view_user_feed_back);

        initializeFirebase ();
        initializeViews ();
        setupRecyclerView ();

        checkIfUserIsAdmin (adminStatus -> {
            isAdmin = adminStatus;
            adapter = new FeedbackAdapter (feedbackList, this, isAdmin);
            recyclerView.setAdapter (adapter);
            loadFeedbacks ();
        });
    }

    private void initializeFirebase () {
        db = FirebaseFirestore.getInstance ();
    }

    private void initializeViews () {
        recyclerView = findViewById (R.id.recyclerViewFeedback);
        progressBar = findViewById (R.id.progressBar);
    }

    private void setupRecyclerView () {
        recyclerView.setLayoutManager (new LinearLayoutManager (this));
        feedbackList = new ArrayList<> ();
    }

    private void checkIfUserIsAdmin (OnAdminCheckComplete callback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance ().getCurrentUser ();
        if ( currentUser != null ) {
            db.collection ("users")
                    .document (currentUser.getUid ())
                    .get ()
                    .addOnSuccessListener (documentSnapshot -> {
                        if ( documentSnapshot.exists () ) {
                            String role = documentSnapshot.getString ("role");
                            boolean isAdmin = "admin".equals (role);
                            callback.onResult (isAdmin);
                        } else {
                            callback.onResult (false);
                        }
                    })
                    .addOnFailureListener (e -> callback.onResult (false));
        } else {
            callback.onResult (false);
        }
    }

    private void loadFeedbacks () {
        progressBar.setVisibility (View.VISIBLE);

        db.collection ("feedbacks")
                .get ()
                .addOnSuccessListener (queryDocumentSnapshots -> {
                    feedbackList.clear ();
                    for ( QueryDocumentSnapshot document : queryDocumentSnapshots ) {
                        FeedBack feedback = document.toObject (FeedBack.class);
                        feedback.setFeedbackId (document.getId ()); // Ensure feedback ID is set
                        feedbackList.add (feedback);
                    }
                    if ( feedbackList.isEmpty () ) {
                        Toast.makeText (ViewUserFeedBack.this, "No feedbacks available", Toast.LENGTH_SHORT).show ();
                        Intent intent = new Intent (this, AdminPage.class);
                        startActivity (intent);
                    }
                    adapter.setFeedbackList (feedbackList);
                    progressBar.setVisibility (View.GONE);
                })
                .addOnFailureListener (e -> {
                    Toast.makeText (ViewUserFeedBack.this,
                            "Error loading feedbacks: " + e.getMessage (),
                            Toast.LENGTH_SHORT).show ();
                    progressBar.setVisibility (View.GONE);
                });
    }

    @Override
    public void onEdit (FeedBack feedback) {

    }

    @Override
    public void onDelete (String feedbackId) {
    }

    @Override
    public void onRespond (String feedbackId, String response) {
        db.collection ("feedbacks").document (feedbackId)
                .update (
                        "adminResponse", response
                )
                .addOnSuccessListener (aVoid -> {
                    Toast.makeText (ViewUserFeedBack.this,
                            "Response added successfully",
                            Toast.LENGTH_SHORT).show ();
                    loadFeedbacks ();
                })
                .addOnFailureListener (e -> Toast.makeText (ViewUserFeedBack.this,
                        "Error adding response: " + e.getMessage (),
                        Toast.LENGTH_SHORT).show ());
    }

    @Override
    public void onEditResponse (String feedbackId, String newResponse) {
        db.collection ("feedbacks").document (feedbackId)
                .update (
                        "adminResponse", newResponse
                )
                .addOnSuccessListener (aVoid -> {
                    Toast.makeText (ViewUserFeedBack.this,
                            "Response updated successfully",
                            Toast.LENGTH_SHORT).show ();
                    loadFeedbacks ();
                })
                .addOnFailureListener (e -> Toast.makeText (ViewUserFeedBack.this,
                        "Error updating response: " + e.getMessage (),
                        Toast.LENGTH_SHORT).show ());
    }

    @Override
    public void onDeleteResponse (String feedbackId) {
        db.collection ("feedbacks").document (feedbackId)
                .update (
                        "adminResponse", null
                )
                .addOnSuccessListener (aVoid -> {
                    Toast.makeText (ViewUserFeedBack.this,
                            "Response deleted successfully",
                            Toast.LENGTH_SHORT).show ();
                    loadFeedbacks ();
                })
                .addOnFailureListener (e -> Toast.makeText (ViewUserFeedBack.this,
                        "Error deleting response: " + e.getMessage (),
                        Toast.LENGTH_SHORT).show ());
    }
}
