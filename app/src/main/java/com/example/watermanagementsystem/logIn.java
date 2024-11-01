package com.example.watermanagementsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class logIn extends AppCompatActivity {
    private EditText useremail, userpassword;
    private Button loginButton;
    private TextView register;
    private ProgressDialog loadingBar;
    FirebaseUser currentUser;
    private FirebaseAuth appAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        appAuth = FirebaseAuth.getInstance();
        currentUser = appAuth.getCurrentUser ();

        useremail = findViewById(R.id.userEmail);
        userpassword = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        register = findViewById(R.id.signupRedirectText);


        loadingBar = new ProgressDialog(this);

        loadingBar.setCanceledOnTouchOutside(false);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(logIn.this, registration.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser() {
        String email = useremail.getText().toString().trim();
        String password = userpassword.getText().toString().trim();

        if ( TextUtils.isEmpty (email) ) {
            Toast.makeText (logIn.this, "Please enter email id", Toast.LENGTH_SHORT).show ();
        } else if ( TextUtils.isEmpty (password) ) {
            Toast.makeText (logIn.this, "Please enter password", Toast.LENGTH_SHORT).show ();
        } else {
            loadingBar.setTitle("Logging In");
            loadingBar.setMessage("Please wait while we check your credentials...");
            loadingBar.show ();
            appAuth.signInWithEmailAndPassword (email, password)
                    .addOnCompleteListener (new OnCompleteListener <AuthResult> () {
                        @Override
                        public void onComplete (@NonNull Task <AuthResult> task) {
                            if ( task.isSuccessful () ) {
                                checkUserRoleAndRedirect ();
                            } else {
                                String msg = task.getException ().toString ();
                                Toast.makeText (logIn.this, "Error: " + msg, Toast.LENGTH_SHORT).show ();
                                loadingBar.dismiss ();
                            }
                        }
                    });
        }
    }

    private void checkUserRoleAndRedirect() {
        FirebaseUser user = appAuth.getCurrentUser ();
        if ( user != null ) {
            FirebaseFirestore db = FirebaseFirestore.getInstance ();
            DocumentReference userRef = db.collection ("users").document (user.getUid ());
            userRef.get ().addOnSuccessListener (new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess (DocumentSnapshot documentSnapshot) {
                    if ( documentSnapshot.exists () ) {
                        String role = documentSnapshot.getString ("role");
                        if ( role != null ) {
                            if ( role.equals ("Admin") ) {
                                sendToAdminActivity ();
                            } else if ( role.equals ("WaterAuthority") ) {
                                sendToWaterAuthorityActivity ();
                            }
                            else if ( role.equals ("User") ) {
                                sendToResidentsActivity ();
                            } else {
                                Toast.makeText (logIn.this, "Unknown user role", Toast.LENGTH_SHORT).show ();
                            }
                        } else {
                            Toast.makeText (logIn.this, "User role not found", Toast.LENGTH_SHORT).show ();
                        }
                    } else {
                        Toast.makeText (logIn.this, "User document does not exist", Toast.LENGTH_SHORT).show ();
                    }
                    loadingBar.dismiss ();
                }
            }).addOnFailureListener (new OnFailureListener () {
                @Override
                public void onFailure (@NonNull Exception e) {
                    Toast.makeText (logIn.this, "Failed to fetch user role: " + e.getMessage (), Toast.LENGTH_SHORT).show ();
                    loadingBar.dismiss ();
                }
            });
        }
    }
    private void sendToAdminActivity() {
        Intent adminIntent = new Intent(logIn.this, AdminPage.class);
        startActivity(adminIntent);
        finish();
    }
    private void sendToWaterAuthorityActivity() {
        Intent waterAuthorityIntent = new Intent(logIn.this, WaterAuthorityPage.class);
        startActivity(waterAuthorityIntent);
        finish();
    }
    private void sendToResidentsActivity() {
        Intent userIntent = new Intent(logIn.this, residentsHomepage.class);
        startActivity(userIntent);
        finish();
    }
}