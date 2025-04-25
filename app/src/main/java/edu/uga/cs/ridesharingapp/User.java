package edu.uga.cs.ridesharingapp;

public class User {

    private String id;
    private String email;
    private int points;

    private boolean driver;
    private boolean rider;



    public User(String id, String email, int points, boolean driver, boolean rider){
        this.id = id;
        this.email = email;
        this.points = points;
        this.rider = rider;
        this.driver = driver;
    }


    //getter and setter methods
    public String getID(){return id;}

    public String getEmail(){return email;}

    public int getPoints(){return points;}

    public boolean getRider(){return rider;}

    public boolean getDriver(){return driver;}

    public void setID(String id){this.id = id;}

    public void setEmail(String email){this.email = email;}

    public void setPoints(int points){this.points = points;}

    public void setRider(boolean rider){this.rider = rider;}

    public void setDriver(boolean driver){this.driver = driver;}

}
