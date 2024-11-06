package com.example.watermanagementsystem;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AuthoritiesAdapter extends RecyclerView.Adapter<AuthoritiesAdapter.AuthorityViewHolder> {
    private List<UserDetails> authorities;
    private Context context;
    private OnAuthorityClickListener listener;

    public interface OnAuthorityClickListener {
        void onAuthorityClick(UserDetails authority);
    }

    public AuthoritiesAdapter(Context context, List<UserDetails> authorities, OnAuthorityClickListener listener) {
        this.context = context;
        this.authorities = authorities;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AuthorityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.authority_item, parent, false);
        return new AuthorityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AuthorityViewHolder holder, int position) {
        UserDetails authority = authorities.get(position);
        holder.emailTextView.setText("Email: " + authority.getEmail());
        holder.addressTextView.setText("Address: " + authority.getPhysicalAddress());
        holder.contactTextView.setText("Contact: " + authority.getContacts());

        holder.contactButton.setOnClickListener(v -> listener.onAuthorityClick(authority));

        holder.viewMessagesButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, SentMessagesActivity.class);
            intent.putExtra("authority_id", authority.getUserId());
            context.startActivity(intent);
        });
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAuthorityClick(authority);
            }
        });
    }

    @Override
    public int getItemCount() {
        return authorities.size();
    }

    public void updateAuthorities(List<UserDetails> newAuthorities) {
        authorities = newAuthorities;
        notifyDataSetChanged();
    }

    static class AuthorityViewHolder extends RecyclerView.ViewHolder {
        TextView emailTextView;
        TextView addressTextView;
        TextView contactTextView;
        Button contactButton;
        Button viewMessagesButton;

        public AuthorityViewHolder(@NonNull View itemView) {
            super(itemView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            contactTextView = itemView.findViewById(R.id.contactTextView);
            contactButton = itemView.findViewById(R.id.contactButton);
            viewMessagesButton = itemView.findViewById(R.id.viewMessagesButton);

        }
    }
}
