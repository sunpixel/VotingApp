package com.example.votingapp;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private final String BASE_URL = "http://10.0.2.2:5079/api/"; // change if needed
    List<VoteDto> items = new ArrayList<>();
    VoteDtoAdapter adapter; // we'll create an adapter for VoteDto (or reuse existing with slight changes)
    VoteRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repo = new VoteRepository(this, BASE_URL);

        RecyclerView rv = findViewById(R.id.rvVotes);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VoteDtoAdapter(items, this::onVoteClicked, this::onEditClicked);
        rv.setAdapter(adapter);

        Button btnRefresh = findViewById(R.id.btnRefresh);
        Button btnStartPolling = findViewById(R.id.btnStartPolling);
        Button btnStopPolling = findViewById(R.id.btnStopPolling);
        Button btnStartVoting = findViewById(R.id.btnStartVoting);
        Button btnResults = findViewById(R.id.btnResults);

        btnRefresh.setOnClickListener(v -> loadVotesFromServer());

        btnStartPolling.setOnClickListener(v -> {
            Intent intent = new Intent(this, PollingService.class);
            startForegroundService(intent);
            Toast.makeText(this, "Polling started", Toast.LENGTH_SHORT).show();
        });

        btnStopPolling.setOnClickListener(v -> {
            Intent intent = new Intent(this, PollingService.class);
            stopService(intent);
            Toast.makeText(this, "Polling stopped", Toast.LENGTH_SHORT).show();
        });

        btnStartVoting.setOnClickListener(v -> {
            Intent i = new Intent(this, VotingActivity.class);
            startActivity(i);
        });

        btnResults.setOnClickListener(v -> startActivity(new Intent(this, ResultsActivity.class)));

        // Schedule WorkManager periodic update every 15 minutes (minimum)
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(UpdateWorker.class, 15, TimeUnit.MINUTES)
                .build();
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("vote_updates", ExistingPeriodicWorkPolicy.KEEP, request);

        // Load cached votes immediately
        List<VoteDto> cached = CacheHelper.loadVotes(this);
        if (!cached.isEmpty()) {
            items.clear();
            items.addAll(cached);
            adapter.notifyDataSetChanged();
        }

        // Then refresh from server
        loadVotesFromServer();
    }

    private void loadVotesFromServer() {
        repo.fetchVotes(new Callback<List<VoteDto>>() {
            @Override
            public void onResponse(Call<List<VoteDto>> call, Response<List<VoteDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    items.clear();
                    items.addAll(response.body());
                    CacheHelper.saveVotes(MainActivity.this, items);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<VoteDto>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onVoteClicked(VoteDto vote) {
        // open VotingActivity with selected index
        Intent i = new Intent(this, VotingActivity.class);
        i.putExtra("voteId", vote.id);
        startActivity(i);
    }

    private void onEditClicked(VoteDto vote) {
        // optional: open a dialog to edit (only if allowed)
    }
}
