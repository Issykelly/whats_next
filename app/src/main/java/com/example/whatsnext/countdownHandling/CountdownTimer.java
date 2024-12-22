package com.example.whatsnext.countdownHandling;

import android.icu.text.SimpleDateFormat;
import android.os.CountDownTimer;
import android.util.Log;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CountdownTimer {

    public CountdownTimer(String eventDate, CountdownCallback callback) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);

        try {
            Date event = sdf.parse(eventDate);
            // getTime calculates the amount of miliseconds since
            // janurary 1st, 1970. CurrentTimeMillis calculates
            // the amount of time between now and janurary 1st,
            long eventTimeInMillis = event.getTime();
            long currentTimeInMillis = System.currentTimeMillis();

            // Determine the countdown mode
            long timeRemaining = eventTimeInMillis - currentTimeInMillis;
            long timeElapsed = currentTimeInMillis - eventTimeInMillis;

            // If the event is in the past, calculate the time elapsed
            if (timeRemaining <= 0) {
                // Calculate days, hours, minutes, and seconds since the event
                long days = TimeUnit.MILLISECONDS.toDays(timeElapsed);
                long hours = TimeUnit.MILLISECONDS.toHours(timeElapsed) - TimeUnit.DAYS.toHours(days);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(timeElapsed) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeElapsed));
                long seconds = TimeUnit.MILLISECONDS.toSeconds(timeElapsed) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeElapsed));
                
                callback.onCountdownUpdate(days, hours, minutes, seconds); // Show elapsed time
                return;
            }

            // Set the correct timer duration

            // Set up a unified CountDownTimer
            new CountDownTimer(timeRemaining, 1000) {
                @Override
                public void onTick(long millisUntilTick) {
                    // if time remaining is bigger than 0, referenceTime = millisUntilTick
                    // this is the time remianing until the event.
                    // if not, the event is in the past, and use millisUntilTick + timeElapsed

                    // Calculate days, hours, minutes, and seconds
                    long days = TimeUnit.MILLISECONDS.toDays(millisUntilTick);
                    long hours = TimeUnit.MILLISECONDS.toHours(millisUntilTick) - TimeUnit.DAYS.toHours(days);
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilTick) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilTick));
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilTick) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilTick));

                    // Return result via callback
                    //Log.d("countdown", String.valueOf(days));
                    callback.onCountdownUpdate(days, hours, minutes, seconds);
                }

                @Override
                public void onFinish() {
                    callback.onCountdownFinish(); // Notify when finished
                }
            }.start();

        } catch (Exception e) {
            Log.e("countdownTimer", "error in countdown Timer");
        }
    }

    public interface CountdownCallback {
        void onCountdownUpdate(long days, long hours, long minutes, long seconds);
        void onCountdownFinish(); // Called when the countdown finishes
    }
}


