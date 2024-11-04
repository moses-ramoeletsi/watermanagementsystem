package com.example.watermanagementsystem;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CleanWaterSource extends AppCompatActivity {

    private EditText sourceNameInput, descriptionInput, directionsInput;
    private TextView lastMaintenanceDateText;
    private Spinner statusSpinner;
    private Button submitButton, datePickerButton;
    private RecyclerView sourcesRecyclerView;
    private WaterSourceAdapter adapter;
    private List<WaterSource> waterSources;
    private Date selectedMaintenanceDate;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private SimpleDateFormat dateFormat;
    private Calendar calendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_clean_water_source);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        calendar = Calendar.getInstance();
        selectedMaintenanceDate = calendar.getTime();

        initializeViews();
        setupSpinner();
        setupDatePicker();
        setupRecyclerView();
        loadWaterSources();

        submitButton.setOnClickListener(v -> saveWaterSource());
    }

    private void initializeViews() {
        sourceNameInput = findViewById(R.id.sourceNameInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        directionsInput = findViewById(R.id.directionsInput);
        statusSpinner = findViewById(R.id.statusSpinner);
        submitButton = findViewById(R.id.submitButton);
        datePickerButton = findViewById(R.id.datePickerButton);
        lastMaintenanceDateText = findViewById(R.id.lastMaintenanceDateText);
        sourcesRecyclerView = findViewById(R.id.sourcesRecyclerView);

        updateDateDisplay();
    }

    private void setupDatePicker() {
        datePickerButton.setOnClickListener(v -> showDatePickerDialog());
    }

    private void showDatePickerDialog() {
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    selectedMaintenanceDate = calendar.getTime();
                    updateDateDisplay();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    private void updateDateDisplay() {
        lastMaintenanceDateText.setText(String.format("Last Maintenance Date: %s",
                dateFormat.format(selectedMaintenanceDate)));
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.status_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);
    }

    private void setupRecyclerView() {
        waterSources = new ArrayList<>();
        adapter = new WaterSourceAdapter(waterSources);
        sourcesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sourcesRecyclerView.setAdapter(adapter);
    }

    private void saveWaterSource() {
        String sourceName = sourceNameInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String directions = directionsInput.getText().toString().trim();
        String status = statusSpinner.getSelectedItem().toString();

        if (sourceName.isEmpty() || description.isEmpty() || directions.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> waterSource = new HashMap<>();
        waterSource.put("sourceName", sourceName);
        waterSource.put("description", description);
        waterSource.put("directions", directions);
        waterSource.put("status", status);
        waterSource.put("lastMaintenanceDate", selectedMaintenanceDate);
        waterSource.put("userEmail", auth.getCurrentUser().getEmail());
        waterSource.put("userContact", auth.getCurrentUser().getPhoneNumber());

        db.collection("waterSources")
                .add(waterSource)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(CleanWaterSource.this,
                            "Water source added successfully", Toast.LENGTH_SHORT).show();
                    clearInputs();
                    loadWaterSources();

                })
                .addOnFailureListener(e ->
                        Toast.makeText(CleanWaterSource.this,
                                "Error adding water source", Toast.LENGTH_SHORT).show());
    }

    private void clearInputs() {
        sourceNameInput.setText("");
        descriptionInput.setText("");
        directionsInput.setText("");
        statusSpinner.setSelection(0);
        calendar = Calendar.getInstance();
        selectedMaintenanceDate = calendar.getTime();
        updateDateDisplay();
    }

    private void loadWaterSources() {
        db.collection("waterSources")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        waterSources.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            WaterSource source = document.toObject(WaterSource.class);
                            source.setDocumentId(document.getId());
                            waterSources.add(source);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

}