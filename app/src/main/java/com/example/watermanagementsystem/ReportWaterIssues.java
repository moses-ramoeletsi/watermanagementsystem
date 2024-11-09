package com.example.watermanagementsystem;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class ReportWaterIssues extends AppCompatActivity {

    private Spinner issueTypeSpinner;
    private EditText locationInput;
    private EditText detailsInput;
    private Button submitButton;
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_report_water_issues);

        db = FirebaseFirestore.getInstance ();
        auth = FirebaseAuth.getInstance ();

        initializeViews ();
        setupClickListeners ();
    }

    private void initializeViews () {
        issueTypeSpinner = findViewById (R.id.issueTypeSpinner);
        locationInput = findViewById (R.id.locationInput);
        detailsInput = findViewById (R.id.detailsInput);
        submitButton = findViewById (R.id.submitButton);
        progressBar = findViewById (R.id.progressBar);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource (
                this,
                R.array.issue_types,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);
        issueTypeSpinner.setAdapter (adapter);
    }

    private void setupClickListeners () {
        submitButton.setOnClickListener (v -> {
            if ( validateForm () ) {
                submitReport ();
            }
        });
    }

    private boolean validateForm () {
        if ( issueTypeSpinner.getSelectedItemPosition () == Spinner.INVALID_POSITION ) {
            showSnackbar ("Please select an issue type");
            return false;
        }
        if ( locationInput.getText ().toString ().trim ().isEmpty () ) {
            showSnackbar ("Please enter the location");
            return false;
        }
        if ( detailsInput.getText ().toString ().trim ().isEmpty () ) {
            showSnackbar ("Please provide issue details");
            return false;
        }
        return true;
    }

    private void submitReport () {
        progressBar.setVisibility (View.VISIBLE);
        submitButton.setEnabled (false);

        FirebaseUser currentUser = auth.getCurrentUser ();
        if ( currentUser == null ) {
            showSnackbar ("Please log in to submit a report");
            progressBar.setVisibility (View.GONE);
            submitButton.setEnabled (true);
            return;
        }

        String reportId = UUID.randomUUID ().toString ();
        createReport (reportId);
    }

    private Map<String, Object> createReportData (String reportId, FirebaseUser currentUser) {
        Map<String, Object> userInfo = new HashMap<> ();
        userInfo.put ("email", currentUser.getEmail ());
        userInfo.put ("uid", currentUser.getUid ());
        userInfo.put ("phoneNumber", currentUser.getPhoneNumber () != null ?
                currentUser.getPhoneNumber () : "Not provided");

        Map<String, Object> report = new HashMap<> ();
        report.put ("reportId", reportId);
        report.put ("issueType", issueTypeSpinner.getSelectedItem ().toString ());
        report.put ("location", locationInput.getText ().toString ());
        report.put ("details", detailsInput.getText ().toString ());

        long currentTimeMillis = System.currentTimeMillis ();
        report.put ("timestamp", new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault ()).format (new Date (currentTimeMillis)));  // Human readable format

        report.put ("status", "Pending");
        report.put ("userInfo", userInfo);

        return report;
    }

    private void createReport (String reportId) {
        FirebaseUser currentUser = auth.getCurrentUser ();
        Map<String, Object> report = createReportData (reportId, currentUser);

        db.collection ("waterIssues")
                .document (reportId)
                .set (report)
                .addOnSuccessListener (aVoid -> {
                    progressBar.setVisibility (View.GONE);
                    showSnackbar ("Report submitted successfully");
                    clearForm ();
                    finish ();
                })
                .addOnFailureListener (e -> {
                    progressBar.setVisibility (View.GONE);
                    submitButton.setEnabled (true);
                    showSnackbar ("Failed to submit report: " + e.getMessage ());
                });
    }

    private void clearForm () {
        issueTypeSpinner.setSelection (0);
        locationInput.getText ().clear ();
        detailsInput.getText ().clear ();
    }

    private void showSnackbar (String message) {
        Snackbar.make (findViewById (android.R.id.content), message, Snackbar.LENGTH_LONG).show ();
    }
}
