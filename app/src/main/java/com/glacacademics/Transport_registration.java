package com.glacacademics;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.UUID;

public class Transport_registration extends AppCompatActivity {

    private EditText mfullname,mid,mplate;
    private Spinner mtype;
    private ImageView mprofile;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private ProgressDialog progressDialog;
    private String user_id,Transport;
    private Button msubmit;
    private Uri imageUri = null;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_registration);


        storageReference= FirebaseStorage.getInstance().getReference();
        auth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        user_id=auth.getCurrentUser().getUid();
        progressDialog =new ProgressDialog(Transport_registration.this);
        mfullname =(EditText)findViewById(R.id.edtFullName);
        mid=(EditText)findViewById(R.id.edtid_number);
        mplate=(EditText)findViewById(R.id.edtnumberplate);
        mprofile=(ImageView)findViewById(R.id.profileImage);
        mtype=(Spinner)findViewById(R.id.spintype);
        msubmit=(Button)findViewById(R.id.btntransreg);

        mtype.setSelection(0);


        mprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(Transport_registration.this);

            }
        });

        msubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String fullname=mfullname.getText().toString().trim();
                final String idnumber=mid.getText().toString().trim();
                final String numberplate=mplate.getText().toString().trim();
                final String type=mtype.getSelectedItem().toString();

                if (TextUtils.isEmpty(fullname)){
                    mfullname.setError("Enter your name...");
                }else if (TextUtils.isEmpty(idnumber)){
                    mid.setError("Enter your id number..");
                }else if (TextUtils.isEmpty(numberplate)){
                    mplate.setError("Enter number plate..");
                }else if (mtype.getSelectedItemPosition()==0){
                    Toast.makeText(Transport_registration.this,"Enter type of your transport..", Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog.setMessage("Submitting your details..");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.show();

                    final String randomName = UUID.randomUUID().toString();
                    final StorageReference reference = storageReference.child(user_id+" user_images").child(randomName+".jpg");
                    reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri download_url = uri;

                                    registration(null,type,numberplate,idnumber,fullname,download_url.toString());
                                }
                            });

                        }
                    });

                }

            }
        });
    }

    private void registration(@NonNull Task<UploadTask.TaskSnapshot> task, String type, String numberplate, String idnumber, String fullname, String dowload_uri) {

        Map<String,String> detailsMap = new HashMap<>();
        detailsMap.put("Car type",type);
        detailsMap.put("Number Plate",numberplate);
        detailsMap.put("ID Number",idnumber);
        detailsMap.put("Full Name",fullname);
        detailsMap.put("imageUrl",dowload_uri);

        firebaseFirestore.collection("Transporter").document(user_id).set(detailsMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    Toast.makeText(Transport_registration.this, "Successfully registered..", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(Transport_registration.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                mprofile.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
