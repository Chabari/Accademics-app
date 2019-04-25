package com.glacacademics;

import java.util.Date;

/**
 * Created by George on 3/23/2019.
 */

public class Commenting_list extends PostId {
    private String regno,comment;
    private Date timeStamp;

    public Commenting_list() {
    }

    public Commenting_list(String regno, String comment, Date timeStamp) {
        this.regno = regno;
        this.comment = comment;
        this.timeStamp = timeStamp;
    }

    public String getRegno() {
        return regno;
    }

    public void setRegno(String regno) {
        this.regno = regno;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}
