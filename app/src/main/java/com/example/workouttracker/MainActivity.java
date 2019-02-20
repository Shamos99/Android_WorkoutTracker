package com.example.workouttracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;

import com.google.gson.*;

public class MainActivity extends AppCompatActivity {

    String workoutskey = "wrr_key";
    String exercisekey = "exr_key";
    ArrayList<Workout> workouts = new ArrayList<>();
    ArrayList<Exercise> exercises = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Load currently existing workouts/exercises
        mainroutine();

        //Listeners
        FloatingActionButton addworkouts = (FloatingActionButton) findViewById(R.id.addworkouts);
        addworkouts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Menuaddworkout();
            }
        });

        FloatingActionButton addexercises = (FloatingActionButton) findViewById(R.id.addexercises);
        addexercises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Menuaddexercise();
            }
        });

    }

    //Routine Functions

    private void mainroutine() {


        if (retrieveworkouts()) {
            displayworkouts();
        }

        if (retrieveexercises()) {
            displayexercises();
        }
    }

    private boolean retrieveworkouts() {
        //Retrieve workouts from shared preferances and
        //store them into the "workouts" array

        SharedPreferences mypref = PreferenceManager.getDefaultSharedPreferences(this);

        Gson gson = new Gson();
        String retr = mypref.getString(workoutskey, "");
        if (!(retr.equals(""))) {
            String[] split = retr.split("@");

            for (int i = 0; i < split.length; i++) {
                workouts.add(gson.fromJson(split[i], Workout.class));

            }
            return true;
        }
        return false;
    }

    private boolean retrieveexercises() {
        //Retrieve workouts from shared preferances and
        //store them into the "workouts" array

        SharedPreferences mypref = PreferenceManager.getDefaultSharedPreferences(this);

        Gson gson = new Gson();
        String retr = mypref.getString(exercisekey, "");
        if (!(retr.equals(""))) {
            String[] split = retr.split("@");

            for (int i = 0; i < split.length; i++) {
                exercises.add(gson.fromJson(split[i], Exercise.class));

            }
            return true;
        }
        return false;
    }

    //Display Data

    private void displayworkouts() {

        final LinearLayout test = (LinearLayout) findViewById(R.id.testlayout);
        test.removeAllViews();
        ArrayList<TextView> txt = new ArrayList<>();

        for (int x = 0; x < workouts.size(); x++) {

            txt.add(new TextView(MainActivity.this));
            txt.get(x).setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            txt.get(x).setText(workouts.get(x).getname());
            txt.get(x).setTextSize(25);
            txt.get(x).setTag(x);

            txt.get(x).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Menudeletworkout(v);
                    return true;

                }
            });
            
            txt.get(x).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 intentworkout(v);
                }
            });

            test.addView(txt.get(x));
        }
    }

    private void intentworkout(View v){
        int index = (int) v.getTag();
        Intent intent = new Intent(getApplicationContext(),Main2Activity.class);
        intent.putExtra("index",index);
        startActivity(intent);
    }
   

    private void displayexercises() {

        final LinearLayout test = (LinearLayout) findViewById(R.id.exerciselayout);
        test.removeAllViews();
        ArrayList<TextView> txt = new ArrayList<>();

        for (int x = 0; x < exercises.size(); x++) {

            txt.add(new TextView(MainActivity.this));
            txt.get(x).setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            txt.get(x).setText(exercises.get(x).getname());
            txt.get(x).setTextSize(20);
            txt.get(x).setTag(x);

            txt.get(x).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Menudelete_exercise(v);
                    return true;

                }
            });

            test.addView(txt.get(x));
        }
    }

    //Delete workout functions

    private void Menudeletworkout(View v) {

        AlertDialog.Builder prompt = new AlertDialog.Builder(MainActivity.this);

        final int index = (int) v.getTag();

        prompt.setTitle("Are you sure you want to delete this workout?");

        prompt.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteworkout(index);
                displayworkouts();
            }
        });

        prompt.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //to do
            }
        });

        AlertDialog finalprompt = prompt.create();
        finalprompt.show();
    }

    private void deleteworkout(int index) {

        ArrayList<String> contents = new ArrayList<>();
        workouts.remove(index);
        saveobjects();
    }

    //Add workout functions

    private void Menuaddworkout() {

        AlertDialog.Builder prompt = new AlertDialog.Builder(MainActivity.this);
        final EditText input = new EditText(MainActivity.this);

        prompt.setTitle("Enter the name of your Workout");
        prompt.setView(input);

        prompt.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addworkout(input.getText().toString());
                displayworkouts();
            }
        });

        prompt.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //to do
            }
        });

        AlertDialog finalprompt = prompt.create();
        finalprompt.show();
    }

    private void addworkout(String name) {

        Workout newworkout = new Workout(name);
        workouts.add(newworkout);
        saveobjects();
    }

    //Add Exercises

    private void Menuaddexercise() {

        AlertDialog.Builder prompt = new AlertDialog.Builder(MainActivity.this);
        final EditText input = new EditText(MainActivity.this);

        prompt.setTitle("Enter the name of your Exercise");
        prompt.setView(input);

        prompt.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addexercise(input.getText().toString());
                displayexercises();
            }
        });

        prompt.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //to do
            }
        });

        AlertDialog finalprompt = prompt.create();
        finalprompt.show();
    }

    private void addexercise(String name) {

        Exercise exr = new Exercise(name);
        exercises.add(exr);
        saveobjects();
    }

    //Delete exercises

    private void Menudelete_exercise(View v) {

        AlertDialog.Builder prompt = new AlertDialog.Builder(MainActivity.this);

        final int index = (int) v.getTag();

        prompt.setTitle("Are you sure you want to delete this exercise?");

        prompt.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                delete_exercise(index);
                displayexercises();
            }
        });

        prompt.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //to do
            }
        });

        AlertDialog finalprompt = prompt.create();
        finalprompt.show();
    }

    private void delete_exercise(int index) {

        ArrayList<String> contents = new ArrayList<>();
        exercises.remove(index);
        saveobjects();
    }


    //Functions related to saving and retrieving data from storage

    private void saveobjects() {

        Gson gson = new Gson();
        SharedPreferences mypref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor myprefedit = mypref.edit();
        StringBuilder json = new StringBuilder();

        for (int i = 0; i < workouts.size(); i++) {
            json.append(gson.toJson(workouts.get(i)));
            if (i < (workouts.size() - 1)) {
                json.append("@");
            }
        }

        myprefedit.putString(workoutskey, json.toString());
        myprefedit.apply();

        json.setLength(0);

        for (int i = 0; i < exercises.size(); i++) {
            json.append(gson.toJson(exercises.get(i)));
            if (i < (exercises.size() - 1)) {
                json.append("@");
            }
        }

        myprefedit.putString(exercisekey, json.toString());
        myprefedit.apply();
    }
}