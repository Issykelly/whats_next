package com.example.whatsnext.countdownHandling;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsnext.R;

import java.text.ParseException;
import java.util.List;

public class CountdownAdapter extends RecyclerView.Adapter<CountdownAdapter.CountdownViewHolder> {

    private List<CountdownModel> countdownModelList;
    private Context context;
    private OnItemClickListener listener;

    public CountdownAdapter(List<CountdownModel> countdownModelList, OnItemClickListener listener) {
        this.countdownModelList = countdownModelList;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
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

        // Set event name and date and colour
        holder.Background.setBackgroundColor(countdownModel.getEventColour());
        holder.EventName.setText(countdownModel.getEventName());
        holder.EventDate.setText(countdownModel.getEventDate());


        // create a new ImageView within each recylcer view element
        // or countdown.
        ImageView imageView = new ImageView(context);
        imageView.setId(View.generateViewId());
        // get the image name from the countDown model object currently
        // being rendered
        String imageName = countdownModel.getEventImage();
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
        holder.EventIcons.addView(imageView);

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
                    holder.EventCountdown.setText(result[0]);
                    holder.EventCountdownLabel.setText(result[1]);
                }

                @Override
                public void onCountdownFinish() {
                    // When countdown finishes, set the result to finished
                    countdownModel.setCountdown(new String[]{"Event Finished", " "});
                    holder.EventCountdown.setText("Event Finished");
                    holder.EventCountdownLabel.setText("Finished");
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
            holder.EventCountdown.setText("Error");
            holder.EventCountdownLabel.setText("Invalid Date");
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(holder.getAdapterPosition()));
    }


    @Override
    public int getItemCount() {
        return countdownModelList.size();
    }

    // ViewHolder class
    public static class CountdownViewHolder extends RecyclerView.ViewHolder {

        TextView EventName, EventDate, EventCountdown, EventCountdownLabel;
        RelativeLayout Background, EventIcons;

        public CountdownViewHolder(@NonNull View itemView) {
            super(itemView);

            // Bind views from the row layout
            EventName = itemView.findViewById(R.id.eventName);
            Background = itemView.findViewById(R.id.background);
            EventDate = itemView.findViewById(R.id.date);
            EventCountdown = itemView.findViewById(R.id.countdown);
            EventCountdownLabel = itemView.findViewById(R.id.countdownLabel);
            EventIcons = itemView.findViewById(R.id.eventIcon);
        }
    }
}



