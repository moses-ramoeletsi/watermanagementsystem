package com.example.watermanagementsystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ViewWaterSourceAdapter extends RecyclerView.Adapter<ViewWaterSourceAdapter.WaterSourceViewHolder> {

    private List<WaterSource> waterSources;
    private SimpleDateFormat dateFormat = new SimpleDateFormat ("MMM dd, yyyy", Locale.getDefault ());
    private FirebaseFirestore db;

    public ViewWaterSourceAdapter () {
        this.waterSources = new ArrayList<> ();
        this.db = FirebaseFirestore.getInstance ();
    }

    public static class WaterSourceViewHolder extends RecyclerView.ViewHolder {
        TextView sourceNameText, statusText, descriptionText,
                directionsText, maintenanceDateText, contactText;
        Button editButton, deleteButton;

        public WaterSourceViewHolder (@NonNull View itemView) {
            super (itemView);
            sourceNameText = itemView.findViewById (R.id.sourceNameText);
            statusText = itemView.findViewById (R.id.statusText);
            descriptionText = itemView.findViewById (R.id.descriptionText);
            directionsText = itemView.findViewById (R.id.directionsText);
            maintenanceDateText = itemView.findViewById (R.id.maintenanceDateText);
            contactText = itemView.findViewById (R.id.contactText);
            editButton = itemView.findViewById (R.id.editButton);
            deleteButton = itemView.findViewById (R.id.deleteButton);
        }
    }

    @NonNull
    @Override
    public WaterSourceViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from (parent.getContext ())
                .inflate (R.layout.item_water_source, parent, false);
        return new WaterSourceViewHolder (view);
    }

    @Override
    public void onBindViewHolder (@NonNull WaterSourceViewHolder holder, int position) {
        WaterSource source = waterSources.get (position);
        holder.sourceNameText.setText (source.getSourceName ());
        holder.statusText.setText ("Status: " + source.getStatus ());
        holder.descriptionText.setText (source.getDescription ());
        holder.directionsText.setText ("Directions: " + source.getDirections ());
        holder.maintenanceDateText.setText ("Last Maintenance: " +
                dateFormat.format (source.getLastMaintenanceDate ()));
        holder.contactText.setText ("Contact: " + source.getUserEmail ());

        holder.editButton.setVisibility (View.GONE);
        holder.deleteButton.setVisibility (View.GONE);

        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams ();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        holder.itemView.setLayoutParams (params);


    }

    @Override
    public int getItemCount () {
        return waterSources.size ();
    }

    public void updateWaterSources (List<WaterSource> newSources) {
        waterSources.clear ();
        waterSources.addAll (newSources);
        notifyDataSetChanged ();
    }
}