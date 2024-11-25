package com.example.whatsnext;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView pastRecyclerView;
    RecyclerView futureRecyclerView;
    List<CountdownModel> countdownModelListF = new ArrayList<>();
    List<CountdownModel> countdownModelListP = new ArrayList<>();

    String[][] events = {
            {"Event1", "2024-11-21"},
            {"Event2", "2024-12-01"},
            {"Event3", "2025-01-15"}
    };

    CountdownAdapter countdownAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        DBHandler db = new DBHandler(this);
        Log.d("DBHandler", "Data Loading...");
        ArrayList<String[][]> events = db.onLoad();
        Log.d("DBHandler", "Data Loaded");
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

        TextView futureText = findViewById(R.id.futureEventText);
        String futureString = "<u>future events V</u>";
        futureText.setText(Html.fromHtml(futureString));
        futureText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pastRecyclerView.getVisibility() == View.VISIBLE){
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
            CountdownModel countdownModel = new CountdownModel(event[0], event[1]);
            countdownModelList.add(countdownModel);
        }

        countdownAdapter = new CountdownAdapter(countdownModelList);
        recyclerView.setAdapter(countdownAdapter);
    }


}