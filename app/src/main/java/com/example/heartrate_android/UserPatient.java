package com.example.heartrate_android;

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
    // properties
    public void setTimestamp(long timeStamp) {
        this.UserTimeStamp = timeStamp;
    }
    public long getTimestamp() {
        return this.UserTimeStamp;
    }
    public void setXValues(float x) {
        this.UserXVal = x;
    }
    public float getXValues() {
        return this.UserXVal;
    }
    public void setYValues(float y) {
        this.UserYVal = y;
    }
    public float getYValues() {
        return this.UserYVal;
    }
    public void setZValues(float z) {
        this.UserZVal = z;
    }
    public float getZValues() {
        return this.UserZVal;
    }
}

