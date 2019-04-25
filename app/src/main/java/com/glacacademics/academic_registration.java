package com.glacacademics;


import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class academic_registration extends Fragment {

    private Button mSubmit;
    private EditText mRegno;
    private Spinner mUniversity,mCourse,mYos;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private String user_id,university,course,yos,regno;
    private ProgressDialog progressDialog;
    private View view;


    public academic_registration() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_academic_registration, container, false);

        auth=FirebaseAuth.getInstance();
        user_id = auth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(getContext());

        //initializingtems
        mSubmit= (Button)view.findViewById(R.id.btnSubmitAcademics1);
        mCourse =  (Spinner)view.findViewById(R.id.spn_course1);
        mUniversity = (Spinner)view.findViewById(R.id.spn_university1);
        mRegno = (EditText)view.findViewById(R.id.edt_regno1);
        mYos = (Spinner)view.findViewById(R.id.spn_yos1);

        //setting the initail selection to spinners
        mCourse.setSelection(0);
        mUniversity.setSelection(0);
        mYos.setSelection(0);

        //button click
        mSubmit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                university = mUniversity.getSelectedItem().toString();
                regno = mRegno.getText().toString().trim();
                course = mCourse.getSelectedItem().toString();
                yos = mYos.getSelectedItem().toString();
                if (TextUtils.isEmpty(regno)){
                    mRegno.setError("Enter your registration number..");
                }else if (mUniversity.getSelectedItemPosition()== 0){
                    Toast.makeText(getContext(), "Select university...", Toast.LENGTH_SHORT).show();
                }else if (mCourse.getSelectedItemPosition()== 0){
                    Toast.makeText(getContext(), "Select Course...", Toast.LENGTH_SHORT).show();
                }else if (mYos.getSelectedItemPosition()== 0){
                    Toast.makeText(getContext(), "Select Year of Study...", Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog.setMessage("Please wait submitting your details...");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.show();
                    registration(regno,university,course,yos);
                }
            }
        });

        return view;
    }
    private void registration(String regno, final String university, String course, String yos){
        Map<String,String> detailsMap = new HashMap<>();
        detailsMap.put("regno",regno);
        detailsMap.put("university",university);
        detailsMap.put("course",course);
        detailsMap.put("yos",yos);

        firebaseFirestore.collection("UniversityRegistration").document(user_id).set(detailsMap).addOnCompleteListener(new OnCompleteListener<Void>() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()){
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Details submitted successfully", Toast.LENGTH_SHORT).show();
            }else {
                progressDialog.dismiss();
                String error = task.getException().toString();
                Toast.makeText(getContext(), "Something went wrong: "+error, Toast.LENGTH_SHORT).show();
            }
        }
    });


}

}
