package com.example.whatsnext.habitsHandling;

import android.app.Dialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.whatsnext.MainActivity;
import com.example.whatsnext.MainActivityHabits;
import com.example.whatsnext.R;
import com.example.whatsnext.countdownHandling.CountdownModel;
import com.example.whatsnext.countdownHandling.addEvent;
import com.example.whatsnext.database.DBHandler;

import java.text.ParseException;
import java.util.Date;

import yuku.ambilwarna.AmbilWarnaDialog;

public class addHabit extends AppCompatActivity {

    Button Quit, Add, Delete, PickColorButton;
    private TextView Error;
    private EditText habitName, habitDate, habitDesc, habitGoal, habitUnit;
    private int DefaultColor;
    private HabitsModel HabitsModel;
    Date date;
    DBHandler db = new DBHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_habit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        PickColorButton = findViewById(R.id.pickColourButton);
        Quit = findViewById(R.id.quitButton);
        Add = findViewById(R.id.addButton);
        Delete = findViewById(R.id.deleteButton);
        habitName = findViewById(R.id.edithabitName);
        habitDesc = findViewById(R.id.editHabitsDescription);
        habitGoal = findViewById(R.id.editGoalText);
        habitUnit = findViewById(R.id.editUnitText);
        Error = findViewById(R.id.errorMessage);
        habitDate = findViewById(R.id.edithabitDate);
        DefaultColor = -6190938;
        Spinner icon = findViewById(R.id.icon_spinner);
        Spinner freq = findViewById(R.id.frequency_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.icon_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        icon.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                this,
                R.array.freq_array,
                android.R.layout.simple_spinner_item
        );
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        freq.setAdapter(adapter2);

        Intent intent = getIntent();
        if (intent.hasExtra("HabitsModel")) {
            Object extra = intent.getSerializableExtra("HabitsModel");
            if (extra instanceof HabitsModel) {
                HabitsModel = (HabitsModel) extra; // Properly assign HabitsModel here
            } else {
                date = (Date) extra;  // If it's not HabitsModel, it must be Date
            }
        }

        if (HabitsModel != null){
            habitName.setText(HabitsModel.getHabitName());
            habitDate.setText(HabitsModel.getHabitDate());
            habitGoal.setText(String.valueOf(HabitsModel.getHabitGoal()));
            habitDesc.setText(HabitsModel.getHabitDesc());
            habitUnit.setText(HabitsModel.getHabitUnit());
            DefaultColor = HabitsModel.getHabitColour();
            PickColorButton.setBackgroundColor(DefaultColor);
            String image = HabitsModel.getHabitImage();
            String freqency = HabitsModel.getHabitFreq();
            int position = adapter.getPosition(image);
            icon.setSelection(position);
            int position2 = adapter2.getPosition(freqency);
            freq.setSelection(position2);
        } else {
            habitName.setText("");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = sdf.format(date);
            habitDate.setText(formattedDate);
            habitGoal.setText("");
            habitDesc.setText("");
            habitUnit.setText("");
            icon.setSelection(0);
            freq.setSelection(0);
        }

        PickColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPickerDialogue();
            }
        });

        Quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HabitsModel = null;
                Intent intent = new Intent(addHabit.this, MainActivityHabits.class);
                startActivity(intent);
            }
        });

        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                openDeleteDialogue();
            }
        });

        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // to allow us to check the date is in the right format
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false);

                //get the name & date from the user
                String name = String.valueOf(habitName.getText());
                String desc = String.valueOf(habitDesc.getText());
                String date = String.valueOf(habitDate.getText());
                String goal = String.valueOf(habitGoal.getText());
                String unit = String.valueOf(habitUnit.getText());
                String selectedFreqOption = freq.getSelectedItem().toString();
                String selectedIconOption = icon.getSelectedItem().toString();

                // input validation!
                if (name.isEmpty()) {
                    // if the name is empty, show the error message
                    // and update it as neccessary
                    Error.setVisibility(View.VISIBLE);
                    Error.setText("please enter a name");
                } else if (name.length() > 20) {
                    // if the name too long, show the error message
                    // and update it as neccessary
                    Error.setVisibility(View.VISIBLE);
                    Error.setText("please enter a name shorter than 20 characters");
                } else if (desc.isEmpty()) {
                    Error.setVisibility(View.VISIBLE);
                    Error.setText("please enter a description");
                } else if (name.length() > 35) {
                    Error.setVisibility(View.VISIBLE);
                    Error.setText("please enter a description shorter than 35 characters");
                } else if (unit.isEmpty()) {
                    Error.setVisibility(View.VISIBLE);
                    Error.setText("please enter a unit");
                } else if (unit.length() >= 10) {
                    Error.setVisibility(View.VISIBLE);
                    Error.setText("please enter a unit shorter than 10 characters");
                } else {
                    try {
                        // try parsing date, if no error
                        // than update the database!

                        int goalInteger = Integer.parseInt(goal);
                        Date habitDate = sdf.parse(date);

                        if (HabitsModel != null){
                            db.editHabit(HabitsModel.getHabitNumber(), name, desc, date, String.valueOf(DefaultColor), selectedIconOption, Integer.valueOf(goal), unit, selectedFreqOption);
                        } else {
                            db.addNewHabit(name, desc, date, String.valueOf(DefaultColor), selectedIconOption, Integer.valueOf(goal), unit, selectedFreqOption);
                        }

                        HabitsModel = null;
                        Intent intent = new Intent(addHabit.this, MainActivityHabits.class);
                        startActivity(intent);
                    } catch (ParseException e) {
                        // if the Date is wrong, show the error message
                        // and update it as neccessary
                        Error.setVisibility(View.VISIBLE);
                        Error.setText("Invalid date format. Please use YYYY-MM-DD");
                    } catch (NumberFormatException e){
                        Error.setVisibility(View.VISIBLE);
                        Error.setText("Invalid goal format. Please make this a number");
                    }
                }
            }
        });
    }
    public void openColorPickerDialogue() {
        // the AmbilWarnaDialog callback needs 3 parameters
        // one is the context, second is default color,
        final AmbilWarnaDialog colorPickerDialogue = new AmbilWarnaDialog(this, DefaultColor,
                new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        // close the pick colour interface
                    }

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        DefaultColor = color;
                        PickColorButton.setBackgroundColor(DefaultColor);
                    }
                });
        colorPickerDialogue.show();
    }

    public void openDeleteDialogue(){
        if (HabitsModel != null){
            Dialog dialog = new Dialog(this);

            dialog.setContentView(R.layout.delete_dialog);
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_window);
            dialog.show();

            Button instance = dialog.findViewById(R.id.thisInstance);
            Button every = dialog.findViewById(R.id.every);

            instance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.deletehabitInstance(HabitsModel.getHabitTrackingNo());
                    Intent intent = new Intent(addHabit.this, MainActivityHabits.class);
                    startActivity(intent);
                }
            });

            every.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.deleteWholeHabit(HabitsModel.getHabitNumber());
                    Intent intent = new Intent(addHabit.this, MainActivityHabits.class);
                    startActivity(intent);
                }
            });
        } else {
            Intent intent = new Intent(addHabit.this, MainActivityHabits.class);
            startActivity(intent);
        }
    }
}