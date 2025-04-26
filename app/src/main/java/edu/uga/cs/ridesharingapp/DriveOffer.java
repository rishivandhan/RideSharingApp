package edu.uga.cs.ridesharingapp;

import java.sql.Driver;

public class DriveOffer {
    private String key;
    private String riderid;
    private String driverid;
    private long date;
    private String startLocation;
    private String endLocation;
    private boolean accepted;
    private boolean riderConfirm;
    private boolean driverConfirm;

    public DriveOffer(){
        this.key = null;
        this.riderid = null;
        this.driverid = null;
        this.date = 0;
        this.startLocation = null;
        this.endLocation = null;
        this.accepted = false;
        this.riderConfirm = false;
        this.driverConfirm = false;
    }

    public DriveOffer(long date, String startLocation, String endLocation){
        this.key = null;
        this.riderid = null;
        this.driverid = null;
        this.date = date;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.accepted = false;
        this.riderConfirm = false;
        this.driverConfirm = false;
    }

    public String getKey() {
        return key;
    }

    public String getRiderid() {
        return riderid;
    }

    public String getDriverid() {
        return driverid;
    }

    public long getDate() {
        return date;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public boolean isRiderConfirm() {
        return riderConfirm;
    }

    public boolean isDriverConfirm() {
        return driverConfirm;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setRiderid(String riderid) {
        this.riderid = riderid;
    }

    public void setDriverid(String driverid) {
        this.driverid = driverid;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public void setRiderConfirm(boolean riderConfirm) {
        this.riderConfirm = riderConfirm;
    }

    public void setDriverConfirm(boolean driverConfirm) {
        this.driverConfirm = driverConfirm;
    }
}



