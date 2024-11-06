package com.example.watermanagementsystem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserRoleManager {
    private static final String USERS_COLLECTION = "users";

    public interface RoleCallback {
        void onRoleChecked(boolean isWaterAuthority);
    }

    public static void checkUserRole(RoleCallback callback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            callback.onRoleChecked(false);
            return;
        }

        FirebaseFirestore.getInstance()
                .collection(USERS_COLLECTION)
                .document(auth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(document -> {
                    String role = document.getString("role");
                    callback.onRoleChecked("waterAuthority".equals(role));
                })
                .addOnFailureListener(e -> callback.onRoleChecked(false));
    }
}
