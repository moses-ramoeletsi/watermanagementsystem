//package com.example.watermanagementsystem;
//
//import android.os.Bundle;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QuerySnapshot;
//
//public class UserManagement extends AppCompatActivity {
//
//    private TextView userNationalIdTextView;
//    private TextView userPhoneNumberTextView;
//    private TextView userAddressTextView;
//    private TextView userNameTextView;
//    private TextView userReportsTextView;
//
//    private TextView waterAuthorityNationalIdTextView;
//    private TextView waterAuthorityPhoneNumberTextView;
//    private TextView waterAuthorityAddressTextView;
//
//    private FirebaseFirestore db;
//
//    @Override
//    protected void onCreate (Bundle savedInstanceState) {
//        super.onCreate (savedInstanceState);
//        setContentView (R.layout.activity_user_management);
//
//        // Initialize Firebase Firestore instance before any database calls
//        db = FirebaseFirestore.getInstance ();
//
//        userNationalIdTextView = findViewById (R.id.user_national_id);
//        userPhoneNumberTextView = findViewById (R.id.user_phone_number);
//        userAddressTextView = findViewById (R.id.user_address);
//        userNameTextView = findViewById (R.id.user_name);
//        userReportsTextView = findViewById (R.id.user_reports);
//
//        waterAuthorityNationalIdTextView = findViewById (R.id.water_authority_name);
//        waterAuthorityPhoneNumberTextView = findViewById (R.id.water_authority_phone_number);
//        waterAuthorityAddressTextView = findViewById (R.id.water_authority_address);
//
//        // Call data-fetching methods after db initialization
//        fetchUserData ();
//        fetchWaterAuthorityData ();
//    }
//
//
//    private void fetchUserData () {
//        db.collection ("users")
//                .whereEqualTo ("role", "user")
//                .get ()
//                .addOnSuccessListener (querySnapshot -> {
//                    for ( DocumentSnapshot document : querySnapshot.getDocuments () ) {
//                        String userId = document.getString ("uid");
//                        String userName = document.getString ("name");
//                        String nationalId = document.getString ("nationalId");
//                        String phoneNumber = document.getString ("contacts");
//                        String address = document.getString ("residentialAddress");
//
//                        fetchUserReports (userId);
//
//                        userNameTextView.setText (userName);
//                        userNationalIdTextView.setText (nationalId);
//                        userPhoneNumberTextView.setText (phoneNumber);
//                        userAddressTextView.setText (address);
//                    }
//                });
//    }
//
//    private void fetchUserReports (String userId) {
//        db.collection ("waterIssues")
//                .whereEqualTo ("userInfo.uid", userId)
//                .addSnapshotListener ((querySnapshot, e) -> {
//                    if ( e != null ) {
//                        return;
//                    }
//
//                    int reportCount = querySnapshot.size ();
//                    userReportsTextView.setText (String.format ("%d Reports", reportCount));
//                });
//    }
//
//    private void fetchWaterAuthorityData () {
//        db.collection ("users")
//                .whereEqualTo ("role", "waterAuthority")
//                .get ()
//                .addOnSuccessListener (querySnapshot -> {
//                    for ( DocumentSnapshot document : querySnapshot.getDocuments () ) {
//                        String authorityName = document.getString ("name");
//                        String phoneNumber = document.getString ("contacts");
//                        String address = document.getString ("physicalAddress");
//
//                        waterAuthorityNationalIdTextView.setText (authorityName);
//                        waterAuthorityPhoneNumberTextView.setText (phoneNumber);
//                        waterAuthorityAddressTextView.setText (address);
//                    }
//                });
//    }
//}

package com.example.watermanagementsystem;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class UserManagement extends AppCompatActivity {
    private RecyclerView userRecyclerView, authRecyclerView;
    private FirebaseFirestore db;
    private UserAdapter userAdapter;
    private WaterAuthorityAdapter waterAuthorityAdapter;
    private TextView userCountTextView;
    private TextView authCountTextView;


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_user_management);

        db = FirebaseFirestore.getInstance ();


        userCountTextView = findViewById (R.id.userCountTextView);
        authCountTextView = findViewById (R.id.authCountTextView);

        userRecyclerView = findViewById (R.id.userRecyclerView);

        userAdapter = new UserAdapter ();
        LinearLayoutManager userLayoutManager = new LinearLayoutManager (this, LinearLayoutManager.HORIZONTAL, false);
        userRecyclerView.setLayoutManager (userLayoutManager);
        userRecyclerView.setAdapter (userAdapter);

        authRecyclerView = findViewById (R.id.authRecyclerView);

        waterAuthorityAdapter = new WaterAuthorityAdapter ();
        LinearLayoutManager waterAuthLayoutManager = new LinearLayoutManager (this, LinearLayoutManager.HORIZONTAL, false);
        authRecyclerView.setLayoutManager (waterAuthLayoutManager);
        authRecyclerView.setAdapter (waterAuthorityAdapter);

        fetchUsers ();
        fetchWaterAuthorities ();
    }

    private void fetchWaterAuthorities () {

        List<WaterAuthorityModel> authorities = new ArrayList<> ();

        db.collection ("users")
                .whereEqualTo ("role", "waterAuthority")
                .get ()
                .addOnSuccessListener (querySnapshot -> {
                    querySnapshot.getDocuments ().forEach (document -> {
                        WaterAuthorityModel authority = new WaterAuthorityModel (
                                document.getString ("uid"),
                                document.getString ("name"),
                                document.getString ("contacts"),
                                document.getString ("physicalAddress")
                        );
                        authorities.add (authority);
                    });

                    waterAuthorityAdapter.setAuthorities (authorities);
                    authCountTextView.setText (String.format ("%d Authorities", authorities.size ()));
                });
    }

    private void fetchUsers () {
     
        List<UserModel> users = new ArrayList<> ();

        db.collection ("users")
                .whereEqualTo ("role", "user")
                .get ()
                .addOnSuccessListener (querySnapshot -> {
                    querySnapshot.getDocuments ().forEach (document -> {
                        UserModel user = new UserModel (
                                document.getString ("uid"),
                                document.getString ("name"),
                                document.getString ("nationalId"),
                                document.getString ("contacts"),
                                document.getString ("residentialAddress")
                        );
                        users.add (user);
                        fetchUserReports (user);
                    });

                    userAdapter.setUsers (users);
                    userCountTextView.setText (String.format ("%d Users", users.size ()));
                });
    }

    private void fetchUserReports (UserModel user) {
        db.collection ("waterIssues")
                .whereEqualTo ("userInfo.uid", user.getUid ())
                .addSnapshotListener ((querySnapshot, e) -> {
                    if ( e != null ) return;

//                    user.setReportCount (querySnapshot.size ());
                    userAdapter.notifyDataSetChanged ();
                });
    }
}
