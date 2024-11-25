package com.example.whatsnext;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.util.List;

public class CountdownAdapter extends RecyclerView.Adapter<CountdownAdapter.CountdownViewHolder> {

    private List<CountdownModel> countdownModelList;
    private Context context;

    public CountdownAdapter(List<CountdownModel> countdownModelList) {
        this.countdownModelList = countdownModelList;
    }

    @NonNull
    @Override
    public CountdownViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        // Inflate the card layout for each item
        View view = LayoutInflater.from(context).inflate(R.layout.countdown_card, parent, false);
        return new CountdownViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CountdownViewHolder holder, int position) {
        CountdownModel countdownModel = countdownModelList.get(position);

        // Set event name and date
        holder.tvEventName.setText(countdownModel.getEventName());
        holder.tvEventDate.setText(countdownModel.getEventDate());

        // Set up the countdown timer
        try {
            new CountdownTimer(countdownModel.getEventDate(), new CountdownTimer.CountdownCallback() {
                @Override
                public void onCountdownUpdate(long days, long hours, long minutes, long seconds) {
                    // Create the countdown array with updated values
                    Log.e("123", String.valueOf(days) + " " + String.valueOf(hours) + " "+ String.valueOf(minutes) + " " + String.valueOf(seconds));
                    String[] result = {"0", "seconds"};
                    if (days > 0){
                        result = new String[]{String.valueOf(days + 1), "days"};
                    } else if (hours > 0) {
                        result = new String[]{String.valueOf(hours + 1), "hours"};
                    } else if (minutes > 0) {
                        result = new String[]{String.valueOf(minutes + 1), "minutes"};
                    } else if (seconds > 0) {
                        result = new String[]{String.valueOf(seconds), "seconds"};
                    }

                    // Update the model and the UI
                    countdownModel.setCountdown(result);
                    holder.tvEventCountdown.setText(result[0]);
                    holder.tvEventCountdownLabel.setText(result[1]);
                }

                @Override
                public void onCountdownFinish() {
                    // When countdown finishes, set the result to finished
                    countdownModel.setCountdown(new String[]{"Event Finished", " "});
                    holder.tvEventCountdown.setText("Event Finished");
                    holder.tvEventCountdownLabel.setText("Finished");
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
            holder.tvEventCountdown.setText("Error");
            holder.tvEventCountdownLabel.setText("Invalid Date");
        }
    }


    @Override
    public int getItemCount() {
        return countdownModelList.size();
    }

    // ViewHolder class
    public static class CountdownViewHolder extends RecyclerView.ViewHolder {

        TextView tvEventName, tvEventDate, tvEventCountdown, tvEventCountdownLabel;

        public CountdownViewHolder(@NonNull View itemView) {
            super(itemView);

            // Bind views from the row layout
            tvEventName = itemView.findViewById(R.id.eventName);
            tvEventDate = itemView.findViewById(R.id.date);
            tvEventCountdown = itemView.findViewById(R.id.countdown);
            tvEventCountdownLabel = itemView.findViewById(R.id.countdownLabel);
        }
    }
}



