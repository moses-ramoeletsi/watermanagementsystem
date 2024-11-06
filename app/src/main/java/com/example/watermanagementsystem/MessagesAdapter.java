package com.example.watermanagementsystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {
    private final Context context;
    private List<Message> messages;
    private final OnMessageActionListener listener;

    public MessagesAdapter(Context context, List<Message> messages, OnMessageActionListener listener) {
        this.context = context;
        this.messages = messages;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_item, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.subjectTextView.setText(message.getSubject());
        holder.contentTextView.setText(message.getContent());
        if (message != null) {
            if (holder.editButton != null && listener != null) {
                holder.editButton.setOnClickListener(v -> listener.onEditMessage(message));
            }
            if (holder.deleteButton != null && listener != null) {
                holder.deleteButton.setOnClickListener(v -> listener.onDeleteMessage(message));
            }
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void updateMessages(List<Message> newMessages) {
        this.messages = newMessages;
        notifyDataSetChanged();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView subjectTextView;
        TextView contentTextView;
        ImageButton editButton;
        ImageButton deleteButton;

        MessageViewHolder(View itemView) {
            super(itemView);
            subjectTextView = itemView.findViewById(R.id.subjectTextView);
            contentTextView = itemView.findViewById(R.id.messageTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    public interface OnMessageActionListener {
        void onEditMessage(Message message);

        void onDeleteMessage(Message message);
    }
}