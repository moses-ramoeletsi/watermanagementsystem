package com.example.watermanagementsystem;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.FirebaseFirestore;

public class EditMessageDialog extends DialogFragment {
    private Message message;
    private FirebaseFirestore db;
    private Context activityContext;

    public EditMessageDialog(Message message) {
        this.message = message;
        this.db = FirebaseFirestore.getInstance();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activityContext = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_message, null);

        EditText subjectEditText = view.findViewById(R.id.editSubjectEditText);
        EditText messageEditText = view.findViewById(R.id.editMessageEditText);

        subjectEditText.setText(message.getSubject());
        messageEditText.setText(message.getContent());

        builder.setView(view)
                .setTitle("Edit Message")
                .setPositiveButton("Save", (dialog, id) -> {
                    String newSubject = subjectEditText.getText().toString();
                    String newMessage = messageEditText.getText().toString();

                    if (!newSubject.isEmpty() && !newMessage.isEmpty()) {
                        updateMessage(newSubject, newMessage);
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());

        return builder.create();
    }

    private void updateMessage(String newSubject, String newMessage) {
        // Store a final reference to the activity context
        final Context context = activityContext;

        db.collection("messages")
                .document(message.getMessageId())
                .update(
                        "subject", newSubject,
                        "content", newMessage
                )
                .addOnSuccessListener(aVoid -> {
                    if (context != null) {
                        Toast.makeText(context, "Message updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    if (context != null) {
                        Toast.makeText(context, "Failed to update message", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activityContext = null;
    }
}