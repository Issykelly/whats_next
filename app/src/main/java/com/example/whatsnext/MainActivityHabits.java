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

import com.example.whatsnext.database.DBHandler;
import com.example.whatsnext.habitsHandling.HabitsAdapter;
import com.example.whatsnext.habitsHandling.HabitsModel;
import com.example.whatsnext.habitsHandling.addHabit;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.List;

public class MainActivityHabits extends AppCompatActivity {

    Date currentlyShowing;
    Date today;
    ChipGroup chipGroup;
    TextView currentDateText;
    RecyclerView recyclerView;
    int previousSelectedChip;
    boolean isUserInteracting = false;
    List<HabitsModel> habitModelList = new ArrayList<>();
    String[][] habits;

    HabitsAdapter HabitsAdapter;

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

        recyclerView = findViewById(R.id.habitsView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

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

        Button addButton = findViewById(R.id.addButton);
        Button currentDate = findViewById(R.id.currentButton);
        // Set an OnClickListener on the button
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the addEvent activity
                Intent intent = new Intent(MainActivityHabits.this, addHabit.class);
                intent.putExtra("HabitsModel", currentlyShowing);
                startActivity(intent);
            }
        });

        currentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUserInteracting = false;
                update(today);
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
                if (isUserInteracting){
                    Chip selectedChip = findViewById(checkedId);
                    int currentIndex = group.indexOfChild(selectedChip);

                    // find difference in days
                    int difference = currentIndex - previousSelectedChip;

                    // Update the calendar based on the difference
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(currentlyShowing);
                    calendar.add(Calendar.DATE, difference); // Add/subtract days

                    // Get the new date after subtracting 2 days
                    Date newDate = calendar.getTime();
                    previousSelectedChip = currentIndex;
                    update(newDate);
                }
                }
            });
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

        if (recyclerView.getAdapter() != null && recyclerView.getAdapter().getItemCount() > 0) {
            // Clear the adapter's data
            HabitsAdapter.clearData();
        }

        DBHandler db = new DBHandler(this);
        habits = db.onLoadHabits(date);

        habitModelList.clear();
        for (String[] habitData : habits) {
            HabitsModel habitModel = new HabitsModel(
                    Integer.valueOf(habitData[3]),
                    habitData[0], // Name
                    habitData[1], // Description
                    Integer.valueOf(habitData[5]), // Target Goal
                    Integer.valueOf(habitData[6]),  // Completed Progress
                    habitData[7], // Units
                    Integer.valueOf(habitData[2]),
                    habitData[4],
                    Integer.valueOf(habitData[10]),
                    habitData[8],
                    habitData[9]
            );
            habitModelList.add(habitModel);
        }

        HabitsAdapter = new HabitsAdapter(habitModelList, this, position -> {
            // this code is creating an onClickListener method for recycler view
            //where you grab which event is being clicked, and send its infomation
            // into add events
            HabitsModel selectedModel = habitModelList.get(position);
        });
        recyclerView.setAdapter(HabitsAdapter);

        //store for later use
        currentlyShowing = date;
        isUserInteracting = true; //helps to avoid recursion unnecessarily
    }
}