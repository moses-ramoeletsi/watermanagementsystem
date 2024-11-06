package com.example.watermanagementsystem;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ReportedWaterIssues extends AppCompatActivity {
    private RecyclerView recyclerView;
    private WaterIssuesAdapter adapter;
    private FirebaseFirestore db;
    private View progressBar;

    private FirebaseAuth auth;
    private String currentUserEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reported_water_issues);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);


        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();


        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            currentUserEmail = currentUser.getEmail();
        }

        adapter = new WaterIssuesAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadWaterIssues();
    }

    private void loadWaterIssues() {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("waterIssues")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    progressBar.setVisibility(View.GONE);

                    if (error != null) {
                        Toast.makeText(this, "Error loading issues: " + error.getMessage(),
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (value != null) {
                        ArrayList<WaterIssue> issues = new ArrayList<>();
                        for (com.google.firebase.firestore.DocumentSnapshot doc : value.getDocuments()) {
                            WaterIssue issue = doc.toObject(WaterIssue.class);
                            if (issue != null) {
                                issue.setId(doc.getId());
                                issues.add(issue);
                            }
                        }
                        adapter.updateIssues(issues);
                    }
                });
    }

    public void updateIssueStatus(String reportId, String newStatus) {
        if (currentUserEmail == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        db.collection("waterIssues").document(reportId)
                .update("status", newStatus, "lastUpdatedBy", currentUserEmail)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Status updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to update status: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}