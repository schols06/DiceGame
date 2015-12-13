package com.example.dicegameclient;

/**
 * Created by Acer on 07/12/2015.
 */
public class Vector3 {

    public float x;
    public float y;
    public float z;

    public Vector3(){
        x = y = z = 0;
    }

    public Vector3(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float Length() {
        return ((float)(Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2))));
    }

    public float Distance(Vector3 to){
        return ((float)(Math.sqrt(Math.pow(this.x - to.x, 2) + Math.pow(this.y - to.y, 2) + Math.pow(this.z - to.z, 2))));
    }

}
