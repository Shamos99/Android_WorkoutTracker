package com.example.workouttracker;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {

    String workoutskey = "wrr_key";
    String exercisekey = "exr_key";
    int thisworkoutindex;
    ArrayList<Workout> workouts = new ArrayList<>();
    ArrayList<Exercise> exercises = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        startroutine();

        FloatingActionButton btn = (FloatingActionButton) findViewById(R.id.addtoroutine);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addexerciseroutine();
            }
        });

    }

    private void addexerciseroutine() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.exerciselayout);
        layout.removeAllViews();
        ArrayList<TextView> texts = new ArrayList<>();

        for(int i = 0;i<exercises.size();i++){
            texts.add(new TextView(Main2Activity.this));
            texts.get(i).setTag(i);
            texts.get(i).setTextSize(20);
            texts.get(i).setText("(tap to add) "+exercises.get(i).getname());

            texts.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addthisexercise(v);
                    saveobjects();
                    startroutine();
                }
            });
            layout.addView(texts.get(i));
        }

    }

    private void addthisexercise(View v){
        int index = (int) v.getTag();
        workouts.get(thisworkoutindex).addexercise(exercises.get(index));

    }

    private void displayexercises(){
        LinearLayout layout = (LinearLayout) findViewById(R.id.exerciselayout);
        ArrayList<TextView> texts = new ArrayList<>();
        layout.removeAllViews();

        for(int i = 0;i<exercises.size();i++){
            texts.add(new TextView(Main2Activity.this));
            texts.get(i).setTag(i);
            texts.get(i).setTextSize(20);
            texts.get(i).setText(exercises.get(i).getname());
            layout.addView(texts.get(i));
        }
    }

    private void displayroutine(){
        LinearLayout layout = (LinearLayout) findViewById(R.id.routinelayout);
        ArrayList<TextView> texts = new ArrayList<>();
        layout.removeAllViews();

        Workout thisworkout = workouts.get(thisworkoutindex);
        ArrayList<Exercise> routineshit = new ArrayList<>();
        routineshit = thisworkout.getexercises();

        int numofexercises = thisworkout.getexercisenum();

        if (numofexercises==0){
               //do nothing
        }
        else {
            for (int i = 0; i < thisworkout.getexercisenum(); i++) {
                texts.add(new TextView(Main2Activity.this));

                texts.get(i).setText(routineshit.get(i).getname());
                texts.get(i).setTag(i);
                texts.get(i).setTextSize(25);

                texts.get(i).setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        menuremoveexercise(v);
                        return true;
                    }
                });

                texts.get(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intentexercises(v);
                    }
                });
                layout.addView(texts.get(i));
            }
        }
    }

    private void intentexercises(View v){
        int index = (int) v.getTag();
        Intent intent = new Intent(getApplicationContext(),Main3Activity.class);
        intent.putExtra("indexexercise",index);
        intent.putExtra("indexworkout",thisworkoutindex);
        startActivity(intent);
    }

    private void menuremoveexercise(View v){

        AlertDialog.Builder prompt = new AlertDialog.Builder(Main2Activity.this);

        final int index = (int) v.getTag();

        prompt.setTitle("Remove this exercise from the routine?");

        prompt.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                workouts.get(thisworkoutindex).removeexercise(index);
                saveobjects();
                displayroutine();
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

    private void startroutine(){

        Intent intent = getIntent();
        int index = intent.getIntExtra("index",0);
        retrieveworkouts();
        retrieveexercises();

        String title = workouts.get(index).getname();
        setTitle(title);

        thisworkoutindex = index;
        displayexercises();

        displayroutine();
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

        exercises.clear();

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
    }

}




