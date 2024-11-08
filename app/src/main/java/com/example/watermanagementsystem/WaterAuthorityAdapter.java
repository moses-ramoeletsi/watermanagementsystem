package com.example.watermanagementsystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class WaterAuthorityAdapter extends RecyclerView.Adapter<WaterAuthorityAdapter.AuthorityViewHolder> {
    private List<WaterAuthorityModel> authorities = new ArrayList<> ();

    public void setAuthorities (List<WaterAuthorityModel> authorities) {
        this.authorities = authorities;
        notifyDataSetChanged ();
    }

    @NonNull
    @Override
    public AuthorityViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from (parent.getContext ())
                .inflate (R.layout.item_water_authority, parent, false);
        return new AuthorityViewHolder (view);
    }

    @Override
    public void onBindViewHolder (@NonNull AuthorityViewHolder holder, int position) {
        WaterAuthorityModel authority = authorities.get (position);
        holder.bind (authority);
    }

    @Override
    public int getItemCount () {
        return authorities.size ();
    }

    static class AuthorityViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText;
        private TextView phoneText;
        private TextView addressText;

        public AuthorityViewHolder (@NonNull View itemView) {
            super (itemView);
            nameText = itemView.findViewById (R.id.water_authority_name);
            phoneText = itemView.findViewById (R.id.water_authority_phone_number);
            addressText = itemView.findViewById (R.id.water_authority_address);
        }

        public void bind (WaterAuthorityModel authority) {
            nameText.setText (authority.getName ());
            phoneText.setText (authority.getContacts ());
            addressText.setText (authority.getAddress ());
        }
    }
}