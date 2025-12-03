package com.example.votingapp;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {


    List<Vote> items = new ArrayList<>();
    VoteAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


// sample data (use drawable resources if you placed images)
        items.add(new Vote(UUID.randomUUID().toString(), "Option A", "Description A", R.drawable.img1));
        items.add(new Vote(UUID.randomUUID().toString(), "Option B", "Description B", R.drawable.img2));
        items.add(new Vote(UUID.randomUUID().toString(), "No Image Option", "Text only option", 0));


        RecyclerView rv = findViewById(R.id.rvVotes);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VoteAdapter(items, this::onVoteClicked, this::onEditClicked);
        rv.setAdapter(adapter);


        Button btnNew = findViewById(R.id.btnAdd);
        btnNew.setOnClickListener(v -> showAddDialog());


        Button btnStartVoting = findViewById(R.id.btnStartVoting);
        btnStartVoting.setOnClickListener(v -> {
            Intent i = new Intent(this, VotingActivity.class);
// pass items (simple approach: serialize minimal fields)
// For demo, using a static holder would be easier. We'll use a simple shared static place.
            VoteStore.items = items;
            startActivity(i);
        });


        Button btnResults = findViewById(R.id.btnResults);
        btnResults.setOnClickListener(v -> startActivity(new Intent(this, ResultsActivity.class)));


// Simple notification example on open
        NotificationHelper.notifySimple(this, "Welcome", "Open the app to start voting.");
    }


    void onVoteClicked(Vote vote) {
// show confirm dialog and increment
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Confirm Vote");
        b.setMessage("Vote for " + vote.title + "?");
        b.setPositiveButton("Yes", (dialog, which) -> {
            vote.votes++;
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Voted for " + vote.title, Toast.LENGTH_SHORT).show();
            NotificationHelper.notifyAction(this, vote.title + " voted", "Thanks for voting!");
        });
        b.setNegativeButton("Cancel", null);
        b.show();
    }


    void onEditClicked(Vote vote) {
// simple edit dialog to change title
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout = inflater.inflate(R.layout.dialog_add_vote, null);
        EditText etTitle = layout.findViewById(R.id.etTitle);
        EditText etDesc = layout.findViewById(R.id.etDesc);
        etTitle.setText(vote.title);
        etDesc.setText(vote.description);


        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Edit Item");
        b.setView(layout);
        b.setPositiveButton("Save", (dialog, which) -> {
            vote.title = etTitle.getText().toString();
            vote.description = etDesc.getText().toString();
            adapter.notifyDataSetChanged();
        });
        b.setNegativeButton("Cancel", null);
        b.show();
    }


    void showAddDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout = inflater.inflate(R.layout.dialog_add_vote, null);
        EditText etTitle = layout.findViewById(R.id.etTitle);
        EditText etDesc = layout.findViewById(R.id.etDesc);


        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Add New Item");
        b.setView(layout);
        b.setPositiveButton("Add", (dialog, which) -> {
            String t = etTitle.getText().toString().trim();
            String d = etDesc.getText().toString().trim();
            if (t.isEmpty()) {
                Toast.makeText(this, "Title required", Toast.LENGTH_SHORT).show();
                return;
            }
            Vote v = new Vote(UUID.randomUUID().toString(), t, d, 0);
            items.add(v);
            adapter.notifyDataSetChanged();


// Show a progress notification for background processing demo
            NotificationHelper.notifyProgress(this, "Adding item", "Processing new item...");
        });
        b.setNegativeButton("Cancel", null);
        b.show();
    }
}