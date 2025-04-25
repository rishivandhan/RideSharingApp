package edu.uga.cs.ridesharingapp;

public class User {

    private String id;
    private String email;
    private int points;


    public User(String id, String email, int points){
        this.id = id;
        this.email = email;
        this.points = points;
    }


    //getter and setter methods
    public String getID(){return id;}

    public String getEmail(){return email;}

    public int getPoints(){return points;}

    public void setID(String id){this.id = id;}

    public void setEmail(String email){this.email = email;}

    public void setPoints(int points){this.points = points;}
}
