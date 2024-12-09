package com.example.whatsnext.habitsHandling;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsnext.MainActivity;
import com.example.whatsnext.MainActivityHabits;
import com.example.whatsnext.R;
import com.example.whatsnext.countdownHandling.CountdownAdapter;
import com.example.whatsnext.countdownHandling.CountdownModel;
import com.example.whatsnext.countdownHandling.addEvent;
import com.example.whatsnext.database.DBHandler;

import java.util.List;

public class HabitsAdapter extends RecyclerView.Adapter<HabitsAdapter.HabitsViewHolder> {

    private List<HabitsModel> habitModelList;
    private OnItemClickListener listener;
    private int selectedPosition = -1;
    DBHandler db;
    private Context context;
    //private HabitsAdapter.OnItemClickListener listener;

    public HabitsAdapter(List<HabitsModel> habitModelList, Context context, OnItemClickListener listener) {
        this.habitModelList = habitModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public HabitsAdapter.HabitsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        db = new DBHandler(context);
        // Inflate the card layout for each item
        View view = LayoutInflater.from(context).inflate(R.layout.habit_card, parent, false);
        return new HabitsAdapter.HabitsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitsViewHolder holder, int position) {
        position = holder.getAdapterPosition();

        HabitsModel habit = habitModelList.get(position);
        holder.Background.setBackgroundColor(habit.getHabitColour());
        holder.name.setText(habit.getHabitName());
        holder.description.setText(habit.getHabitDesc());
        holder.progress.setText(String.format("%s / %s", habit.getHabitProgress(), habit.getHabitGoal()));
        holder.units.setText(habit.getHabitUnit());

        ImageView imageView = new ImageView(context);
        imageView.setId(View.generateViewId());
        // get the image name from the countDown model object currently
        // being rendered
        String imageName = habit.getHabitImage();
        // add to the image to the imageview
        int resourceId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
        imageView.setImageResource(resourceId);

        // add xml layout
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);

        // add to recycler view!
        imageView.setLayoutParams(params);
        holder.habitIcons.addView(imageView);

        if (position == selectedPosition) {
            holder.buttonsView.setVisibility(View.VISIBLE);  // Show buttons for selected item
        } else {
            holder.buttonsView.setVisibility(View.GONE);  // Hide buttons for others
        }

        int finalPosition = position;
        holder.itemView.setOnClickListener(v -> {
            // Handle item click and update the selected position
            if (selectedPosition == finalPosition) {
                selectedPosition = -1;  // Deselect if the same item is clicked
            } else {
                selectedPosition = finalPosition;  // Select the clicked item
            }
            notifyDataSetChanged();  // Notify adapter to refresh the UI
        });

        holder.plus.setOnClickListener(v -> {
            if (habit.getHabitProgress() < habit.getHabitGoal()){
                db.addHabitProgress(habit.getHabitProgress(), habit.getHabitGoal(), String.valueOf(habit.getHabitTrackingNo()));
                habit.setHabitProgress(habit.getHabitProgress() + 1);
                notifyDataSetChanged();
            }
        });

        holder.minus.setOnClickListener(v -> {
            if (habit.getHabitProgress() > 0){
                db.minusHabitProgress(habit.getHabitProgress(), String.valueOf(habit.getHabitTrackingNo()));
                habit.setHabitProgress(habit.getHabitProgress() - 1);
                notifyDataSetChanged();
            }
        });

        holder.edit.setOnClickListener(v -> {
            Intent intent = new Intent(context, addHabit.class);
            intent.putExtra("HabitsModel", habit);
            context.startActivity(intent);
                });

    }

    @Override
    public int getItemCount() {
        return habitModelList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void clearData() {
        habitModelList.clear(); // Clear the dataset in the adapter
        notifyDataSetChanged(); // Notify the RecyclerView to refresh
    }

    public static class HabitsViewHolder extends RecyclerView.ViewHolder {
        TextView name, description, progress, units;
        RelativeLayout Background, habitIcons;
        LinearLayout buttonsView;
        Button edit, plus, minus;

        public HabitsViewHolder(@NonNull View itemView) {
            super(itemView);
            Background = itemView.findViewById(R.id.background);
            habitIcons = itemView.findViewById(R.id.habitIcon);
            name = itemView.findViewById(R.id.habitName);
            description = itemView.findViewById(R.id.habitDescription);
            progress = itemView.findViewById(R.id.progress);
            units = itemView.findViewById(R.id.units);
            buttonsView = itemView.findViewById(R.id.buttonsView);
            edit = itemView.findViewById(R.id.edit);
            plus = itemView.findViewById(R.id.plus);
            minus = itemView.findViewById(R.id.minus);
        }
    }
}

