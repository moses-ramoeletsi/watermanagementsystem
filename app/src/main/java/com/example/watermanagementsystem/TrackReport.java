package com.example.watermanagementsystem;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TrackReport extends AppCompatActivity {

    private RecyclerView reportsRecyclerView;
    private TextView noReportsText;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ReportsAdapter adapter;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_track_report);

        db = FirebaseFirestore.getInstance ();
        mAuth = FirebaseAuth.getInstance ();

        reportsRecyclerView = findViewById (R.id.reportsRecyclerView);
        noReportsText = findViewById (R.id.noReportsText);
        progressBar = findViewById (R.id.progressBar);

        reportsRecyclerView.setLayoutManager (new LinearLayoutManager (this));
        adapter = new ReportsAdapter ();
        reportsRecyclerView.setAdapter (adapter);


        loadReports ();

    }

    private void loadReports () {
        String currentUserId = mAuth.getCurrentUser ().getUid ();
        progressBar.setVisibility (View.VISIBLE);

        db.collection ("waterIssues")
                .whereEqualTo ("userInfo.uid", currentUserId)
                .addSnapshotListener ((value, error) -> {
                    progressBar.setVisibility (View.GONE);

                    if ( error != null ) {
                        Toast.makeText (this, "Error loading reports: " + error.getMessage (),
                                Toast.LENGTH_SHORT).show ();
                        return;
                    }

                    if ( value == null || value.isEmpty () ) {
                        noReportsText.setVisibility (View.VISIBLE);
                        reportsRecyclerView.setVisibility (View.GONE);
                        return;
                    }

                    List<WaterIssue> reports = new ArrayList<> ();
                    for ( DocumentSnapshot doc : value.getDocuments () ) {
                        WaterIssue issue = doc.toObject (WaterIssue.class);
                        if ( issue != null ) {
                            issue.setId (doc.getId ());
                            reports.add (issue);
                        }
                    }

                    noReportsText.setVisibility (reports.isEmpty () ? View.VISIBLE : View.GONE);
                    reportsRecyclerView.setVisibility (reports.isEmpty () ? View.GONE : View.VISIBLE);
                    adapter.setReports (reports);
                });
    }

}