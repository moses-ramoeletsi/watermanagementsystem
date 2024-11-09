package com.example.watermanagementsystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {
    private final Context context;
    private List<Message> messages;
    private final OnMessageActionListener listener;
    private final boolean isAuthority;
    private final FirebaseAuth auth;

    public MessagesAdapter (Context context, List<Message> messages, OnMessageActionListener listener, boolean isAuthority) {
        this.context = context;
        this.messages = messages;
        this.listener = listener;
        this.isAuthority = isAuthority;
        this.auth = FirebaseAuth.getInstance ();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from (context).inflate (R.layout.message_item, parent, false);
        return new MessageViewHolder (view);
    }

    @Override
    public void onBindViewHolder (@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get (position);
        String currentUserId = auth.getCurrentUser () != null ? auth.getCurrentUser ().getUid () : "";

        // Set basic message information
        holder.subjectTextView.setText (message.getSubject ());
        holder.contentTextView.setText (message.getContent ());
        holder.senderNameTextView.setText ("From: " + message.getSenderName ());
        holder.contactsTextView.setText ("Contacts: " + message.getSenderContacts ());

        // Handle response section
        if ( message.getResponse () != null ) {
            holder.responseTextView.setVisibility (View.VISIBLE);
            holder.responseTextView.setText ("Response: " + message.getResponse ());
        } else {
            holder.responseTextView.setVisibility (View.GONE);
        }

        if ( isAuthority ) {
            if ( message.getResponse () == null ) {

                holder.responseButton.setVisibility (View.VISIBLE);
                holder.editButton.setVisibility (View.GONE);
                holder.deleteButton.setVisibility (View.GONE);
            } else if ( message.getResponseAuthorId () != null &&
                    message.getResponseAuthorId ().equals (currentUserId) ) {
                holder.responseButton.setVisibility (View.GONE);
                holder.editButton.setVisibility (View.VISIBLE);
                holder.deleteButton.setVisibility (View.VISIBLE);
            } else {
                // Hide all buttons for responses by other authorities
                holder.responseButton.setVisibility (View.GONE);
                holder.editButton.setVisibility (View.GONE);
                holder.deleteButton.setVisibility (View.GONE);
            }
        } else {

            boolean isOwner = message.getSenderId ().equals (currentUserId);
            holder.editButton.setVisibility (isOwner ? View.VISIBLE : View.GONE);
            holder.deleteButton.setVisibility (isOwner ? View.VISIBLE : View.GONE);
            holder.responseButton.setVisibility (View.GONE);
        }


        holder.editButton.setOnClickListener (v -> {
            if ( listener != null ) listener.onEditMessage (message);
        });

        holder.deleteButton.setOnClickListener (v -> {
            if ( listener != null ) listener.onDeleteMessage (message);
        });

        holder.responseButton.setOnClickListener (v -> {
            if ( listener != null ) listener.onRespondToMessage (message);
        });
    }

    @Override
    public int getItemCount () {
        return messages.size ();
    }

    public void updateMessages (List<Message> newMessages) {
        this.messages = newMessages;
        notifyDataSetChanged ();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView senderNameTextView;
        TextView subjectTextView;
        TextView contentTextView;
        TextView contactsTextView;
        TextView responseTextView;
        Button editButton;
        Button deleteButton;
        Button responseButton;

        MessageViewHolder (View itemView) {
            super (itemView);
            senderNameTextView = itemView.findViewById (R.id.senderNameId);
            subjectTextView = itemView.findViewById (R.id.subjectTextView);
            contentTextView = itemView.findViewById (R.id.messageTextView);
            contactsTextView = itemView.findViewById (R.id.contactsTextView);
            responseTextView = itemView.findViewById (R.id.responseTextView);
            editButton = itemView.findViewById (R.id.editButton);
            deleteButton = itemView.findViewById (R.id.deleteButton);
            responseButton = itemView.findViewById (R.id.responseButton);
        }
    }

    public interface OnMessageActionListener {
        void onEditMessage (Message message);

        void onDeleteMessage (Message message);

        void onRespondToMessage (Message message);
    }
}