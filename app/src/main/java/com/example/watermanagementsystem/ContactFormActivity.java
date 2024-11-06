package com.example.watermanagementsystem;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ContactFormActivity extends AppCompatActivity {
    private EditText messageEditText;
    private EditText subjectEditText;
    private Button sendButton;
    private ProgressBar progressBar;
    private TextView authorityInfoText;
    private UserDetails selectedAuthority;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contact_form);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        messageEditText = findViewById(R.id.messageEditText);
        subjectEditText = findViewById(R.id.subjectEditText);
        sendButton = findViewById(R.id.sendButton);
        progressBar = findViewById(R.id.progressBar);
        authorityInfoText = findViewById(R.id.authorityInfoText);


        selectedAuthority = getIntent().getParcelableExtra("selected_authority");
        if (selectedAuthority != null) {
            authorityInfoText.setText("Sending message to: " + selectedAuthority.getEmail());
        }

        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        if (selectedAuthority == null || auth.getCurrentUser() == null) {
            Toast.makeText(this, "Error: Missing required information", Toast.LENGTH_SHORT).show();
            return;
        }

        String message = messageEditText.getText().toString().trim();
        String subject = subjectEditText.getText().toString().trim();

        if (message.isEmpty() || subject.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        sendButton.setEnabled(false);

        db.collection("users")
                .document(auth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String senderName = documentSnapshot.getString("name");
                    String senderContacts = documentSnapshot.getString("contacts");
                    Message contactMessage = new Message(
                            auth.getCurrentUser().getUid(),
                            senderName,
                            senderContacts,
                            selectedAuthority.getUserId(),
                            subject,
                            message
                    );

                    // Save to Firestore
                    db.collection("messages")
                            .add(contactMessage)
                            .addOnSuccessListener(documentReference -> {
                                // Set the messageId to the Firestore document ID
                                String documentId = documentReference.getId();
                                contactMessage.setMessageId(documentId);

                                // Update the document with the messageId
                                documentReference.update("messageId", documentId)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(ContactFormActivity.this,
                                                    "Message sent successfully", Toast.LENGTH_SHORT).show();
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(ContactFormActivity.this,
                                                    "Failed to update messageId: " + e.getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                            sendButton.setEnabled(true);
                                        });
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(ContactFormActivity.this,
                                        "Failed to send message: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                sendButton.setEnabled(true);
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to get user name: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    sendButton.setEnabled(true);
                });
    }
}