package com.example.watermanagementsystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.ReportViewHolder> {
    private List<WaterIssue> reports = new ArrayList<> ();

    public void setReports (List<WaterIssue> reports) {
        this.reports = reports;
        notifyDataSetChanged ();
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from (parent.getContext ())
                .inflate (R.layout.item_report, parent, false);
        return new ReportViewHolder (view);
    }

    @Override
    public void onBindViewHolder (@NonNull ReportViewHolder holder, int position) {
        WaterIssue report = reports.get (position);
        boolean isResolved = "Resolved".equalsIgnoreCase (report.getStatus ());
        holder.normalContentLayout.setVisibility (isResolved ? View.GONE : View.VISIBLE);
        holder.resolvedText.setVisibility (isResolved ? View.VISIBLE : View.GONE);

        if ( ! isResolved ) {
            holder.issueTypeText.setText (report.getIssueType ());
            holder.locationText.setText (report.getLocation ());
            holder.statusText.setText ("Status: " + report.getStatus ());
            holder.timestampText.setText ("Time: " + report.getTimestamp ());
        }
    }

    @Override
    public int getItemCount () {
        return reports.size ();
    }

    class ReportViewHolder extends RecyclerView.ViewHolder {
        ImageView reportImage;
        TextView issueTypeText, locationText, statusText, timestampText, resolvedText;
        View normalContentLayout;

        ReportViewHolder (@NonNull View itemView) {
            super (itemView);
            normalContentLayout = itemView.findViewById (R.id.normalContentLayout);
            reportImage = itemView.findViewById (R.id.reportImage);
            issueTypeText = itemView.findViewById (R.id.issueTypeText);
            locationText = itemView.findViewById (R.id.locationText);
            statusText = itemView.findViewById (R.id.statusText);
            timestampText = itemView.findViewById (R.id.timestampText);
            resolvedText = itemView.findViewById (R.id.resolvedText);
        }
    }
}

