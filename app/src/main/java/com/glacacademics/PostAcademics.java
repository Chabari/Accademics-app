package com.glacacademics;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PostAcademics extends AppCompatActivity {

    private Button mSubmit;
    private ImageView mImage;
    private EditText mWhats;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private String user_id,post;
    private ProgressDialog progressDialog;
    private StorageReference storageReference;
    private Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_academics);

        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user_id = auth.getCurrentUser().getUid();
        progressDialog = new ProgressDialog(this);
        storageReference= FirebaseStorage.getInstance().getReference();

        mImage  =(ImageView)findViewById(R.id.imagePicked);
        mSubmit = (Button)findViewById(R.id.btnPostAcademicsGen);
        mWhats = (EditText)findViewById(R.id.edtPostDescAcademicsGen);

        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(PostAcademics.this);
            }
        });




        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post = mWhats.getText().toString().trim();
                if (TextUtils.isEmpty(post)){
                    mWhats.setError("Enter post...");
                }else {
                    mSubmit.setEnabled(false);
                    progressDialog.setMessage("Uploading your post...");
                    progressDialog.show();

                    //getting university value
                   firebaseFirestore.collection("UniversityRegistration").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                       @Override
                       public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                           if (task.isSuccessful()){
                               if (task.getResult().exists()){
                                   final String regno = task.getResult().getString("regno");
                                   final String university = task.getResult().getString("university");
                                   firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                       @Override
                                       public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                           if (task.isSuccessful()){
                                               if(task.getResult().exists()){
                                                   String fname = task.getResult().getString("fname");
                                                   String lname = task.getResult().getString("lname");

                                                   final String fullname = "@"+fname+""+lname;
                                                   if (imageUri==null){
                                                       String imUrl = "";
                                                       postingToGeneral(university,post,regno,fullname.toLowerCase(),imUrl);

                                                   }else {

                                                       String random = UUID.randomUUID().toString();
                                                       final StorageReference reference = storageReference.child(user_id + "Academics").child(random + ".jpg");
                                                       reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                           @Override
                                                           public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                               reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                   @Override
                                                                   public void onSuccess(Uri uri) {

                                                                       Uri dwnld = uri;
                                                                       postingToGeneral(university, post, regno, fullname.toLowerCase(), dwnld.toString());
                                                                   }
                                                               });
                                                           }
                                                       });
                                                   }
                                               }
                                           }
                                       }
                                   });
                               }
                           }else {
                               String error = task.getException().toString();
                               Toast.makeText(PostAcademics.this, "Somethingwent wrong\n"+error, Toast.LENGTH_SHORT).show();
                           }
                       }
                   });
                }
            }
        });


    }

    private void postingToGeneral(String university,String post,String regno,String name, String image_url){

        Map<String,Object> postMap = new HashMap<>();
        postMap.put("post",post);
        postMap.put("regno",regno);
        postMap.put("fullname",name);
        postMap.put("image_url",image_url);
        postMap.put("timeStamp", FieldValue.serverTimestamp());

        firebaseFirestore.collection("Universities").document(university).collection("GeneralPost").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    onBackPressed();
                    Toast.makeText(PostAcademics.this, "Post submitted successfully", Toast.LENGTH_SHORT).show();

                }else {
                    progressDialog.dismiss();
                    mSubmit.setEnabled(true);
                    String error = task.getException().toString();
                    Toast.makeText(PostAcademics.this, "Something went wrong: "+error, Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                mImage.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }


    }
}
