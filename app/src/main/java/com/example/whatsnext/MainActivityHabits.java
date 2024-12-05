package com.example.whatsnext;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsnext.countdownHandling.addEvent;
import com.example.whatsnext.habitsHandling.HabitsAdapter;
import com.example.whatsnext.habitsHandling.HabitsModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.List;

public class MainActivityHabits extends AppCompatActivity {

    Date currentlyShowing, today;
    ChipGroup chipGroup;
    TextView currentDateText;
    RecyclerView recyclerView;
    int previousSelectedChip;
    boolean isUserInteracting = false;
    List<HabitsModel> habitModelList = new ArrayList<>();

    HabitsAdapter HabitsAdapter;

    String[][] habits = {
            {"Morning Jog", "Start your day with a refreshing jog to stay fit.", "1", "jogs", "0"},
            {"Read Books", "Expand your knowledge by reading daily.", "20", "pages", "0"},
            {"Drink Water", "Stay hydrated by drinking sufficient water.", "8", "glasses", "0"},
            {"Meditate", "Relax and focus with daily meditation sessions.", "10", "minutes", "0"},
            {"Learn a New Skill", "Spend time improving or learning a new skill.", "30", "minutes", "0"},
            {"Declutter Space", "Tidy up your surroundings for better productivity.", "1", "areas", "0"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_habits);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        chipGroup = findViewById(R.id.daysChips);
        currentDateText = findViewById(R.id.currentDate);
        TextView previousButton = findViewById(R.id.prev);
        TextView nextButton = findViewById(R.id.next);

        today = new Date();
        update(today);

        previousButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(currentlyShowing);
                calendar.add(Calendar.DAY_OF_MONTH, - 7);

                Date newDate = calendar.getTime();
                update(newDate);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(currentlyShowing);
                calendar.add(Calendar.DAY_OF_MONTH, + 7);

                Date newDate = calendar.getTime();
                update(newDate);
            }
        });

        Button eventsButton = findViewById(R.id.eventsButton);
        eventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the addEvent activity
                Intent intent = new Intent(MainActivityHabits.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }});

        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged (ChipGroup group,int checkedId){
                // Get the index of the currently selected chip
                Chip selectedChip = findViewById(checkedId);
                int currentIndex = group.indexOfChild(selectedChip);

                // find difference in days
                int difference = previousSelectedChip - currentIndex;

                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_WEEK, - difference);

                // Get the new date after subtracting 2 days
                Date newDate = calendar.getTime();
                update(newDate);
                }
            });

        recyclerView = findViewById(R.id.habitsView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        for (String[] habitData : habits) {
            HabitsModel habitModel = new HabitsModel(
                    0,
                    habitData[0], // Name
                    habitData[1], // Description
                    habitData[2], // Target Goal
                    habitData[4],  // Completed Progress
                    habitData[3], // Units
                    -6190938,
                    "null"
            );
            habitModelList.add(habitModel);
        }

        HabitsAdapter = new HabitsAdapter(habitModelList, this);
        recyclerView.setAdapter(HabitsAdapter);
    }

    public void update(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = formatter.format(date);
        currentDateText.setText(String.valueOf(formattedDate));

        // if the user isn't and instead this is initialisation
        // pick the correct chip to be selected based off the day
        // of the week
        if (!isUserInteracting){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == Calendar.SUNDAY){
                dayOfWeek = 6;
            } else {
                dayOfWeek -= 2;
            }
            previousSelectedChip = dayOfWeek;

            // set correct chip selected
            Chip chipToSelect = (Chip) chipGroup.getChildAt(dayOfWeek);
            chipGroup.check(chipToSelect.getId());
        }

        //store for later use
        currentlyShowing = date;
        isUserInteracting = true; //helps to avoid recursion unnecessarily
    }
}