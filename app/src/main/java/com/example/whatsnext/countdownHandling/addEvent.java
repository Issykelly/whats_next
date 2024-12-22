package com.example.whatsnext.countdownHandling;

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

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.icon_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

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
}
