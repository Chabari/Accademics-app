package com.glacacademics;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class signup extends AppCompatActivity {

    private TextView tvhaveaccount;
    private EditText edtemailSignup, edtpasswordsignup, edtCpasswordsignup;
    private Button btnsignup;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;
    private View view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        progressDialog = new ProgressDialog(this);

        progressDialog = new ProgressDialog(this);
        edtemailSignup = (EditText) findViewById(R.id.edtemailSignup);
        edtpasswordsignup = (EditText) findViewById(R.id.edtpasswordsignup);
        edtCpasswordsignup = (EditText) findViewById(R.id.edtCpasswordsignup);
        btnsignup = (Button) findViewById(R.id.btnsignup);
        tvhaveaccount = (TextView) findViewById(R.id.tvhaveaccount);
        auth = FirebaseAuth.getInstance();


        tvhaveaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }
        });

        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = edtemailSignup.getText().toString().trim();
                String password = edtpasswordsignup.getText().toString().trim();
                String CPassword = edtCpasswordsignup.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    //Toast.makeText(getApplicationContext(), "Enter email address...!", Toast.LENGTH_SHORT).show();
                    edtemailSignup.setError("Enter email address...!");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    //Toast.makeText(getApplicationContext(), "Enter password...!", Toast.LENGTH_SHORT).show();
                    edtpasswordsignup.setError("Enter password...!");
                    return;
                }

                if (password.length() < 6) {
                    //Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters...!", Toast.LENGTH_SHORT).show();
                    edtpasswordsignup.setError("Password too short, enter minimum 6 characters...!");
                    return;
                }
                if (!password.equals(CPassword)) {
                    Toast.makeText(getApplicationContext(), "Passwords don't match...!", Toast.LENGTH_SHORT).show();
                    edtpasswordsignup.setError("Passwords don't match...!");
                    edtCpasswordsignup.setError("Passwords don't match...!");
                    return;
                }
                progressDialog.setMessage("Creating Account...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();

                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(signup.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    Toast.makeText(signup.this, "Account Created", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(signup.this, home.class));
                                } else {
                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                                }

                                progressDialog.dismiss();

                            }
                        });
            }
        });
    }
}




