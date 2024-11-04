package com.example.watermanagementsystem;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class DataInsightsActivity extends AppCompatActivity {
    private static final String TAG = "DataInsightsActivity";
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private Spinner timeframeSpinner;
    private ReportsGraphView graphView;
    private TextView totalReportsText;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_insights);

        progressBar = findViewById(R.id.progressBar);
        timeframeSpinner = findViewById(R.id.timeframeSpinner);
        graphView = findViewById(R.id.graphView);
        totalReportsText = findViewById(R.id.totalReportsText);

        db = FirebaseFirestore.getInstance();
        dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());

        setupTimeframeSpinner();
        loadReportData("MONTHLY");
    }

    private void setupTimeframeSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Daily", "Weekly", "Monthly"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeframeSpinner.setAdapter(adapter);
        timeframeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String timeframe = position == 0 ? "DAILY" : position == 1 ? "WEEKLY" : "MONTHLY";
                loadReportData(timeframe);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    private void loadReportData(String timeframe) {
        progressBar.setVisibility(View.VISIBLE);

        SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        Calendar calendar = Calendar.getInstance();
        Date endDate = calendar.getTime();

        switch (timeframe) {
            case "DAILY":
                calendar.add(Calendar.DAY_OF_MONTH, -30);
                break;
            case "WEEKLY":
                calendar.add(Calendar.WEEK_OF_YEAR, -12);
                break;
            case "MONTHLY":
                calendar.add(Calendar.MONTH, -12);
                break;
        }
        Date startDate = calendar.getTime();

        db.collection("waterIssues")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Map<String, Integer> reportCounts = processReportData(
                            queryDocumentSnapshots.getDocuments(),
                            timeframe
                    );
                    for (Map.Entry<String, Integer> entry : reportCounts.entrySet()) {
                        Log.d(TAG, entry.getKey() + ": " + entry.getValue());
                    }

                    updateUI(reportCounts, queryDocumentSnapshots.size());
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading data: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });

    }

    private Map<String, Integer> processReportData(List<DocumentSnapshot> documents,
                                                   String timeframe) {
        Map<String, Integer> reportCounts = new TreeMap<>();
        SimpleDateFormat labelFormat;


        switch (timeframe) {
            case "DAILY":
                labelFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
                break;
            case "WEEKLY":
                labelFormat = new SimpleDateFormat("'Week' w", Locale.getDefault());
                break;
            case "MONTHLY":
            default:
                labelFormat = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
                break;
        }


        Calendar cal = Calendar.getInstance();
        Date endDate = cal.getTime();

        switch (timeframe) {
            case "DAILY":
                cal.add(Calendar.DAY_OF_MONTH, -30);
                break;
            case "WEEKLY":
                cal.add(Calendar.WEEK_OF_YEAR, -12);
                break;
            case "MONTHLY":
                cal.add(Calendar.MONTH, -12);
                break;
        }

        Date startDate = cal.getTime();
        cal.setTime(startDate);

        while (!cal.getTime().after(endDate)) {
            String label = labelFormat.format(cal.getTime());
            reportCounts.put(label, 0);

            switch (timeframe) {
                case "DAILY":
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                    break;
                case "WEEKLY":
                    cal.add(Calendar.WEEK_OF_YEAR, 1);
                    break;
                case "MONTHLY":
                    cal.add(Calendar.MONTH, 1);
                    break;
            }
        }

        for (DocumentSnapshot document : documents) {
            try {
                String timestampStr = document.getString("timestamp");
                if (timestampStr != null) {
                    Date reportDate = dateFormat.parse(timestampStr);
                    if (reportDate != null) {
                        String label = labelFormat.format(reportDate);
                        reportCounts.put(label, reportCounts.getOrDefault(label, 0) + 1);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return reportCounts;
    }

    private void updateUI(Map<String, Integer> reportCounts, int totalReports) {
        totalReportsText.setText(String.format("Total Reports: %d", totalReports));
        graphView.setData(reportCounts);
    }


}