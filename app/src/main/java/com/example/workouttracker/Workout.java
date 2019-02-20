package com.example.workouttracker;

import java.util.ArrayList;

public class Workout {

    private String name;
    private ArrayList<Exercise> exercises = new ArrayList<Exercise>();

    //Constructor
    Workout(String str){
        name = str;
    }

    //Add Exercises
    void addexercise(Exercise exr){
        this.exercises.add(exr);
    }

    void removeexercise(int index){
        this.exercises.remove(index);
    }

    //Getters
    String getname(){
        return name;
    }

    ArrayList<Exercise> getexercises(){
        return this.exercises;
    }

    int getexercisenum(){
        if (this.exercises.isEmpty()){
            return 0;
        }
        else{
            return exercises.size();
        }
    }
}

