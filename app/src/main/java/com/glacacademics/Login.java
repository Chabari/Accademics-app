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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private TextView tvsignuplogin,tvforgotpasslogin;
    private EditText edtemailLogin,edtpasswordlogin;
    private Button btnlogin;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;

    private static final String TAG = "FAXELOG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth=FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        tvsignuplogin=(TextView)findViewById(R.id.tvsignuplogin);
        tvforgotpasslogin=(TextView)findViewById(R.id.tvforgotpasslogin);
        edtemailLogin=(EditText)findViewById(R.id.edtemailLogin);
        edtpasswordlogin=(EditText)findViewById(R.id.edtpasswordlogin);
        btnlogin=(Button)findViewById(R.id.btnlogin);

        tvsignuplogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), signup.class);
                startActivity(intent);
            }
        });

        tvforgotpasslogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),forgot_password.class);
                startActivity(intent);
            }
        });
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtemailLogin.getText().toString();
                final String password = edtpasswordlogin.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog.setMessage("Authenticating...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();

                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();

                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        edtpasswordlogin.setError(getString(R.string.minimum_password));
                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(Login.this, error, Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Intent intent = new Intent(Login.this, home.class);
                                    startActivity(intent);
                                    finish();
                                }

                            }
                        });

            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser !=null)

        {
            updateUI();
        }
    }

    private void updateUI() {

        Intent intent = new Intent(Login.this, home.class);
        startActivity(intent);
        finish();

    }
}
