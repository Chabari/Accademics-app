package com.glacacademics;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

/**
 * Created by George on 2/7/2019.
 */

public class Post_gen_adapter  extends RecyclerView.Adapter<Post_gen_adapter.Viholder> {

    private Context context;
    private List<Post_list_gen> post_list_gens;
    private FirebaseFirestore firebaseFirestore;
    private String imageurl=null;
    private FirebaseAuth auth;
    private String user_id;

    public Post_gen_adapter(List<Post_list_gen> post_list_gens) {
        this.post_list_gens = post_list_gens;
    }

    @NonNull
    @Override
    public Viholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gen_post_item, viewGroup, false);
        context = viewGroup.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user_id=auth.getCurrentUser().getUid();
        return new Viholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Viholder holder, int position) {

       /* try{

            long milliseconds = post_list_gens.get(position).getTimeStamp().getTime();
            Date current = Calendar.getInstance().getTime();

            long diff = current.getTime() - milliseconds;
            long sec = diff/1000;
            long min = sec/60;
            long hr = min/24;


            String dateString = DateFormat.format("dd/MM/yyyy",new Date(milliseconds)).toString();
            holder.setTime(dateString);

        }catch (Exception e){
            Toast.makeText(context, "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }*/

        final String postId = post_list_gens.get(position).PostId;


        final String fullname = post_list_gens.get(position).getFullname();
        holder.setFullname(fullname);


        imageurl= post_list_gens.get(position).getImage_url();
        if (imageurl == null) {

            holder.imagePosted.setVisibility(View.GONE);
        }else {
            holder.setImagePosted(imageurl);
        }


        final String regno = post_list_gens.get(position).getRegno();
        holder.setRegno(regno);

        final String post = post_list_gens.get(position).getPost();
        holder.setPost(post);

        final String phone=post_list_gens.get(position).getPhone();



        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(),Commenting.class);
                intent.putExtra("postId",postId);
                intent.putExtra("post",post);
                v.getContext().startActivity(intent);
            }
        });

        firebaseFirestore.collection("UniversityRegistration").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        String university=task.getResult().getString("university");

                        firebaseFirestore.collection("Universities").document(university).collection("Commenting")
                                .document(postId).collection("comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                                if (!documentSnapshots.isEmpty()) {

                                    int count = documentSnapshots.size();
                                    holder.setComents(count+" Comment(s)");

                                } else {

                                    holder.setComents(0+" Comment(s)");

                                }

                            }
                        });

                    }
                }
            }
        });

        holder.mComents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),Commenting.class);
                intent.putExtra("postId",postId);
                intent.putExtra("post",post);
                v.getContext().startActivity(intent);
            }
        });

        holder.imagePosted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),Post_item_display_gen.class);
                intent.putExtra("regno",regno);
                intent.putExtra("fullname",fullname);
                intent.putExtra("post",post);
                intent.putExtra("imageUrl",imageurl);
                intent.putExtra("user_id",user_id);
                intent.putExtra("phone",phone);
                v.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return post_list_gens.size();
    }

    public class Viholder extends RecyclerView.ViewHolder {
        private TextView fullname, regno, post,time,mComents;
        private View view;
        private ImageView comment,imagePosted;

        public Viholder(@NonNull View itemView) {
            super(itemView);

            view = itemView;
            comment = (ImageView)view.findViewById(R.id.imageViewComment);
            imagePosted = (ImageView)view.findViewById(R.id.imageViewPostGen);
            mComents=(TextView)view.findViewById(R.id.tvComments);

        }

        public void setFullname(String Fullname){
            fullname = (TextView)view.findViewById(R.id.tvFullnamePostGen);
            fullname.setText(Fullname);
        }

        public void setRegno(String Regno){
            regno = (TextView)view.findViewById(R.id.tvRegNoGen);
            regno.setText(Regno);
        }

        public void setPost(String Post){
            post = (TextView)view.findViewById(R.id.tvPostGen);
            post.setText(Post);
        }

        public void setTime(String Time){
            time = (TextView)view.findViewById(R.id.tvTimeStampGen);
            time.setText(Time);
        }

        public void setImagePosted(String image_url) {

            imagePosted = (ImageView)view.findViewById(R.id.imageViewPostGen);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.centerCrop();
            requestOptions.placeholder(R.color.lightGrray);

            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(image_url).into(imagePosted);
        }
        public void setComents(String Coments){
            mComents=(TextView)view.findViewById(R.id.tvComments);
            mComents.setText(Coments);
        }
    }


}
