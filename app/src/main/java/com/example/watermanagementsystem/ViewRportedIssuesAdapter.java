package com.example.watermanagementsystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ViewRportedIssuesAdapter extends RecyclerView.Adapter<ViewRportedIssuesAdapter.ViewReportedIssueViewHolder> {

    private List<WaterIssue> issues;
    private SimpleDateFormat dateFormat;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public ViewRportedIssuesAdapter () {
        this.issues = new ArrayList<> ();
        this.dateFormat = new SimpleDateFormat ("dd-MM-yyyy HH:mm", Locale.getDefault ());
        this.db = FirebaseFirestore.getInstance ();
        this.auth = FirebaseAuth.getInstance ();
    }


    public static class ViewReportedIssueViewHolder extends RecyclerView.ViewHolder {
        ImageView issueImage;
        TextView issueType;
        TextView location;
        TextView status;
        TextView timestamp;
        TextView email;
        TextView phoneNumber;
        TextView lastUpdatedBy;
        View statusButton;

        public ViewReportedIssueViewHolder (@NonNull View itemView) {
            super (itemView);
            issueImage = itemView.findViewById (R.id.issueImage);
            issueType = itemView.findViewById (R.id.issueType);
            location = itemView.findViewById (R.id.location);
            status = itemView.findViewById (R.id.status);
            timestamp = itemView.findViewById (R.id.timestamp);
            email = itemView.findViewById (R.id.email);
            phoneNumber = itemView.findViewById (R.id.phoneNumber);
            lastUpdatedBy = itemView.findViewById (R.id.lastUpdatedBy);
            statusButton = itemView.findViewById (R.id.statusButton);
        }
    }

    @NonNull
    @Override
    public ViewReportedIssueViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from (parent.getContext ())
                .inflate (R.layout.item_water_issue, parent, false);
        return new ViewReportedIssueViewHolder (view);
    }

    @Override
    public void onBindViewHolder (@NonNull ViewRportedIssuesAdapter.ViewReportedIssueViewHolder holder, int position) {
        WaterIssue issue = issues.get (position);

        if ( issue.getImageUrl () != null && ! issue.getImageUrl ().isEmpty () ) {
            holder.issueImage.setVisibility (View.VISIBLE);
            Glide.with (holder.itemView.getContext ())
                    .load (issue.getImageUrl ())
                    .into (holder.issueImage);
        } else {
            holder.issueImage.setVisibility (View.GONE);
        }

        holder.issueType.setText ("Issue: " + issue.getIssueType ());
        holder.location.setText ("Location: " + issue.getLocation ());
        holder.status.setText ("Status: " + issue.getStatus ());
        holder.timestamp.setText ("Reported: " + issue.getTimestamp ());

        UserInfo userInfo = issue.getUserInfo ();
        if ( userInfo != null ) {
            holder.email.setText ("Email: " + userInfo.getEmail ());
            holder.phoneNumber.setText ("Phone: " + userInfo.getPhoneNumber ());
        } else {
            holder.email.setText ("Email: N/A");
            holder.phoneNumber.setText ("Phone: N/A");
        }

        if ( issue.getLastUpdatedBy () != null && ! issue.getLastUpdatedBy ().isEmpty () ) {
            holder.lastUpdatedBy.setVisibility (View.VISIBLE);
            holder.lastUpdatedBy.setText ("Last updated by: " + issue.getLastUpdatedBy ());
        } else {
            holder.lastUpdatedBy.setVisibility (View.GONE);
        }
        holder.statusButton.setVisibility (View.GONE);

        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams ();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        holder.itemView.setLayoutParams (params);
    }

    @Override
    public int getItemCount () {
        return issues.size ();
    }

    public void updateIssues (List<WaterIssue> newIssues) {
        issues.clear ();
        issues.addAll (newIssues);
        notifyDataSetChanged ();
    }
}
