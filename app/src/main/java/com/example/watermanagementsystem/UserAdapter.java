package com.example.watermanagementsystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<UserModel> users = new ArrayList<> ();

    public void setUsers (List<UserModel> users) {
        this.users = users;
        notifyDataSetChanged ();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from (parent.getContext ())
                .inflate (R.layout.item_user, parent, false);
        return new UserViewHolder (view);

    }

    @Override
    public void onBindViewHolder (@NonNull UserViewHolder holder, int position) {
        UserModel user = users.get (position);
        holder.bind (user);
    }

    @Override
    public int getItemCount () {
        return users.size ();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText;
        private TextView nationalIdText;
        private TextView phoneText;
        private TextView addressText;

        public UserViewHolder (@NonNull View itemView) {
            super (itemView);
            nameText = itemView.findViewById (R.id.user_name);
            nationalIdText = itemView.findViewById (R.id.user_national_id);
            phoneText = itemView.findViewById (R.id.user_phone_number);
            addressText = itemView.findViewById (R.id.user_address);

        }

        public void bind (UserModel user) {
            nameText.setText (user.getName ());
            nationalIdText.setText (user.getNationalId ());
            phoneText.setText (user.getContacts ());
            addressText.setText (user.getResidentialAddress ());

        }
    }
}