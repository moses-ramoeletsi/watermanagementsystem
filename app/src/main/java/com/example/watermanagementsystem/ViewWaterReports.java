package com.example.watermanagementsystem;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class ViewWaterReports extends AppCompatActivity {

    private RecyclerView recyclerView, sourceRecylcerView;
    private ViewRportedIssuesAdapter adapter;
    private ViewWaterSourceAdapter sourcesAdapter;
    private FirebaseFirestore db;
    private View progressBar;
    private TextView issueCountTextView, sourceCountTextView;

    private FirebaseAuth auth;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_view_water_reports);

        recyclerView = findViewById (R.id.waterIssuesRecycler);
        sourceRecylcerView = findViewById (R.id.waterSourcesRecycler);
        progressBar = findViewById (R.id.progressBar);
        issueCountTextView = findViewById (R.id.issueCountTextView);
        sourceCountTextView = findViewById (R.id.sourceCountTextView);

        db = FirebaseFirestore.getInstance ();
        auth = FirebaseAuth.getInstance ();

        adapter = new ViewRportedIssuesAdapter ();

        LinearLayoutManager issuesLayoutManager = new LinearLayoutManager (this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager (issuesLayoutManager);
        recyclerView.setAdapter (adapter);

        sourcesAdapter = new ViewWaterSourceAdapter ();

        LinearLayoutManager sourcesLayoutManager = new LinearLayoutManager (this, LinearLayoutManager.HORIZONTAL, false);
        sourceRecylcerView.setLayoutManager (sourcesLayoutManager);
        sourceRecylcerView.setAdapter (sourcesAdapter);
        loadWaterIssues ();
        loadWaterSources ();
    }

    private void loadWaterSources () {
        progressBar.setVisibility (View.VISIBLE);
        db.collection ("waterSources")
                .addSnapshotListener ((value, error) -> {
                    progressBar.setVisibility (View.GONE);

                    if ( error != null ) {
                        Toast.makeText (this, "Error loading water sources: " + error.getMessage (),
                                Toast.LENGTH_LONG).show ();
                        return;
                    }

                    if ( value != null ) {
                        ArrayList<WaterSource> sources = new ArrayList<> ();
                        for ( com.google.firebase.firestore.DocumentSnapshot doc : value.getDocuments () ) {
                            WaterSource source = doc.toObject (WaterSource.class);
                            if ( source != null ) {
                                sources.add (source);
                            }
                        }
                        sourcesAdapter.updateWaterSources (sources);
                        updateScourceCountText (sources.size ());
                    }
                });
    }

    private void loadWaterIssues () {
        progressBar.setVisibility (View.VISIBLE);
        db.collection ("waterIssues")
                .orderBy ("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener ((value, error) -> {
                    progressBar.setVisibility (View.GONE);

                    if ( error != null ) {
                        Toast.makeText (this, "Error loading issues: " + error.getMessage (),
                                Toast.LENGTH_LONG).show ();
                        return;
                    }

                    if ( value != null ) {
                        ArrayList<WaterIssue> issues = new ArrayList<> ();
                        for ( com.google.firebase.firestore.DocumentSnapshot doc : value.getDocuments () ) {
                            WaterIssue issue = doc.toObject (WaterIssue.class);
                            if ( issue != null ) {
                                issue.setId (doc.getId ());
                                issues.add (issue);
                            }
                        }
                        adapter.updateIssues (issues);
                        updateIssueCountText (issues.size ());
                    }
                });
    }

    private void updateIssueCountText (int count) {
        issueCountTextView.setText (String.format ("%d Issues", count));
    }

    private void updateScourceCountText (int count) {
        sourceCountTextView.setText (String.format ("%d Water Scource", count));
    }
}