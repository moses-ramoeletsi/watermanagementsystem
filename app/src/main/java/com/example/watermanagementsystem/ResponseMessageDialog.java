package com.example.watermanagementsystem;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ResponseMessageDialog extends DialogFragment {
    private final Message message;
    private EditText responseEditText;

    public ResponseMessageDialog (Message message) {
        this.message = message;
    }

    @Override
    public Dialog onCreateDialog (Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder (requireActivity ());
        LayoutInflater inflater = requireActivity ().getLayoutInflater ();
        View view = inflater.inflate (R.layout.dialog_response_message, null);

        responseEditText = view.findViewById (R.id.responseEditText);

        builder.setView (view)
                .setTitle ("Respond to Message")
                .setPositiveButton ("Send", (dialog, id) -> {
                    String response = responseEditText.getText ().toString ().trim ();
                    if ( ! response.isEmpty () ) {
                        updateMessageWithResponse (response);
                    }
                })
                .setNegativeButton ("Cancel", (dialog, id) -> {
                    if ( dialog != null ) {
                        dialog.dismiss ();
                    }
                });

        return builder.create ();
    }

    private void updateMessageWithResponse (String response) {
        FirebaseFirestore db = FirebaseFirestore.getInstance ();
        String currentUserId = FirebaseAuth.getInstance ().getCurrentUser ().getUid ();

        db.collection ("messages")
                .document (message.getMessageId ())
                .update (
                        "response", response,
                        "responseAuthorId", currentUserId,
                        "responseTimestamp", Timestamp.now ()
                )
                .addOnSuccessListener (aVoid -> {
                    // Optionally show success message
                    if ( getActivity () != null ) {
                        ( (WaterAuthorityMessages) getActivity () ).loadMessages ();
                    }
                })
                .addOnFailureListener (e -> {
                    // Handle the error appropriately
                });
    }
}