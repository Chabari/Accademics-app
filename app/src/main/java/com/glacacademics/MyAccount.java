package com.glacacademics;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAccount extends AppCompatActivity {
    private CircleImageView mprofile;
    private TextInputEditText mEmail,mIdnumber,mPhone,mfname,mlname;
    private Button mSubmit; private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private Uri imageUri = null;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        progressDialog = new ProgressDialog(this);

        storageReference = FirebaseStorage.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user_id = auth.getCurrentUser().getUid();

        mSubmit = (Button) findViewById(R.id.submit_profile);
        mfname = (TextInputEditText)findViewById(R.id.edt_firstname_account);
        mlname = (TextInputEditText)findViewById(R.id.edt_lastname_account);
        mIdnumber = (TextInputEditText)findViewById(R.id.edt_id_account);
        mPhone = (TextInputEditText)findViewById(R.id.edt_phone_number_account);
        mEmail = (TextInputEditText)findViewById(R.id.edt_email_account);
        mprofile =(CircleImageView) findViewById(R.id.imagemyaccount);

        setTitle("My Account");

        mprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(MyAccount.this);
            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=mEmail.getText().toString().trim();
                String fname=mfname.getText().toString().trim();
                String lname=mlname.getText().toString().trim();
                String idnumber=mIdnumber.getText().toString().trim();
                String phone=mPhone.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(MyAccount.this, "Enter email address", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(fname)){
                    Toast.makeText(MyAccount.this, "Enter first name", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(lname)){
                    Toast.makeText(MyAccount.this, "Enter last name", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(idnumber)){
                    Toast.makeText(MyAccount.this, "Enter id number", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(phone)){
                    Toast.makeText(MyAccount.this, "Enter phone number", Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog.setMessage("Submitting details");
                    progressDialog.show();
                    uploadimage();
                    fireStoreUpload(null,fname,lname,idnumber,phone,email);
                }
            }
        });


    }

   private void uploadimage() {

        if (imageUri!=null){
            final StorageReference reference = storageReference.child("ProfileImages").child(user_id+".jpg");
            reference.putFile(imageUri);
        }else {
            Toast.makeText(this, "Image cannot be empty..", Toast.LENGTH_SHORT).show();
        }
    }

    private void fireStoreUpload(@NonNull Task<UploadTask.TaskSnapshot> task, final String fname, final String lname, final String idnumber, final String phone, final String email) {


        StorageReference reference = storageReference.child("ProfileImages").child(user_id+".jpg");
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Map<String, String> stringMap = new HashMap<>();
                stringMap.put("fname", fname);
                stringMap.put("lname",lname);
                stringMap.put("id_number",idnumber);
                stringMap.put("phone",phone);
                stringMap.put("email",email);
                stringMap.put("imageUrl",uri.toString());

                firebaseFirestore.collection("Users").document(user_id).set(stringMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"User details Submitted",Toast.LENGTH_LONG);
                            Intent intent=new Intent(MyAccount.this,home.class);
                            startActivity(intent);
                            //onBackPressed();


                        }else {
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(getApplicationContext(),"Server error!..\n"+errorMessage,Toast.LENGTH_LONG);
                        }

                        progressDialog.dismiss();

                    }
                });
            }
        });



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri  = result.getUri();
                mprofile.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
