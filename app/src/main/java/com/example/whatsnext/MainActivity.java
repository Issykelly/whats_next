package com.example.whatsnext;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
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

import com.example.whatsnext.countdownHandling.CountdownAdapter;
import com.example.whatsnext.countdownHandling.CountdownModel;
import com.example.whatsnext.countdownHandling.addEvent;
import com.example.whatsnext.database.DBHandler;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView pastRecyclerView;
    RecyclerView futureRecyclerView;
    List<CountdownModel> countdownModelListF = new ArrayList<>();
    List<CountdownModel> countdownModelListP = new ArrayList<>();

    CountdownAdapter countdownAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        DBHandler db = new DBHandler(this);
        ArrayList<String[][]> events = db.onLoadEvents();
        String [][] futureEvents = events.get(0);
        String [][] pastEvents = events.get(1);


        // Toolbar setup
        pastRecyclerView = findViewById(R.id.pastView);
        futureRecyclerView = findViewById(R.id.futureView);

        // RecyclerView setup
        setupForCountdown(pastRecyclerView, pastEvents, countdownModelListP);
        setupForCountdown(futureRecyclerView, futureEvents, countdownModelListF);

        // text to set labels default names & onclick events;
        TextView pastText = findViewById(R.id.pastEventText);
        String pastString = "<u>past events Λ</u>";
        pastText.setText(Html.fromHtml(pastString));
        pastText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pastRecyclerView.getVisibility() == View.VISIBLE){
                    String pastString = "<u>past events Λ</u>";
                    pastText.setText(Html.fromHtml(pastString));
                    pastRecyclerView.setVisibility(View.GONE);
                } else {
                    String pastString = "<u>past events V</u>";
                    pastText.setText(Html.fromHtml(pastString));
                    pastRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        });

        Button addButton = findViewById(R.id.addButton);
        // Set an OnClickListener on the button
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the addEvent activity
                Intent intent = new Intent(MainActivity.this, addEvent.class);
                startActivity(intent);
            }
        });

        Button habitsButton = findViewById(R.id.habitsButton);
        habitsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the addEvent activity
                Intent intent = new Intent(MainActivity.this, MainActivityHabits.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        TextView futureText = findViewById(R.id.futureEventText);
        String futureString = "<u>future events V</u>";
        futureText.setText(Html.fromHtml(futureString));
        futureText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (futureRecyclerView.getVisibility() == View.VISIBLE){
                    String futureString = "<u>future events Λ</u>";
                    futureText.setText(Html.fromHtml(futureString));
                    futureRecyclerView.setVisibility(View.GONE);
                } else {
                    String futureString = "<u>future events V</u>";
                    futureText.setText(Html.fromHtml(futureString));
                    futureRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupForCountdown(RecyclerView recyclerView, String[][] events,
                                  List<CountdownModel> countdownModelList) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        for (String[] event : events) {
            CountdownModel countdownModel = new CountdownModel(event[3], event[0], event[1], event[2], event[4]);
            Log.e("kms", event[4]);
            countdownModelList.add(countdownModel);
        }

        countdownAdapter = new CountdownAdapter(countdownModelList, position -> {
            CountdownModel selectedModel = countdownModelList.get(position);
            Intent intent = new Intent(MainActivity.this, addEvent.class);
            intent.putExtra("countdownModel", selectedModel);
            startActivity(intent);
        });
        recyclerView.setAdapter(countdownAdapter);
        };
    }