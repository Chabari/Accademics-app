package com.glacacademics;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String University,Fullname,lname,fname,email,image_url,user_id;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private CircleImageView mprofile;
    private TextView mEmail,mUsername;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();
        user_id = auth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        detailsLoading();

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        lname=task.getResult().getString("lname");
                        fname=task.getResult().getString("fname");
                        Fullname=fname+" "+lname;
                        setTitle(Fullname);
                        Accademics academics = new Accademics();
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.main,academics).commit();
                    }else {
                        Toast.makeText(home.this, "Please register for a University", Toast.LENGTH_SHORT).show();
                        setTitle("Registration");
                        academic_registration academicRegstration = new academic_registration();
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.main,academicRegstration).commit();
                    }
                }

            }
        });
    }

    @Override
    protected void onStart() {

        super.onStart();
        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();


        if (current_user != null)
        {
            firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful())
                    {
                        if (!task.getResult().exists())
                        {
                            startActivity(new Intent(home.this,MyAccount.class));

                        }

                    }

                }
            });

        }
    }

    private void detailsLoading() {

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        lname=task.getResult().getString("lname");
                        fname=task.getResult().getString("fname");
                        Fullname=fname+" "+lname;

                        image_url=task.getResult().getString("imageUrl");
                        email=task.getResult().getString("email");


                        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                        View view = navigationView.getHeaderView(0);

                        mUsername=(TextView)view.findViewById(R.id.tvUserName);
                        mEmail=(TextView)view.findViewById(R.id.tvUserEmail);
                        mprofile=(CircleImageView)view.findViewById(R.id.imageprofnav);


                        if (image_url != null){

                            try{
                                RequestOptions requestOptions = new RequestOptions();
                                requestOptions.centerCrop();
                                requestOptions.placeholder(R.color.lightGrray);

                                Glide.with(home.this).applyDefaultRequestOptions(requestOptions).load(image_url).into(mprofile);

                            }catch (Exception e){
                                Toast.makeText(home.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }


                        mUsername.setText(Fullname);
                        mEmail.setText(email);


                     /*  mprofile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // startActivity(new Intent(MainPanelActivity.this,UserProfile.class));
                                Intent intent = new Intent(view.getContext(), UserProfile.class);
                                intent.putExtra("image_url",image_url);
                                view.getContext().startActivity(intent);
                            }
                        });*/
                    }
                }

            }
        });
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_accademics) {
            // Handle the camera action

            Accademics accademics = new Accademics();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main,accademics).commit();
            setTitle(Fullname);
        } else if (id == R.id.nav_registration) {
            setTitle("Registration");
            academic_registration academicRegstration = new academic_registration();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main,academicRegstration).commit();

        } else if (id == R.id.nav_transport) {

            setTitle("Transport");
            transport transport = new transport();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main,transport).commit();

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
