package com.example.heartrate_android;

/**
 * @author amanjotsingh
 * Class to capture the x, y, z values and timestamp*/

public class UserPatient {
    //fields
    private long UserTimeStamp;
    private float UserXVal;
    private float UserYVal;
    private float UserZVal;
    // constructors
    public UserPatient() {}
    public UserPatient(long timestamp, float xval, float yval, float zval) {
        this.UserTimeStamp = timestamp;
        this.UserXVal = xval;
        this.UserYVal = yval;
        this.UserZVal = zval;

    }
    // setters and getters
    public void setTimestamp(long timeStamp) {
        this.UserTimeStamp = timeStamp;
    }
    public void setXVal(float x) {
        this.UserXVal = x;
    }
    public void setYVal(float y) {
        this.UserYVal = y;
    }

    public void setZVal(float z) {
        this.UserZVal = z;
    }
    public long getTimestamp() {
        return this.UserTimeStamp;
    }

    public float getXVal() {
        return this.UserXVal;
    }
    public float getYVal() {
        return this.UserYVal;
    }
    public float getZVal() {
        return this.UserZVal;
    }
}

