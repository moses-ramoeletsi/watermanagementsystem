package com.example.watermanagementsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class registration extends AppCompatActivity {

    private EditText name, nationalId, email, residentialAddress, physicalAddress, contacts, password, confirmPassword;
    private Spinner userroleSpinner;
    private Button signup_button;
    private TextView loginRedirectText;
    private ProgressDialog loadingBar;

    private FirebaseAuth appAuth;

    FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = db.collection("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        appAuth = FirebaseAuth.getInstance();

        name = findViewById(R.id.name);
        nationalId = findViewById(R.id.nationalId);
        email = findViewById(R.id.email);
        residentialAddress = findViewById(R.id.residentialAddress);
        physicalAddress = findViewById(R.id.physicalAddress);
        contacts = findViewById(R.id.contacts);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        userroleSpinner = findViewById(R.id.roleSpinner);

        signup_button = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        loadingBar = new ProgressDialog(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.roles_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userroleSpinner.setAdapter(adapter);
        userroleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedRole = parent.getItemAtPosition(position).toString();
                if (!selectedRole.equals("Select Role")) {
                    updateFormFields(selectedRole);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectUserToLogin();
            }
        });

        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUserAccount();
            }
        });
    }

    private void updateFormFields(String role) {
        // Make sure all fields are initialized before updating visibility
        if (nationalId != null && residentialAddress != null && physicalAddress != null) {
            if (role.equals("user")) {
                nationalId.setVisibility(View.VISIBLE);
                residentialAddress.setVisibility(View.VISIBLE);
                physicalAddress.setVisibility(View.GONE);
            } else if (role.equals("waterAuthority")) {
                nationalId.setVisibility(View.GONE);
                residentialAddress.setVisibility(View.GONE);
                physicalAddress.setVisibility(View.VISIBLE);
            }
        }
    }

    private void createUserAccount() {

        String nameText = name.getText().toString().trim();
        String nationalIdText = nationalId.getText().toString().trim();
        String emailText = email.getText().toString().trim();
        String residentialAddressText = residentialAddress.getText().toString().trim();
        String physicalAddressText = physicalAddress.getText().toString().trim();
        String contactsText = contacts.getText().toString().trim();
        String passwordText = password.getText().toString().trim();

        String role = userroleSpinner.getSelectedItem().toString();
        if (role.equals("Select Role")) {
            Toast.makeText(registration.this, "Please select a role", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(nameText)) {
            Toast.makeText(registration.this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(emailText)) {
            Toast.makeText(registration.this, "Please enter email id", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(passwordText)) {
            Toast.makeText(registration.this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        loadingBar.setTitle("Creating New Account");
        loadingBar.setMessage("Please wait,creating new Account");
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();

        UserDetails userDetails;
        if (role.equals("user")) {
            userDetails = new UserDetails(nameText, nationalIdText, emailText, residentialAddressText,
                    "", contactsText, role);
        } else {
            userDetails = new UserDetails(nameText, "", emailText, "",
                    physicalAddressText, contactsText, role);
        }

        appAuth.createUserWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser currentUser = appAuth.getCurrentUser();
                    String userId = currentUser.getUid();

                    userDetails.setRole(role);

                    usersRef.document(userId).set(userDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                redirectUserToLogin();
                                Toast.makeText(registration.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                String msg = task.getException().getMessage();
                                Toast.makeText(registration.this, "Error: " + msg, Toast.LENGTH_SHORT).show();
                            }
                            loadingBar.dismiss();
                        }
                    });
                } else {
                    String msg = task.getException().getMessage();
                    Toast.makeText(registration.this, "Error: " + msg, Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
        });
    }

    private void redirectUserToLogin() {
        Intent loginIntent = new Intent(registration.this, logIn.class);
        startActivity(loginIntent);
    }
}