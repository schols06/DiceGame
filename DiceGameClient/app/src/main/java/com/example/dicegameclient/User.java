package com.example.dicegameclient;

import android.app.Application;
import android.provider.Settings;

/**
 * User. Class that represents the current User.
 */
public class User {
    // User related variables such as the id, name and the last generated score.
    private String id;
    private String name;
    public Score lastScore;
    private boolean isValid = false;

    /**
     * getId. Getter for the user' id.
     * @return The id (Android_id) of the user.
     */
    public String getId(){
        if(id != null){
            return id;
        }else{
            return null;
        }
    }

    /**
     * getId. Overridden Getter for the user' id.
     * @param application The application, used to get the users android id.
     * @return The id (Android_id) of the user.
     */
    public String getId(Application application){
        if(id != null){
            return id;
        }else{
            setId(application);
            return id;
        }
    }

    /**
     * setId. Used to set a users android id to the one of the device.
     * @param application The application, used to get the users android id.
     */
    private void setId(Application application){
        this.id = Settings.Secure.getString(application.getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    /**
     * getName. Getter for the user' name.
     * @return
     */
    public String getName(){
        if(name != null){
            return name;
        }else{
            return name = "null";
        }
    }

    /**
     * setName. Setter for the user' name
     * @param name The new name.
     */
    public void setName(String name){
        if(name != null){
            this.name = name;
        }else{
            this.name = "null";
            System.out.println("Cannot set name to null, name set to equal string 'nul'");
        }
    }

    /**
     * User. Constructor.
     * @param application The application, used to get the user' android id.
     */
    public User(Application application){
        this.id = Settings.Secure.getString(application.getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        name = "Enter a name";
        lastScore = new Score();
    }

    /**
     * isValid. Checks if the user is valid (proper name etc), and returns true if it is.
     * @return boolean true if the user is valid, false if otherwise.
     */
    public boolean isValid(){
        return isValid(name);
    }

    /**
     * isValidChecks if the user is valid (proper name etc), and returns true if it is.
     * @param name The name to check.
     * @return boolean true if the user is valid, false if otherwise.
     */
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
