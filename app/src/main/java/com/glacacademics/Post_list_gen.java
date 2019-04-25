package com.glacacademics;

import java.util.Date;

/**
 * Created by George on 2/7/2019.
 */

public class Post_list_gen extends PostId {
    String fullname,post,regno,image_url,phone;
    Date timeStamp;

    public Post_list_gen(){

    }

    public Post_list_gen(String fullname, String post, String regno, String image_url, String phone, Date timeStamp) {
        this.fullname = fullname;
        this.post = post;
        this.regno = regno;
        this.image_url = image_url;
        this.phone = phone;
        this.timeStamp = timeStamp;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getRegno() {
        return regno;
    }

    public void setRegno(String regno) {
        this.regno = regno;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}
