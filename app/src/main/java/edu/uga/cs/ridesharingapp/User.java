package edu.uga.cs.ridesharingapp;

public class User {

    private String id;
    private String email;


    public User(String id, String email){
        this.id = id;
        this.email = email;
    }


    //getter and setter methods
    public String getID(){return id;}

    public String getEmail(){return email;}

    public void setID(String id){this.id = id;}

    public void setEmail(String Email){this.email = email;}
}
