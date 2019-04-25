package com.glacacademics;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

/**
 * Created by George on 2/7/2019.
 */

public class PostId {
    @Exclude
    public String PostId;

    public <T extends PostId > T withId(@NonNull final String id){
        this.PostId = id;
        return  (T) this;
    }
}
