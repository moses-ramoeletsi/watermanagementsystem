package com.example.watermanagementsystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {
    private List<FeedBack> feedbackList;
    private final FeedbackActionsListener actionsListener;
    private final boolean isAdmin;

    public FeedbackAdapter (List<FeedBack> feedbackList, FeedbackActionsListener actionsListener, boolean isAdmin) {
        this.feedbackList = feedbackList;
        this.actionsListener = actionsListener;
        this.isAdmin = isAdmin;
    }

    public void setFeedbackList (List<FeedBack> feedbackList) {
        this.feedbackList = feedbackList;
        notifyDataSetChanged ();
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from (parent.getContext ()).inflate (R.layout.item_feedback, parent, false);
        return new FeedbackViewHolder (view);
    }

    @Override
    public void onBindViewHolder (@NonNull FeedbackViewHolder holder, int position) {
        FeedBack feedback = feedbackList.get (position);

        holder.feedbackTypeText.setText (feedback.getFeedbackType ());
        holder.detailsText.setText (feedback.getDetails ());

        if ( feedback.getAdminResponse () != null && ! feedback.getAdminResponse ().isEmpty () ) {
            holder.adminResponseText.setVisibility (View.VISIBLE);
            holder.adminResponseText.setText ("Admin Response: " + feedback.getAdminResponse ());
        } else {
            holder.adminResponseText.setVisibility (View.GONE);
        }


        if ( isAdmin ) {

            configureAdminView (holder, feedback);
        } else {

            configureUserView (holder, feedback);
        }
    }

    private void configureAdminView (FeedbackViewHolder holder, FeedBack feedback) {

        if ( isAdmin ) {
            holder.userEmailText.setVisibility (View.VISIBLE);
            holder.userEmailText.setText ("From: " + feedback.getUserEmail ());
        } else {
            holder.userEmailText.setVisibility (View.GONE);
        }
        holder.editButton.setVisibility (View.GONE);
        holder.deleteButton.setVisibility (View.GONE);

        holder.responseEditText.setVisibility (View.VISIBLE);

        if ( feedback.getAdminResponse () != null && ! feedback.getAdminResponse ().isEmpty () ) {
            holder.respondButton.setVisibility (View.GONE);
            holder.editResponseButton.setVisibility (View.VISIBLE);
            holder.deleteResponseButton.setVisibility (View.VISIBLE);

            holder.editResponseButton.setOnClickListener (v -> {
                String newResponse = holder.responseEditText.getText ().toString ().trim ();
                if ( ! newResponse.isEmpty () && actionsListener != null ) {
                    actionsListener.onEditResponse (feedback.getFeedbackId (), newResponse);
                    holder.responseEditText.setText ("");
                }
            });

            holder.deleteResponseButton.setOnClickListener (v -> {
                if ( actionsListener != null ) {
                    actionsListener.onDeleteResponse (feedback.getFeedbackId ());
                }
            });
        } else {
            // No response yet - show respond button
            holder.respondButton.setVisibility (View.VISIBLE);
            holder.editResponseButton.setVisibility (View.GONE);
            holder.deleteResponseButton.setVisibility (View.GONE);

            // Set up initial response listener
            holder.respondButton.setOnClickListener (v -> {
                String response = holder.responseEditText.getText ().toString ().trim ();
                if ( ! response.isEmpty () && actionsListener != null ) {
                    actionsListener.onRespond (feedback.getFeedbackId (), response);
                    holder.responseEditText.setText ("");
                }
            });
        }
    }

    private void configureUserView (FeedbackViewHolder holder, FeedBack feedback) {

        holder.responseEditText.setVisibility (View.GONE);
        holder.respondButton.setVisibility (View.GONE);
        holder.editResponseButton.setVisibility (View.GONE);
        holder.deleteResponseButton.setVisibility (View.GONE);

        holder.editButton.setVisibility (View.VISIBLE);
        holder.deleteButton.setVisibility (View.VISIBLE);

        holder.editButton.setOnClickListener (v -> {
            if ( actionsListener != null ) {
                actionsListener.onEdit (feedback);
            }
        });

        holder.deleteButton.setOnClickListener (v -> {
            if ( actionsListener != null ) {
                actionsListener.onDelete (feedback.getFeedbackId ());
            }
        });
    }

    @Override
    public int getItemCount () {
        return feedbackList != null ? feedbackList.size () : 0;
    }

    public static class FeedbackViewHolder extends RecyclerView.ViewHolder {
        TextView feedbackTypeText, detailsText, userEmailText, adminResponseText;
        Button editButton, deleteButton, respondButton, editResponseButton, deleteResponseButton;
        EditText responseEditText;

        public FeedbackViewHolder (@NonNull View itemView) {
            super (itemView);
            feedbackTypeText = itemView.findViewById (R.id.feedbackTypeText);
            userEmailText = itemView.findViewById (R.id.userEmailText);
            detailsText = itemView.findViewById (R.id.detailsText);
            adminResponseText = itemView.findViewById (R.id.adminResponseText);
            editButton = itemView.findViewById (R.id.editButton);
            deleteButton = itemView.findViewById (R.id.deleteButton);
            respondButton = itemView.findViewById (R.id.respondButton);
            editResponseButton = itemView.findViewById (R.id.editResponseButton);
            deleteResponseButton = itemView.findViewById (R.id.deleteResponseButton);
            responseEditText = itemView.findViewById (R.id.responseEditText);
        }
    }

    public interface FeedbackActionsListener {
        void onEdit (FeedBack feedback);

        void onDelete (String feedbackId);

        void onRespond (String feedbackId, String response);

        void onEditResponse (String feedbackId, String newResponse);

        void onDeleteResponse (String feedbackId);
    }
}