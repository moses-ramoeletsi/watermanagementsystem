//package com.example.watermanagementsystem;
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.Query;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//
//public class SentMessagesActivity extends AppCompatActivity implements MessagesAdapter.OnMessageActionListener {
//    private RecyclerView recyclerView;
//    private MessagesAdapter adapter;
//    private TextView errorMessageTextView;
//    private ProgressBar progressBar;
//    private FirebaseFirestore db;
//    private FirebaseAuth auth;
//    private String authorityId;
//
//    @Override
//    protected void onCreate (Bundle savedInstanceState) {
//        super.onCreate (savedInstanceState);
//        setContentView (R.layout.activity_sent_messages);
//
//        authorityId = getIntent ().getStringExtra ("authority_id");
//        if ( authorityId == null ) {
//            showToast ("Error: Authority not found");
//            finish ();
//            return;
//        }
//
//        setTitle ("Messages with Authority");
//
//        db = FirebaseFirestore.getInstance ();
//        auth = FirebaseAuth.getInstance ();
//
//        recyclerView = findViewById (R.id.messagesRecyclerView);
//        errorMessageTextView = findViewById (R.id.errorMessageTextView);
//        progressBar = findViewById (R.id.progressBar);
//
//        recyclerView.setLayoutManager (new LinearLayoutManager (this));
//        adapter = new MessagesAdapter (this, new ArrayList<> (), this, false);
//        recyclerView.setAdapter (adapter);
//
//        loadMessages ();
//    }
//
//    private void loadMessages () {
//        if ( auth.getCurrentUser () == null ) return;
//
//        showProgress ();
//        db.collection ("messages")
//                .whereEqualTo ("senderId", auth.getCurrentUser ().getUid ())
//                .whereEqualTo ("recipientId", authorityId)
//                .addSnapshotListener ((value, error) -> {
//                    hideProgress ();
//                    if ( error != null ) {
//                        showError ();
//                        return;
//                    }
//
//                    List<Message> messages = new ArrayList<> ();
//                    if ( value != null ) {
//                        for ( com.google.firebase.firestore.QueryDocumentSnapshot doc : value ) {
//                            Message message = doc.toObject (Message.class);
//                            message.setMessageId (doc.getId ());
//                            messages.add (message);
//                        }
//                    }
//
//                    if ( messages.isEmpty () ) {
//                        showError ();
//                        errorMessageTextView.setText ("No messages with this authority");
//                    } else {
//                        showMessages (messages);
//                    }
//                });
//    }
//
//    @Override
//    public void onEditMessage (Message message) {
//        EditMessageDialog dialog = new EditMessageDialog (message);
//        dialog.show (getSupportFragmentManager (), "EditMessageDialog");
//    }
//
//    @Override
//    public void onDeleteMessage (Message message) {
//        if ( ! message.getRecipientId ().equals (authorityId) ) {
//            showToast ("Cannot delete messages from other authorities");
//            return;
//        }
//
//        db.collection ("messages")
//                .document (message.getMessageId ())
//                .delete ()
//                .addOnSuccessListener (aVoid -> showToast ("Message deleted successfully"))
//                .addOnFailureListener (e -> showToast ("Failed to delete message"));
//    }
//
//    @Override
//    public void onRespondToMessage (Message message) {
//
//    }
//
//    private void showProgress () {
//        progressBar.setVisibility (View.VISIBLE);
//        recyclerView.setVisibility (View.GONE);
//        errorMessageTextView.setVisibility (View.GONE);
//    }
//
//    private void hideProgress () {
//        progressBar.setVisibility (View.GONE);
//    }
//
//    private void showError () {
//        errorMessageTextView.setVisibility (View.VISIBLE);
//        recyclerView.setVisibility (View.GONE);
//    }
//
//    private void showMessages (List<Message> messages) {
//        recyclerView.setVisibility (View.VISIBLE);
//        errorMessageTextView.setVisibility (View.GONE);
//        adapter.updateMessages (messages);
//    }
//
//    private void showToast (String message) {
//        Toast.makeText (this, message, Toast.LENGTH_SHORT).show ();
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

import java.util.ArrayList;
import java.util.List;

public class SentMessagesActivity extends AppCompatActivity implements MessagesAdapter.OnMessageActionListener {
    private RecyclerView recyclerView;
    private MessagesAdapter adapter;
    private TextView errorMessageTextView;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String authorityId;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_sent_messages);

        authorityId = getIntent ().getStringExtra ("authority_id");
        if ( authorityId == null ) {
            showToast ("Error: Authority not found");
            finish ();
            return;
        }

        db = FirebaseFirestore.getInstance ();
        auth = FirebaseAuth.getInstance ();

        recyclerView = findViewById (R.id.messagesRecyclerView);
        errorMessageTextView = findViewById (R.id.errorMessageTextView);
        progressBar = findViewById (R.id.progressBar);

        recyclerView.setLayoutManager (new LinearLayoutManager (this));
        adapter = new MessagesAdapter (this, new ArrayList<> (), this, false); // false for user view
        recyclerView.setAdapter (adapter);

        loadMessages ();
    }

    void loadMessages () {
        if ( auth.getCurrentUser () == null ) return;

        showProgress ();
        String currentUserId = auth.getCurrentUser ().getUid ();

        db.collection ("messages")
                .whereEqualTo ("senderId", currentUserId)
                .whereEqualTo ("recipientId", authorityId)
                .addSnapshotListener ((value, error) -> {
                    hideProgress ();
                    if ( error != null ) {
                        showError ("Error loading messages");
                        return;
                    }

                    List<Message> messages = new ArrayList<> ();
                    if ( value != null ) {
                        for ( com.google.firebase.firestore.QueryDocumentSnapshot doc : value ) {
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
        if ( ! message.getSenderId ().equals (auth.getCurrentUser ().getUid ()) ) {
            showToast ("You can only edit your own messages");
            return;
        }

        EditMessageDialog dialog = new EditMessageDialog (message, false); // false for editing message
        dialog.show (getSupportFragmentManager (), "EditMessageDialog");
    }

    @Override
    public void onDeleteMessage (Message message) {
        if ( ! message.getSenderId ().equals (auth.getCurrentUser ().getUid ()) ) {
            showToast ("You can only delete your own messages");
            return;
        }

        db.collection ("messages")
                .document (message.getMessageId ())
                .delete ()
                .addOnSuccessListener (aVoid -> showToast ("Message deleted successfully"))
                .addOnFailureListener (e -> showToast ("Failed to delete message"));
    }

    @Override
    public void onRespondToMessage (Message message) {
        // Users cannot respond to messages, so this method is empty
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