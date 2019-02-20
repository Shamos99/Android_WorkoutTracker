package com.example.workouttracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.constraint.Constraints;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.ListIterator;

public class Main3Activity extends AppCompatActivity {

    String workoutskey = "wrr_key";
    String exercisekey = "exr_key";
    int thisworkoutindex;
    int thisexerciseindex;
    int width;
    int unsavedsetnum;
    ArrayList<Workout> workouts = new ArrayList<>();
    ArrayList<Exercise> routineexerciss = new ArrayList<>();
    ArrayList<Exercise> exercises = new ArrayList<>();

    ArrayList<EditText[]> sets_reference = new ArrayList<>();
    // 0 is weight
    // 1 is reps
    EditText notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        startroutine();

        FloatingActionButton addbtn = (FloatingActionButton) findViewById(R.id.addbutton);
        FloatingActionButton delbtn = (FloatingActionButton) findViewById(R.id.deletebutton);
        final Button savebtn = (Button) findViewById(R.id.savebtn);
        final Button refreshbtn = (Button) findViewById(R.id.refreshbtn);

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unsavedsetnum++;
                drawinfo();
            }
        });

        delbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(unsavedsetnum>0){
                    unsavedsetnum--;
                }
                drawinfo();
            }
        });

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savestate();
                saveobjects();
                drawinfo();
                Toast.makeText(getApplicationContext(),"Save successful",Toast.LENGTH_SHORT).show();
            }
        });

        refreshbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unsavedsetnum = routineexerciss.get(thisexerciseindex).getsets();
                drawinfo();
            }
        });

    }


    private void savestate(){

        //save notes
        Exercise thisex = routineexerciss.get(thisexerciseindex);
        thisex.addnotes(notes.getText().toString());

        //save weight/reps info

        ArrayList<String> rep_arr = new ArrayList<>();
        ArrayList<String> weight_arr = new ArrayList<>();

        for(int i=0;i<sets_reference.size();i++){
            rep_arr.add(sets_reference.get(i)[1].getText().toString());
            weight_arr.add(sets_reference.get(i)[0].getText().toString());
        }

        thisex.setrepsarr(rep_arr);
        thisex.setweightr(weight_arr);
        thisex.setsets(unsavedsetnum);

        String name = thisex.getname();

        //make sure both exercises arrays are the same
        for(int i=0;i<exercises.size();i++){
            if(name.equals(exercises.get(i).getname())){
                exercises.set(i,thisex);
            }
        }

    }

    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        width = findViewById(R.id.verticallayout).getWidth();
        drawinfo();
    }

    private void drawinfo(){

        LinearLayout mainlayout = (LinearLayout) findViewById(R.id.verticallayout);

        mainlayout.removeAllViews();

        sets_reference.clear();

        ArrayList<String> rep_arr = new ArrayList<>();
        ArrayList<String> weight_arr = new ArrayList<>();

        rep_arr = routineexerciss.get(thisexerciseindex).getreps();
        weight_arr = routineexerciss.get(thisexerciseindex).getweights();

        for(int i=0;i<unsavedsetnum;i++){
            LinearLayout thisrow = new LinearLayout(Main3Activity.this);

            TextView sets = new TextView(Main3Activity.this);
            EditText weight = new EditText(Main3Activity.this);
            EditText reps = new EditText(Main3Activity.this);

            EditText[] ref = {weight,reps};
            sets_reference.add(ref);
            sets.setText(String.valueOf(i+1));
            sets.setTextSize(15);
            sets.setGravity(Gravity.CENTER_HORIZONTAL);
            sets.setLayoutParams(new LinearLayout.LayoutParams(width/3,LinearLayout.LayoutParams.WRAP_CONTENT));

            weight.setGravity(Gravity.CENTER_HORIZONTAL);
            reps.setGravity(Gravity.CENTER_HORIZONTAL);

            if ((rep_arr.size()>i)) {
                weight.setText(weight_arr.get(i));
                reps.setText(rep_arr.get(i));
            }

            weight.setLayoutParams(new LinearLayout.LayoutParams(width/3,LinearLayout.LayoutParams.WRAP_CONTENT));
            reps.setLayoutParams(new LinearLayout.LayoutParams(width/3,LinearLayout.LayoutParams.WRAP_CONTENT));

            thisrow.setOrientation(LinearLayout.HORIZONTAL);

            thisrow.addView(sets);
            thisrow.addView(weight);
            thisrow.addView(reps);

            mainlayout.addView(thisrow);

        }

        if(routineexerciss.get(thisexerciseindex).getnotes()==null || routineexerciss.get(thisexerciseindex).getnotes().equals("")){
            notes.setHint("Type any notes in here....");
        }
        else{
            notes.setText(routineexerciss.get(thisexerciseindex).getnotes());
        }




    }

    private void startroutine(){

        Intent intent = getIntent();
        int index = intent.getIntExtra("indexexercise",0);
        thisexerciseindex = index;
        index = intent.getIntExtra("indexworkout",0);
        thisworkoutindex = index;

        retrieveworkouts();
        retrieveexercises();

        routineexerciss = workouts.get(thisworkoutindex).getexercises();

        String title = routineexerciss.get(thisexerciseindex).getname();
        setTitle(title);

        notes = (EditText) findViewById(R.id.notes);

        unsavedsetnum = routineexerciss.get(thisexerciseindex).getsets();

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
