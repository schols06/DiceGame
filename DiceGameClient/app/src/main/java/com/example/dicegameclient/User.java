package com.example.dicegameclient;

import android.app.Application;
import android.provider.Settings;

/**
 * Created by Acer on 03/12/2015.
 */
public class User {
    private String id;
    private String name;
    private boolean isValid = false;


    public String getId(){
        if(id != null){
            return id;
        }else{
            return null;
        }
    }

    public String getId(Application application){
        if(id != null){
            return id;
        }else{
            setId(application);
            return id;
        }
    }

    private void setId(Application application){
        this.id = Settings.Secure.getString(application.getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public boolean getIsValid(){
        return isValid;
    }

    public String getName(){
        if(name != null){
            return name;
        }else{
            return name = "null";
        }
    }

    public void setName(String name){
        if(name != null){
            this.name = name;
        }else{
            this.name = "null";
            System.out.println("Cannot set name to null, name set to equal string 'nul'");
        }
    }

    public User(Application application){
        this.id = Settings.Secure.getString(application.getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        name = "Enter a name";
    }

    public User(String name, Application application){
        this.id = Settings.Secure.getString(application.getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        this.name = name;
    }

    public User(String name, String ip, Application application){
        this.id = Settings.Secure.getString(application.getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        this.name = name;
    }

    public boolean isValid(){
        return isValid(name);
    }

    public boolean isValid(String name){
        // Assume the name is valid until proven otherwise.
        if(name != null && !name.isEmpty()) {

            if(name.equalsIgnoreCase("Enter a name")) {
                return false;
            }else if(name.equalsIgnoreCase("username")) {
                return false;
            }
            // Additional checks can go here


            // We passed the checks and the name is not empty or null, return true
            isValid = true;
            return true;
        }
        return false;
    }

    @Override
    public String toString(){
        return "User:\nid: " + id + "\nName: " + name;
    }
}
