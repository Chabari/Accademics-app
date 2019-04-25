package com.glacacademics;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import static java.security.AccessController.getContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class Accademics extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private List<Post_list_gen> post_list_gens;
    private Post_gen_adapter post_gen_adapter;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String user_id,university;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_accademics, container, false);

        auth=FirebaseAuth.getInstance();
        user_id = auth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();

        post_list_gens = new ArrayList<>();
        post_gen_adapter = new Post_gen_adapter(post_list_gens);

        swipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.swipe11);

        recyclerView = (RecyclerView)view.findViewById(R.id.recycleViewAcademics);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(post_gen_adapter);
        floatingActionButton = (FloatingActionButton)view.findViewById(R.id.fbPost);


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),PostAcademics.class));
            }
        });
        firebaseFirestore.collection("UniversityRegistration").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        university= task.getResult().getString("university");
                        Toast.makeText(getContext(), university, Toast.LENGTH_SHORT).show();
                        gettingValues(university);
                    }
                }
            }
        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        swipeRefreshLayout.setRefreshing(false);
                        recyclerView.setAdapter(post_gen_adapter);

                    }
                },300);
            }
        });
        return view;

    }
    private void gettingValues(String University){

        Query query = firebaseFirestore.collection("Universities").document(University).collection("GeneralPost").orderBy("timeStamp",Query.Direction.DESCENDING);
        query.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for (DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){

                    if (doc.getType() == DocumentChange.Type.ADDED) {

                        String postID = doc.getDocument().getId();
                        Post_list_gen postItem = doc.getDocument().toObject(Post_list_gen.class).withId(postID);

                        post_list_gens.add(postItem);

                        post_gen_adapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(getContext(), "No post for ", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

}
