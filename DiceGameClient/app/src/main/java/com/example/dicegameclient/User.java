package com.example.dicegameclient;

/**
 * Created by Acer on 03/12/2015.
 */
public class User {
    String id;
    String name;
    String serverIP;

    public User(){
        id = "Invalid";
        name = "Enter a name";
        serverIP = "localhost";
    }

    public User(String id, String name){
        this.id = id;
        this.name = name;
        serverIP = "localhost";
    }

    public boolean isValid(){
        if(name == null || id == null){
            return false;
        }

        if(name.isEmpty()){
            return false;
        }else if(id.isEmpty()){
            return false;
        }
        return true;
    }

    @Override
    public String toString(){
        return "User:\nid: " + id + "\nName: " + name;
    }
}
