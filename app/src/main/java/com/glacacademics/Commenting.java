package com.glacacademics;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class Commenting extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText mComment;
    private Button mSend;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;
    private String user_id,university,postId,regno,post;
    private List<Commenting_list> commentLists;
    private Commenting_Addapter commenting_addapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commenting);


        commentLists = new ArrayList<>();
        commenting_addapter = new Commenting_Addapter(commentLists);

        auth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        user_id=auth.getCurrentUser().getUid();
        mComment=(EditText)findViewById(R.id.edtComment);
        mSend=(Button)findViewById(R.id.btnComment);

        recyclerView=(RecyclerView)findViewById(R.id.recyClerCommenting);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(commenting_addapter);
        gettingAllComments();

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment=mComment.getText().toString().trim();
                if (TextUtils.isEmpty(comment)){
                    Toast.makeText(Commenting.this, "Write a comment to send", Toast.LENGTH_SHORT).show();
                }else {
                    mComment.setText("");
                    sending(comment);

                }
            }
        });
        postId=getIntent().getExtras().getString("postId");
        post=getIntent().getExtras().getString("post");



    }


    private void sending(final String comment) {

        //getting user details
        firebaseFirestore.collection("UniversityRegistration").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                    if (task.getResult().exists()){

                        university = task.getResult().getString("university");
                        regno = task.getResult().getString("regno");

                        Map<String, Object> stringMap = new HashMap<>();
                        stringMap.put("regno",regno);
                        stringMap.put("comment",comment);
                        stringMap.put("timeStamp", FieldValue.serverTimestamp());

                        firebaseFirestore.collection("Universities").document(university).collection("Commenting")
                                .document(postId).collection("comments").add(stringMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {

                                if (task.isSuccessful()){
                                    Toast.makeText(Commenting.this,"Sent...",Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(Commenting.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
                                }

                            }
                        });


                    }
                }
            }
        });

    }

    private void gettingAllComments(){

        firebaseFirestore.collection("UniversityRegistration").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                    if (task.getResult().exists()){

                        String  university = task.getResult().getString("university");

                        Query query = firebaseFirestore.collection("Universities").document(university).collection("Commenting")
                                .document(postId).collection("comments").orderBy("timeStamp", Query.Direction.ASCENDING);
                        query.addSnapshotListener(Commenting.this, new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                                for (DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){
                                    if (doc.getType() == DocumentChange.Type.ADDED){

                                        String postId = doc.getDocument().getId();
                                        Commenting_list commentList  = doc.getDocument().toObject(Commenting_list.class).withId(postId);

                                        commentLists.add(commentList);
                                        commenting_addapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        });


                    }
                }
            }
        });


    }
}
