package com.example.whatsnext.habitsHandling;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsnext.R;
import com.example.whatsnext.countdownHandling.CountdownAdapter;
import com.example.whatsnext.countdownHandling.CountdownModel;

import java.util.List;

public class HabitsAdapter extends RecyclerView.Adapter<HabitsAdapter.HabitsViewHolder> {

    private List<HabitsModel> habitModelList;
    private Context context;
    //private HabitsAdapter.OnItemClickListener listener;

    public HabitsAdapter(List<HabitsModel> habitModelList, Context context) {
        this.habitModelList = habitModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public HabitsAdapter.HabitsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        // Inflate the card layout for each item
        View view = LayoutInflater.from(context).inflate(R.layout.habit_card, parent, false);
        return new HabitsAdapter.HabitsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitsViewHolder holder, int position) {
        HabitsModel habit = habitModelList.get(position);
        holder.Background.setBackgroundColor(habit.getHabitColour());
        holder.name.setText(habit.getHabitName());
        holder.description.setText(habit.getHabitDesc());
        holder.progress.setText(String.format("%s / %s", habit.getHabitProgress(), habit.getHabitGoal()));
        holder.units.setText(habit.getHabitUnit());
    }

    @Override
    public int getItemCount() {
        return habitModelList.size();
    }

    public void clearData() {
        habitModelList.clear(); // Clear the dataset in the adapter
        notifyDataSetChanged(); // Notify the RecyclerView to refresh
    }

    public static class HabitsViewHolder extends RecyclerView.ViewHolder {
        TextView name, description, progress, units;
        RelativeLayout Background, EventIcons;

        public HabitsViewHolder(@NonNull View itemView) {
            super(itemView);
            Background = itemView.findViewById(R.id.background);
            name = itemView.findViewById(R.id.habitName);
            description = itemView.findViewById(R.id.habitDescription);
            progress = itemView.findViewById(R.id.progress);
            units = itemView.findViewById(R.id.units);
        }
    }
}

