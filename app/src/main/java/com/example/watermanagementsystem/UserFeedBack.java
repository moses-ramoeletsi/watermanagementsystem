package com.example.watermanagementsystem;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserFeedBack extends AppCompatActivity implements FeedbackAdapter.FeedbackActionsListener {

    private EditText emailInput, contactsInput, detailsInput;
    private Spinner feedbackSpinner;
    private Button submitButton;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private RecyclerView feedbackRecyclerView;
    private FeedbackAdapter feedbackAdapter;
    private List<FeedBack> feedbackList;
    private CollectionReference feedbackRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_feed_back);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        feedbackList = new ArrayList<>();

        emailInput = findViewById(R.id.emailInput);
        contactsInput = findViewById(R.id.contactsInput);
        feedbackSpinner = findViewById(R.id.feedbackSpinner);
        detailsInput = findViewById(R.id.detailsInput);
        submitButton = findViewById(R.id.submitButton);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource (this, R.array.feedback_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);
        feedbackSpinner.setAdapter (adapter);

        feedbackRecyclerView = findViewById(R.id.recyclerViewFeedback);
        feedbackRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        feedbackAdapter = new FeedbackAdapter(feedbackList, (FeedbackAdapter.FeedbackActionsListener) this);
        feedbackRecyclerView.setAdapter(feedbackAdapter);

        feedbackRef = db.collection("feedbacks");
        loadUserFeedback();
        populateUserInfo();

        submitButton.setOnClickListener(v -> submitFeedback());

    }

    private void loadUserFeedback() {
        String userId = auth.getCurrentUser().getUid();
        feedbackRef.whereEqualTo("userId", userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        feedbackList.clear();
                        for (DocumentSnapshot doc : task.getResult()) {
                            FeedBack feedback = doc.toObject(FeedBack.class);
                            feedbackList.add(feedback);
                        }
                        feedbackAdapter.setFeedbackList(feedbackList);
                    } else {
                        Toast.makeText(UserFeedBack.this, "Failed to load feedback", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void populateUserInfo() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            emailInput.setText(currentUser.getEmail());


            DocumentReference userRef = db.collection("users").document(currentUser.getUid());
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String contacts = documentSnapshot.getString("contacts");
                    String email = documentSnapshot.getString("email");

                    if (contacts != null) contactsInput.setText(contacts);
                    if (email != null) emailInput.setText(email);

                } else {
                    Log.e("UserFeedBack", "No user document found in Firestore");
                }
            }).addOnFailureListener(e -> Log.e("UserFeedBack", "Error fetching user data", e));
        }
    }
    private void submitFeedback() {
        String email = emailInput.getText() != null ? emailInput.getText().toString().trim() : "";
        String contacts = contactsInput.getText() != null ? contactsInput.getText().toString().trim() : "";
        String details = detailsInput.getText() != null ? detailsInput.getText().toString().trim() : "";
        String feedbackType = (feedbackSpinner.getSelectedItem() != null) ? feedbackSpinner.getSelectedItem().toString() : "";

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "User is not logged in", Toast.LENGTH_SHORT).show();
            return;
        }


        String feedbackId = db.collection("feedbacks").document().getId();

        Map<String, Object> feedbackData = new HashMap<>();
        feedbackData.put("userEmail", email);
        feedbackData.put("userContacts", contacts);
        feedbackData.put("userId", auth.getCurrentUser().getUid());
        feedbackData.put("feedbackId", feedbackId);
        feedbackData.put("feedbackType", feedbackType);
        feedbackData.put("details", details);


        db.collection("feedbacks").document(feedbackId)
                .set(feedbackData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Feedback submitted successfully!", Toast.LENGTH_SHORT).show();
                    detailsInput.setText("");
                    loadUserFeedback();
                })
                .addOnFailureListener(e -> {
                    Log.e("UserFeedBack", "Error submitting feedback", e);
                    Toast.makeText(this, "Failed to submit feedback", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onEdit(FeedBack feedback) {
        showEditDialog(feedback);
    }



    private void showEditDialog(FeedBack feedback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_feedback, null);
        builder.setView(dialogView);

        Spinner editFeedbackSpinner = dialogView.findViewById(R.id.editFeedbackSpinner);
        EditText editDetailsInput = dialogView.findViewById(R.id.editDetailsInput);

        String[] feedbackTypes = {"Suggestion", "Issue"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, feedbackTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editFeedbackSpinner.setAdapter(adapter);


        if (feedback.getFeedbackType() != null) {
            int spinnerPosition = adapter.getPosition(feedback.getFeedbackType());
            if (spinnerPosition >= 0) {
                editFeedbackSpinner.setSelection(spinnerPosition);
            }
        }

        if (feedback.getDetails() != null) {
            editDetailsInput.setText(feedback.getDetails());
        } else {
            editDetailsInput.setHint("Enter details");
        }

        builder.setPositiveButton("Save", (dialog, which) -> {
            String updatedType = editFeedbackSpinner.getSelectedItem().toString();
            String updatedDetails = editDetailsInput.getText().toString();

            if (TextUtils.isEmpty(updatedDetails)) {
                Toast.makeText(UserFeedBack.this, "Details cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            DocumentReference feedbackDocRef = feedbackRef.document(feedback.getFeedbackId());
            feedbackDocRef.update("feedbackType", updatedType, "details", updatedDetails)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(UserFeedBack.this, "Feedback updated", Toast.LENGTH_SHORT).show();
                        loadUserFeedback();
                    })
                    .addOnFailureListener(e -> Toast.makeText(UserFeedBack.this, "Update failed", Toast.LENGTH_SHORT).show());
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onDelete(String feedbackId) {
        feedbackRef.document(feedbackId).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(UserFeedBack.this, "Feedback deleted", Toast.LENGTH_SHORT).show();
                    loadUserFeedback();
                })
                .addOnFailureListener(e -> Toast.makeText(UserFeedBack.this, "Delete failed", Toast.LENGTH_SHORT).show());
    }

    private int getSpinnerIndex(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(value)) {
                return i;
            }
        }
        return 0;
    }

}