package com.example.whatsnext.countdownHandling;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.whatsnext.database.DBHandler;
import com.example.whatsnext.MainActivity;
import com.example.whatsnext.R;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import yuku.ambilwarna.AmbilWarnaDialog;

public class addEvent extends AppCompatActivity {
    private Button PickColorButton;
    private CountdownModel countdownModel;
    private TextView Error;
    private EditText EventName, EventDate;
    private int DefaultColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_event);
        createNotificationChannel();

        DBHandler db = new DBHandler(this);

        PickColorButton = findViewById(R.id.pickColourButton);
        Button quit = findViewById(R.id.quitButton);
        Button add = findViewById(R.id.addButton);
        Button delete = findViewById(R.id.deleteButton);
        EventName = findViewById(R.id.editEventName);
        Error = findViewById(R.id.errorMessage);
        EventDate = findViewById(R.id.editEventDate);
        DefaultColor = -6190938;
        Spinner spinner = findViewById(R.id.icon_spinner);
        Spinner notifs = findViewById(R.id.notifications_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.icon_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                this,
                R.array.notification_array,
                android.R.layout.simple_spinner_item
        );
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        notifs.setAdapter(adapter2);

        Intent intent = getIntent();
        countdownModel = (CountdownModel) intent.getSerializableExtra("countdownModel");
        if (countdownModel != null){
            EventName.setText(countdownModel.getEventName());
            EventDate.setText(countdownModel.getEventDate());
            DefaultColor = countdownModel.getEventColour();
            PickColorButton.setBackgroundColor(DefaultColor);
            String image = countdownModel.getEventImage();
            int position = adapter.getPosition(image);
            spinner.setSelection(position);
        } else {
            EventName.setText("");
            EventDate.setText("");
            spinner.setSelection(0);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        PickColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPickerDialogue();
            }
        });

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countdownModel = null;
                Intent intent = new Intent(addEvent.this, MainActivity.class);
                startActivity(intent);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countdownModel != null){
                    db.deleteEvent(countdownModel.getEventNumber());
                }
                countdownModel = null;
                Intent intent = new Intent(addEvent.this, MainActivity.class);
                startActivity(intent);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // to allow us to check the date is in the right format
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
                sdf.setLenient(false);

                //get the name & date from the user
                String name = String.valueOf(EventName.getText());
                String date = String.valueOf(EventDate.getText());
                String selectedOption = spinner.getSelectedItem().toString();
                String notifications = notifs.getSelectedItem().toString();

                // input validation!
                if (name.isEmpty()) {
                    // if the name is empty, show the error message
                    // and update it as necessary
                    Error.setVisibility(View.VISIBLE);
                    Error.setText(getString(R.string.nameError));
                } else if (name.length() > 20) {
                    // if the name too long, show the error message
                    // and update it as necessary
                    Error.setVisibility(View.VISIBLE);
                    Error.setText(getString(R.string.nameLengthLongError));
                } else {
                    try {
                        // try parsing date, if no error
                        // than update the database!
                        Date eventDate = sdf.parse(date);
                        Date currentDate = new Date();
                        int future = 1;

                        if (eventDate.before(currentDate)) {
                            future = 0;
                        }
                        if (countdownModel != null){
                            db.editEvent(countdownModel.getEventNumber(), name, date, String.valueOf(DefaultColor), future, selectedOption);
                            //placed here so you don't accidentally create lots of notifications when
                            // editing an event - if i had more time would have made a database
                            // to store notifications to be deleted / edited
                            if (!notifications.equals("none")){
                                if (ContextCompat.checkSelfPermission(addEvent.this, Manifest.permission.POST_NOTIFICATIONS)
                                        == PackageManager.PERMISSION_GRANTED) {
                                    addNotification(eventDate, name, notifications);
                                }
                            }
                        } else {
                            db.addNewEvent(name, date, String.valueOf(DefaultColor), future, selectedOption);
                        }

                        countdownModel = null;
                        Intent intent = new Intent(addEvent.this, MainActivity.class);
                        startActivity(intent);
                    } catch (ParseException e) {
                        // if the Date is wrong, show the error message
                        // and update it as necessary
                        Error.setVisibility(View.VISIBLE);
                        Error.setText(getString(R.string.invalidDateFormat));
                    }
                }
            }
        });
    }

    public void openColorPickerDialogue() {
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

    public void addNotification(Date eventdate, String eventName, String notificationTime){
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("eventName", eventName);
        int uniqueID = (int) System.currentTimeMillis();

        long alarmTime = eventdate.getTime();

        switch (notificationTime) {
            case "day":
                alarmTime += 24 * 60 * 60 * 1000;
                break;
            case "week":
                alarmTime += 7 * 24 * 60 * 60 * 1000;
                break;
            case "30 days":
                alarmTime += 30L * 24 * 60 * 60 * 1000;
                break;
        }

        PendingIntent sender = PendingIntent.getBroadcast(this, uniqueID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        am.setExact(AlarmManager.RTC_WAKEUP, alarmTime, sender);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // only available with API 27 or above
            CharSequence name = getString(R.string.eventChannelname);
            String description = getString(R.string.eventChannelDescription);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static class AlarmReceiver extends BroadcastReceiver {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            DBHandler db = new DBHandler(context);
            String[] names = db.fetchEventNames();
            Bundle extras = intent.getExtras();
            int uniqueID = (int) System.currentTimeMillis();

            if (extras != null) {
                String extraValue = extras.getString("eventName");
                for (String item : names){
                    if (item.equals(extraValue)){
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "1")
                                .setSmallIcon(R.drawable.mirrorball)
                                .setContentTitle(extraValue)
                                .setContentText(extraValue + " is soon, open the app to find out!")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                        // suppressed error asking me to check if notifications are enabled, as ive
                        // already checked prior to this code being run
                        notificationManager.notify(uniqueID, builder.build());
                    }}}}
        }
    }
