package com.glacacademics;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Transport_People extends AppCompatActivity {

    private FirebaseAuth auth;
    private String user_id,Transport;
    private FirebaseFirestore firebaseFirestore;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport__people);

        setTitle("Glac Moves");

        auth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        user_id=auth.getCurrentUser().getUid();

        checkingRegistration(user_id);
    }

    private void checkingRegistration(String user_id) {

        firebaseFirestore.collection("Transporter").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){

                }else {
                    startActivity(new Intent(Transport_People.this,Transport_registration.class));
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkingRegistration(auth.getCurrentUser().getUid());
    }
}
