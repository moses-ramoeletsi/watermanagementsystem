package com.example.watermanagementsystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder>{
    private List<FeedBack> feedbackList;
    private final FeedbackActionsListener actionsListener;

    public FeedbackAdapter(List<FeedBack> feedbackList, FeedbackActionsListener actionsListener) {
        this.feedbackList = feedbackList;
        this.actionsListener = actionsListener;
    }

    public void setFeedbackList(List<FeedBack> feedbackList) {
        this.feedbackList = feedbackList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback, parent, false);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        FeedBack feedback = feedbackList.get(position);

        holder.feedbackTypeText.setText(feedback.getFeedbackType());
        holder.detailsText.setText(feedback.getDetails());

        holder.editButton.setOnClickListener(v -> {
            if (actionsListener != null) {
                actionsListener.onEdit(feedback);
            } else {
                Toast.makeText(v.getContext(), "Edit action unavailable", Toast.LENGTH_SHORT).show();
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (actionsListener != null) {
                actionsListener.onDelete(feedback.getFeedbackId());
            } else {
                Toast.makeText(v.getContext(), "Delete action unavailable", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return feedbackList != null ? feedbackList.size() : 0;
    }

    public static class FeedbackViewHolder extends RecyclerView.ViewHolder {

        TextView feedbackTypeText, detailsText;
        Button editButton, deleteButton;

        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            feedbackTypeText = itemView.findViewById(R.id.feedbackTypeText);
            detailsText = itemView.findViewById(R.id.detailsText);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
    public interface FeedbackActionsListener {
        void onEdit(FeedBack feedback);
        void onDelete(String feedbackId);
    }

}
