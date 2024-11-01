package com.example.watermanagementsystem;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class ReportWaterIssues extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private Spinner issueTypeSpinner;
    private EditText locationInput;
    private EditText detailsInput;
    private Button uploadPhotoButton;
    private ImageView photoPreview;
    private Button submitButton;
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report_water_issues);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        issueTypeSpinner = findViewById(R.id.issueTypeSpinner);
        locationInput = findViewById(R.id.locationInput);
        detailsInput = findViewById(R.id.detailsInput);
        uploadPhotoButton = findViewById(R.id.uploadPhotoButton);
        photoPreview = findViewById(R.id.photoPreview);
        submitButton = findViewById(R.id.submitButton);
        progressBar = findViewById(R.id.progressBar);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.issue_types,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        issueTypeSpinner.setAdapter(adapter);
    }

    private void setupClickListeners() {
        uploadPhotoButton.setOnClickListener(v -> openImagePicker());
        submitButton.setOnClickListener(v -> {
            if (validateForm()) {
                submitReport();
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            photoPreview.setImageURI(selectedImageUri);
            photoPreview.setVisibility(View.VISIBLE);
        }
    }

    private boolean validateForm() {
        if (issueTypeSpinner.getSelectedItemPosition() == 0) {
            showSnackbar("Please select an issue type");
            return false;
        }
        if (locationInput.getText().toString().trim().isEmpty()) {
            showSnackbar("Please enter the location");
            return false;
        }
        if (detailsInput.getText().toString().trim().isEmpty()) {
            showSnackbar("Please provide issue details");
            return false;
        }
        return true;
    }

    private void submitReport() {
        progressBar.setVisibility(View.VISIBLE);
        submitButton.setEnabled(false);

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            showSnackbar("Please log in to submit a report");
            progressBar.setVisibility(View.GONE);
            submitButton.setEnabled(true);
            return;
        }

        String reportId = UUID.randomUUID().toString();

        if (selectedImageUri != null) {
            uploadImageAndCreateReport(reportId);
        } else {
            createReport(reportId, null);
        }
    }

    private void uploadImageAndCreateReport(String reportId) {
        StorageReference imageRef = storage.getReference()
                .child("report_images/" + reportId + ".jpg");

        imageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> createReport(reportId, uri.toString()))
                            .addOnFailureListener(e -> {
                                progressBar.setVisibility(View.GONE);
                                submitButton.setEnabled(true);
                                showSnackbar("Failed to get image URL: " + e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    submitButton.setEnabled(true);
                    showSnackbar("Failed to upload image: " + e.getMessage());
                });
    }

    private Map<String, Object> createReportData(String reportId, String imageUrl, FirebaseUser currentUser) {

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("email", currentUser.getEmail());
        userInfo.put("uid", currentUser.getUid());
        userInfo.put("phoneNumber", currentUser.getPhoneNumber() != null ?
                currentUser.getPhoneNumber() : "Not provided");

        Map<String, Object> report = new HashMap<>();
        report.put("reportId", reportId);
        report.put("issueType", issueTypeSpinner.getSelectedItem().toString());
        report.put("location", locationInput.getText().toString());
        report.put("details", detailsInput.getText().toString());
        report.put("imageUrl", imageUrl);

        long currentTimeMillis = System.currentTimeMillis();
        report.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()).format(new Date(currentTimeMillis)));  // Human readable format

        report.put("status", "Pending");
        report.put("userInfo", userInfo);

        return report;
    }

    private void createReport(String reportId, String imageUrl) {
        FirebaseUser currentUser = auth.getCurrentUser();
        Map<String, Object> report = createReportData(reportId, imageUrl, currentUser);

        db.collection("waterIssues")
                .document(reportId)
                .set(report)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    showSnackbar("Report submitted successfully");
                    clearForm();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    submitButton.setEnabled(true);
                    showSnackbar("Failed to submit report: " + e.getMessage());
                });
    }

    private void clearForm() {
        issueTypeSpinner.setSelection(0);
        locationInput.getText().clear();
        detailsInput.getText().clear();
        selectedImageUri = null;
        photoPreview.setImageURI(null);
        photoPreview.setVisibility(View.GONE);
    }

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }
}