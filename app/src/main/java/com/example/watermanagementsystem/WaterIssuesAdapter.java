package com.example.watermanagementsystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WaterIssuesAdapter extends RecyclerView.Adapter<WaterIssuesAdapter.WaterIssueViewHolder> {
    private List<WaterIssue> issues;
    private SimpleDateFormat dateFormat;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private OnStatusUpdateListener statusUpdateListener;

    public interface OnStatusUpdateListener {
        void onStatusUpdate (String reportId, String newStatus);
    }

    public void setOnStatusUpdateListener (OnStatusUpdateListener listener) {
        this.statusUpdateListener = listener;
    }

    public WaterIssuesAdapter () {
        this.issues = new ArrayList<> ();
        this.dateFormat = new SimpleDateFormat ("dd-MM-yyyy HH:mm", Locale.getDefault ());
        this.db = FirebaseFirestore.getInstance ();
        this.auth = FirebaseAuth.getInstance ();
    }

    public static class WaterIssueViewHolder extends RecyclerView.ViewHolder {
        ImageView issueImage;
        TextView issueType;
        TextView location;
        TextView status;
        TextView timestamp;
        TextView email;
        TextView phoneNumber;
        TextView lastUpdatedBy;
        View statusButton;
        TextView resolvedOverlay;
        View contentLayout;

        public WaterIssueViewHolder (@NonNull View itemView) {
            super (itemView);
            contentLayout = itemView.findViewById (R.id.contentLayout);
            issueImage = itemView.findViewById (R.id.issueImage);
            resolvedOverlay = itemView.findViewById (R.id.resolvedOverlay);
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
    public WaterIssueViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from (parent.getContext ())
                .inflate (R.layout.item_water_issue, parent, false);
        return new WaterIssueViewHolder (view);
    }

    @Override
    public void onBindViewHolder (@NonNull WaterIssueViewHolder holder, int position) {
        WaterIssue issue = issues.get (position);

        boolean isResolved = "Resolved".equals (issue.getStatus ());
        holder.contentLayout.setVisibility (isResolved ? View.INVISIBLE : View.VISIBLE);
        holder.resolvedOverlay.setVisibility (isResolved ? View.VISIBLE : View.GONE);

//        if ( issue.getImageUrl () != null && ! issue.getImageUrl ().isEmpty () ) {
//            holder.issueImage.setVisibility (View.VISIBLE);
//            Glide.with (holder.itemView.getContext ())
//                    .load (issue.getImageUrl ())
//                    .into (holder.issueImage);
//        } else {
//            holder.issueImage.setVisibility (View.GONE);
//        }

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

        // Set last updated by info
        if ( issue.getLastUpdatedBy () != null && ! issue.getLastUpdatedBy ().isEmpty () ) {
            holder.lastUpdatedBy.setVisibility (View.VISIBLE);
            holder.lastUpdatedBy.setText ("Last updated by: " + issue.getLastUpdatedBy ());
        } else {
            holder.lastUpdatedBy.setVisibility (View.GONE);
        }

        // Setup status update button
        holder.statusButton.setOnClickListener (v -> {
            if ( statusUpdateListener != null ) {
                // Show status selection dialog
                showStatusUpdateDialog (v.getContext (), issue.getId ());
            }
        });
    }

    private void showStatusUpdateDialog (Context context, String reportId) {
        String[] statusOptions = { "Pending", "In Progress", "Resolved" };

        new AlertDialog.Builder (context)
                .setTitle ("Update Status")
                .setItems (statusOptions, (dialog, which) -> {
                    String newStatus = statusOptions[which];
                    if ( statusUpdateListener != null ) {
                        statusUpdateListener.onStatusUpdate (reportId, newStatus);
                    }
                })
                .show ();
    }

    private void showStatusUpdateMenu (View view, WaterIssue issue) {
        FirebaseUser currentUser = auth.getCurrentUser ();
        if ( currentUser == null ) {
            Toast.makeText (view.getContext (), "You must be logged in to update status",
                    Toast.LENGTH_SHORT).show ();
            return;
        }

        PopupMenu popup = new PopupMenu (view.getContext (), view);
        popup.getMenu ().add ("In Progress");
        popup.getMenu ().add ("Resolved");

        popup.setOnMenuItemClickListener (item -> {
            String newStatus = item.getTitle ().toString ();
            updateIssueStatus (issue, newStatus, currentUser.getEmail ());
            return true;
        });

        popup.show ();
    }

    private void updateIssueStatus (WaterIssue issue, String newStatus, String userEmail) {
        Map<String, Object> updates = new HashMap<> ();
        updates.put ("status", newStatus);
        updates.put ("lastUpdatedBy", userEmail);
        updates.put ("lastUpdatedAt", new SimpleDateFormat ("dd-MM-yyyy HH:mm",
                Locale.getDefault ()).format (new Date ()));

        db.collection ("waterIssues")
                .document (issue.getId ())
                .update (updates)
                .addOnSuccessListener (aVoid -> {
                    // Update will be reflected through the snapshot listener
                })
                .addOnFailureListener (e -> {
                    Toast.makeText (null, "Failed to update status: " + e.getMessage (),
                            Toast.LENGTH_SHORT).show ();
                });
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
