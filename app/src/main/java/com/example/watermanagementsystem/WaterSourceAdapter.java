package com.example.watermanagementsystem;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WaterSourceAdapter extends RecyclerView.Adapter<WaterSourceAdapter.ViewHolder> {
    private static final String TAG = "WaterSourceAdapter";
    private List<WaterSource> waterSources;
    private SimpleDateFormat dateFormat = new SimpleDateFormat ("MMM dd, yyyy", Locale.getDefault ());
    private Context context;
    private FirebaseFirestore db;
    private boolean isWaterAuthority;

    public WaterSourceAdapter (List<WaterSource> waterSources, boolean isWaterAuthority) {
        this.waterSources = waterSources;
        this.isWaterAuthority = isWaterAuthority;
        this.db = FirebaseFirestore.getInstance ();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext ();
        View view = LayoutInflater.from (context)
                .inflate (R.layout.item_water_source, parent, false);
        return new ViewHolder (view);
    }

    @Override
    public void onBindViewHolder (@NonNull ViewHolder holder, int position) {
        WaterSource source = waterSources.get (position);
        holder.sourceNameText.setText (source.getSourceName ());
        holder.statusText.setText ("Status: " + source.getStatus ());
        holder.descriptionText.setText (source.getDescription ());
        holder.directionsText.setText ("Directions: " + source.getDirections ());
        holder.maintenanceDateText.setText ("Last Maintenance: " +
                dateFormat.format (source.getLastMaintenanceDate ()));
        holder.contactText.setText ("Contact: " + source.getUserEmail ());

        holder.editButton.setVisibility (isWaterAuthority ? View.VISIBLE : View.GONE);
        holder.deleteButton.setVisibility (isWaterAuthority ? View.VISIBLE : View.GONE);


        if ( isWaterAuthority ) {
            holder.editButton.setOnClickListener (v -> showEditDialog (source, holder.getAdapterPosition ()));
            holder.deleteButton.setOnClickListener (v -> showDeleteConfirmation (source, holder.getAdapterPosition ()));
        }
    }

    private void showEditDialog (WaterSource source, int position) {
        try {
            LayoutInflater inflater = LayoutInflater.from (context);
            View dialogView = inflater.inflate (R.layout.dialog_edit_water_source, null);

            if ( dialogView == null ) {
                Toast.makeText (context, "Error loading edit dialog", Toast.LENGTH_SHORT).show ();
                return;
            }

            EditText editSourceName = dialogView.findViewById (R.id.editSourceNameInput);
            EditText editDescription = dialogView.findViewById (R.id.editDescriptionInput);
            EditText editDirections = dialogView.findViewById (R.id.editDirectionsInput);
            Spinner editStatus = dialogView.findViewById (R.id.editStatusSpinner);
            Button editDateButton = dialogView.findViewById (R.id.editDatePickerButton);
            TextView editDateText = dialogView.findViewById (R.id.editLastMaintenanceDateText);

            if ( editSourceName == null || editDescription == null || editDirections == null ||
                    editStatus == null || editDateButton == null || editDateText == null ) {
                Toast.makeText (context, "Error loading edit dialog", Toast.LENGTH_SHORT).show ();
                return;
            }

            ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource (context,
                    R.array.status_options, android.R.layout.simple_spinner_item);
            spinnerAdapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);
            editStatus.setAdapter (spinnerAdapter);

            editSourceName.setText (source.getSourceName ());
            editDescription.setText (source.getDescription ());
            editDirections.setText (source.getDirections ());
            editStatus.setSelection (spinnerAdapter.getPosition (source.getStatus ()));
            editDateText.setText ("Last Maintenance: " + dateFormat.format (source.getLastMaintenanceDate ()));

            Calendar calendar = Calendar.getInstance ();
            calendar.setTime (source.getLastMaintenanceDate ());

            editDateButton.setOnClickListener (v -> {
                DatePickerDialog datePickerDialog = new DatePickerDialog (
                        context,
                        (view, year, month, dayOfMonth) -> {
                            calendar.set (Calendar.YEAR, year);
                            calendar.set (Calendar.MONTH, month);
                            calendar.set (Calendar.DAY_OF_MONTH, dayOfMonth);
                            editDateText.setText ("Last Maintenance: " +
                                    dateFormat.format (calendar.getTime ()));
                        },
                        calendar.get (Calendar.YEAR),
                        calendar.get (Calendar.MONTH),
                        calendar.get (Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.getDatePicker ().setMaxDate (System.currentTimeMillis ());
                datePickerDialog.show ();
            });

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder (context)
                    .setTitle ("Edit Water Source")
                    .setView (dialogView)
                    .setPositiveButton ("Save", (dialog, which) -> {
                        source.setSourceName (editSourceName.getText ().toString ());
                        source.setDescription (editDescription.getText ().toString ());
                        source.setDirections (editDirections.getText ().toString ());
                        source.setStatus (editStatus.getSelectedItem ().toString ());
                        source.setLastMaintenanceDate (calendar.getTime ());

                        updateWaterSource (source, position);
                    })
                    .setNegativeButton ("Cancel", null);

            AlertDialog dialog = builder.create ();
            dialog.show ();

        } catch ( Exception e ) {
            Toast.makeText (context, "Error showing edit dialog", Toast.LENGTH_SHORT).show ();
        }
    }

    private void showDeleteConfirmation (WaterSource source, int position) {
        new MaterialAlertDialogBuilder (context)
                .setTitle ("Delete Water Source")
                .setMessage ("Are you sure you want to delete this water source?")
                .setPositiveButton ("Delete", (dialog, which) -> deleteWaterSource (source, position))
                .setNegativeButton ("Cancel", null)
                .show ();
    }

    private void updateWaterSource (WaterSource source, int position) {
        if ( source.getDocumentId () == null ) {
            Toast.makeText (context, "Error updating: Missing document ID",
                    Toast.LENGTH_SHORT).show ();
            return;
        }

        db.collection ("waterSources").document (source.getDocumentId ())
                .set (source)
                .addOnSuccessListener (aVoid -> {
                    waterSources.set (position, source);
                    notifyItemChanged (position);
                    Toast.makeText (context, "Water source updated successfully",
                            Toast.LENGTH_SHORT).show ();
                })
                .addOnFailureListener (e -> {
                    Log.e (TAG, "Error updating water source", e);
                    Toast.makeText (context, "Error updating water source",
                            Toast.LENGTH_SHORT).show ();
                });
    }

    private void deleteWaterSource (WaterSource source, int position) {
        if ( source.getDocumentId () == null ) {
            Log.e (TAG, "Document ID is null for water source");
            Toast.makeText (context, "Error deleting: Missing document ID",
                    Toast.LENGTH_SHORT).show ();
            return;
        }

        db.collection ("waterSources").document (source.getDocumentId ())
                .delete ()
                .addOnSuccessListener (aVoid -> {
                    waterSources.remove (position);
                    notifyItemRemoved (position);
                    Toast.makeText (context, "Water source deleted successfully",
                            Toast.LENGTH_SHORT).show ();
                })
                .addOnFailureListener (e -> {
                    Toast.makeText (context, "Error deleting water source",
                            Toast.LENGTH_SHORT).show ();
                });
    }

    @Override
    public int getItemCount () {
        return waterSources.size ();
    }

    public void updateData (List<WaterSource> newWaterSources) {
        waterSources.clear ();
        waterSources.addAll (newWaterSources);
        notifyDataSetChanged ();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView sourceNameText, statusText, descriptionText,
                directionsText, maintenanceDateText, contactText;
        Button editButton, deleteButton;

        ViewHolder (View itemView) {
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
}