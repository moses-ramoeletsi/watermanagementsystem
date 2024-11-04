package com.example.watermanagementsystem;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private TextView lastMaintenanceDateText, noNearbyWaterSourcesMessage, nearbyWaterSourcesTitle;
    private Spinner statusSpinner;
    private Button submitButton, datePickerButton, nearByWaterSource;
    private RecyclerView sourcesRecyclerView;
    private WaterSourceAdapter adapter;
    private List<WaterSource> waterSources;
    private Date selectedMaintenanceDate;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private SimpleDateFormat dateFormat;
    private Calendar calendar;
    private View formContainer;
    private boolean isWaterAuthority = false;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 123;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int PROXIMITY_RADIUS = 5000; // 5 km radius
    private boolean showingNearbyWaterSources = false;

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
        checkUserRole();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        nearByWaterSource.setOnClickListener(v -> handleNearbyWaterSources());
    }

    private void handleNearbyWaterSources() {
        if (!showingNearbyWaterSources) {
            // Show "Water Source Near You" title and get nearby water sources
            nearbyWaterSourcesTitle.setVisibility(View.VISIBLE);
            nearbyWaterSourcesTitle.setText("Water Sources Near You");
            getNearbyWaterSources();
            showingNearbyWaterSources = true;
        } else {
            // Show "All Available Water Sources" title and load all water sources
            nearbyWaterSourcesTitle.setVisibility(View.VISIBLE);
            nearbyWaterSourcesTitle.setText("All Available Water Sources");
            loadWaterSources();
            showingNearbyWaterSources = false;
        }
    }

    private void getNearbyWaterSources() {
        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
            return;
        }

        // Get the user's current location
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        double userLatitude = location.getLatitude();
                        double userLongitude = location.getLongitude();

                        // Load water sources from Firestore
                        db.collection("waterSources")
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        List<WaterSource> nearbyWaterSources = new ArrayList<>();
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            WaterSource source = document.toObject(WaterSource.class);
                                            source.setDocumentId(document.getId());

                                            // Calculate the distance between the user and the water source
                                            double[] sourceCoordinates = parseCoordinates(source.getDirections());
                                            double distance = calculateDistance(userLatitude, userLongitude, sourceCoordinates[0], sourceCoordinates[1]);

                                            // Add the water source to the list if it's within the proximity radius
                                            if (distance <= PROXIMITY_RADIUS) {
                                                nearbyWaterSources.add(source);
                                            }
                                        }

                                        // Update the UI with the nearby water sources
                                        updateWaterSourcesUI(nearbyWaterSources);
                                    } else {
                                        // Handle error
                                        Log.e(TAG, "Error getting water sources: ", task.getException());
                                        showErrorMessage("Error loading nearby water sources");
                                    }
                                });
                    } else {
                        // Handle error
                        Log.e(TAG, "Current location is null");
                        showErrorMessage("Unable to get your location");
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle error
                    Log.e(TAG, "Error getting location: ", e);
                    showErrorMessage("Error getting your location");
                });
    }

    private double[] parseCoordinates(String coordinatesString) {
        String[] parts = coordinatesString.split(", ");
        double latitude = Double.parseDouble(parts[0]);
        double longitude = Double.parseDouble(parts[1]);
        return new double[]{latitude, longitude};
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371; // in kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }

    private void updateWaterSourcesUI(List<WaterSource> nearbyWaterSources) {
        if (nearbyWaterSources.isEmpty()) {

            sourcesRecyclerView.setVisibility(View.GONE);
            noNearbyWaterSourcesMessage.setVisibility(View.VISIBLE);
        } else {

            sourcesRecyclerView.setVisibility(View.VISIBLE);
            noNearbyWaterSourcesMessage.setVisibility(View.GONE);
            adapter.updateData(nearbyWaterSources);
        }
    }

    private void showErrorMessage(String message) {
        // Display an error message to the user
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void initializeViews() {
        formContainer = findViewById(R.id.formContainer);
        sourceNameInput = findViewById(R.id.sourceNameInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        directionsInput = findViewById(R.id.directionsInput);
        statusSpinner = findViewById(R.id.statusSpinner);
        submitButton = findViewById(R.id.submitButton);
        datePickerButton = findViewById(R.id.datePickerButton);
        lastMaintenanceDateText = findViewById(R.id.lastMaintenanceDateText);
        sourcesRecyclerView = findViewById(R.id.sourcesRecyclerView);
        nearByWaterSource = findViewById(R.id.nearByWaterSource);
        noNearbyWaterSourcesMessage = findViewById(R.id.noNearbyWaterSourcesMessage);
        nearbyWaterSourcesTitle = findViewById(R.id.nearbyWaterSourcesTitle);

        nearByWaterSource.setOnClickListener(v -> getNearbyWaterSources());
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
        adapter = new WaterSourceAdapter(waterSources, isWaterAuthority);
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

    private void checkUserRole() {
        UserRoleManager.checkUserRole(isAuthority -> {
            isWaterAuthority = isAuthority;
            runOnUiThread(() -> {
                formContainer.setVisibility(isAuthority ? View.VISIBLE : View.GONE);
                nearByWaterSource.setVisibility(isAuthority ? View.GONE : View.VISIBLE);
                setupSpinner();
                setupDatePicker();
                setupRecyclerView();
                loadWaterSources();
            });
        });
    }
}
