//package com.example.watermanagementsystem;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
//public class WaterAuthorityMessages extends AppCompatActivity {
//    private RecyclerView messagesRecyclerView;
//    private MessagesAdapter messagesAdapter;
//    private TextView errorMessageTextView;
//    private ProgressBar progressBar;
//    private FirebaseFirestore db;
//
//    @Override
//    protected void onCreate (Bundle savedInstanceState) {
//        super.onCreate (savedInstanceState);
//        setContentView (R.layout.activity_water_authority_messages);
//
//        db = FirebaseFirestore.getInstance ();
//
//        messagesRecyclerView = findViewById (R.id.authoritiesRecyclerView);
//        errorMessageTextView = findViewById (R.id.errorMessageTextView);
//        progressBar = findViewById (R.id.progressBar);
//
//        messagesRecyclerView.setLayoutManager (new LinearLayoutManager (this));
//        messagesAdapter = new MessagesAdapter (this, new ArrayList<> (), this, true);
//        messagesRecyclerView.setAdapter (messagesAdapter);
//
//        loadMessages ();
//    }
//
//    private void loadMessages () {
//        showProgress ();
//
//        String currentUserId = FirebaseAuth.getInstance ().getCurrentUser ().getUid ();
//
//        db.collection ("messages")
//                .whereEqualTo ("recipientId", currentUserId)
//                .get ()
//                .addOnCompleteListener (task -> {
//                    hideProgress ();
//
//                    if ( task.isSuccessful () ) {
//                        List<Message> messages = new ArrayList<> ();
//                        for ( QueryDocumentSnapshot document : task.getResult () ) {
//                            Message message = document.toObject (Message.class);
//                            messages.add (message);
//                        }
//
//                        if ( messages.isEmpty () ) {
//                            showError ();
//                        } else {
//                            showMessages (messages);
//                        }
//                    } else {
//                        showError ();
//                        Log.e ("WaterAuthorityMessages", "Error getting messages", task.getException ());
//                    }
//                });
//    }
//
//    private void showProgress () {
//        progressBar.setVisibility (View.VISIBLE);
//        messagesRecyclerView.setVisibility (View.GONE);
//        errorMessageTextView.setVisibility (View.GONE);
//    }
//
//    private void hideProgress () {
//        progressBar.setVisibility (View.GONE);
//    }
//
//    private void showError () {
//        errorMessageTextView.setVisibility (View.VISIBLE);
//        messagesRecyclerView.setVisibility (View.GONE);
//    }
//
//    private void showMessages (List<Message> messages) {
//        messagesRecyclerView.setVisibility (View.VISIBLE);
//        errorMessageTextView.setVisibility (View.GONE);
//        messagesAdapter.updateMessages (messages);
//    }
//}

package com.example.watermanagementsystem;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class WaterAuthorityMessages extends AppCompatActivity implements MessagesAdapter.OnMessageActionListener {
    private RecyclerView recyclerView;
    private MessagesAdapter adapter;
    private TextView errorMessageTextView;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_water_authority_messages);

        db = FirebaseFirestore.getInstance ();
        auth = FirebaseAuth.getInstance ();

        recyclerView = findViewById (R.id.authoritiesRecyclerView);
        errorMessageTextView = findViewById (R.id.errorMessageTextView);
        progressBar = findViewById (R.id.progressBar);

        recyclerView.setLayoutManager (new LinearLayoutManager (this));
        adapter = new MessagesAdapter (this, new ArrayList<> (), this, true); // true for authority view
        recyclerView.setAdapter (adapter);

        loadMessages ();
    }

    public void loadMessages () {
        if ( auth.getCurrentUser () == null ) return;

        showProgress ();
        String currentUserId = auth.getCurrentUser ().getUid ();

        db.collection ("messages")
                .whereEqualTo ("recipientId", currentUserId)
                .addSnapshotListener ((value, error) -> {
                    hideProgress ();
                    if ( error != null ) {
                        showError ("Error loading messages");
                        return;
                    }

                    List<Message> messages = new ArrayList<> ();
                    if ( value != null ) {
                        for ( QueryDocumentSnapshot doc : value ) {
                            Message message = doc.toObject (Message.class);
                            message.setMessageId (doc.getId ());
                            messages.add (message);
                        }
                    }

                    if ( messages.isEmpty () ) {
                        showError ("No messages found");
                    } else {
                        showMessages (messages);
                    }
                });
    }

    @Override
    public void onEditMessage (Message message) {
        EditMessageDialog dialog = new EditMessageDialog (message, true); // true for editing response
        dialog.show (getSupportFragmentManager (), "EditMessageDialog");
    }

    @Override
    public void onDeleteMessage (Message message) {
        if ( ! message.getResponseAuthorId ().equals (auth.getCurrentUser ().getUid ()) ) {
            showToast ("You can only delete your own responses");
            return;
        }

        db.collection ("messages")
                .document (message.getMessageId ())
                .update ("response", null,
                        "responseAuthorId", null,
                        "responseTimestamp", null)
                .addOnSuccessListener (aVoid -> showToast ("Response deleted successfully"))
                .addOnFailureListener (e -> showToast ("Failed to delete response"));
    }

    @Override
    public void onRespondToMessage (Message message) {
        ResponseMessageDialog dialog = new ResponseMessageDialog (message);
        dialog.show (getSupportFragmentManager (), "ResponseMessageDialog");
    }

    private void showProgress () {
        progressBar.setVisibility (View.VISIBLE);
        recyclerView.setVisibility (View.GONE);
        errorMessageTextView.setVisibility (View.GONE);
    }

    private void hideProgress () {
        progressBar.setVisibility (View.GONE);
    }

    private void showError (String message) {
        errorMessageTextView.setText (message);
        errorMessageTextView.setVisibility (View.VISIBLE);
        recyclerView.setVisibility (View.GONE);
    }

    private void showMessages (List<Message> messages) {
        recyclerView.setVisibility (View.VISIBLE);
        errorMessageTextView.setVisibility (View.GONE);
        adapter.updateMessages (messages);
    }

    private void showToast (String message) {
        Toast.makeText (this, message, Toast.LENGTH_SHORT).show ();
    }
}