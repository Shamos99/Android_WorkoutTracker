package com.example.workouttracker;

import java.util.ArrayList;

public class Exercise {

    private String name;
    private String notes = "";
    private int sets;
    private ArrayList<String> reps = new ArrayList<>();
    private ArrayList<String> weight = new ArrayList<>();

    Exercise(String str){
        name = str;
    }

    //Getters
    String getname(){
        return name;
    }

    String getnotes(){ return notes; }

    int getsets(){ return sets; }

    ArrayList<String> getreps(){
        return reps;
    }

    ArrayList<String> getweights(){
        return weight;
    }

    //Setters
    void addnotes(String str){
        notes = str;
    }

    void setsets(int num){
        sets= num;
    }

    void setrepsarr(ArrayList<String> arr){
        reps = arr;
    }

    void setweightr(ArrayList<String> arr){
        weight = arr;
    }



}
