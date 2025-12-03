package com.example.votingapp;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ResultsActivity extends AppCompatActivity {

    List<Vote> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        items = VoteStore.items;
        if (items == null) items = Collections.emptyList();

        TextView tvTop = findViewById(R.id.tvTop);
        ListView lv = findViewById(R.id.lvResults);

        if (items.isEmpty()) {
            tvTop.setText("No votes yet");
            return;
        }

        // Sort items by vote count (desc)
        items.sort(Comparator.comparingInt(v -> -v.votes));

        Vote top = items.get(0);
        tvTop.setText("Top: " + top.title + " (" + top.votes + " votes)");

        // Create a simple list
        String[] lines = new String[items.size()];
        for (int i = 0; i < items.size(); i++) {
            lines[i] = items.get(i).title + " - " + items.get(i).votes + " votes";
        }

        lv.setAdapter(new android.widget.ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                lines
        ));
    }
}
