package com.example.votingapp;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VotingActivity extends AppCompatActivity {

    private final String BASE_URL = "http://10.0.2.2:5079/api/";
    VoteRepository repo;
    List<VoteDto> items;
    int voteIdParam = -1;
    VoteDto currentItem;

    TextView tvTitle, tvDesc, tvVotes;
    ImageView ivImage;
    Button btnVote, btnBack, btnNext, btnPrev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);

        repo = new VoteRepository(this, BASE_URL);

        tvTitle = findViewById(R.id.tvTitle);
        tvDesc = findViewById(R.id.tvDesc);
        tvVotes = findViewById(R.id.tvCount);
        ivImage = findViewById(R.id.ivItem);

        btnVote = findViewById(R.id.btnVote);
        btnBack = findViewById(R.id.btnBack);
        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);

        voteIdParam = getIntent().getIntExtra("voteId", -1);

        // Load cached list then refresh
        items = CacheHelper.loadVotes(this);
        if (items.isEmpty()) {
            // try server immediately
            fetchVotesAndShow();
        } else {
            findAndShowCurrent();
            fetchVotesInBackground();
        }

        btnVote.setOnClickListener(v -> doVote());
        btnBack.setOnClickListener(v -> finish());

        btnNext.setOnClickListener(v -> {
            int idx = findIndex(currentItem.id);
            if (idx < items.size() - 1) {
                currentItem = items.get(idx + 1);
                showItem(currentItem);
            }
        });

        btnPrev.setOnClickListener(v -> {
            int idx = findIndex(currentItem.id);
            if (idx > 0) {
                currentItem = items.get(idx - 1);
                showItem(currentItem);
            }
        });
    }

    private void fetchVotesAndShow() {
        repo.fetchVotes(new Callback<List<VoteDto>>() {
            @Override
            public void onResponse(Call<List<VoteDto>> call, Response<List<VoteDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    items = response.body();
                    CacheHelper.saveVotes(VotingActivity.this, items);
                    findAndShowCurrent();
                } else {
                    Toast.makeText(VotingActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<VoteDto>> call, Throwable t) {
                Toast.makeText(VotingActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchVotesInBackground() {
        // same as above but non-blocking — used to refresh counts
        repo.fetchVotes(new Callback<List<VoteDto>>() {
            @Override
            public void onResponse(Call<List<VoteDto>> call, Response<List<VoteDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    items = response.body();
                    CacheHelper.saveVotes(VotingActivity.this, items);
                    findAndShowCurrent();
                }
            }
            @Override public void onFailure(Call<List<VoteDto>> call, Throwable t) {}
        });
    }

    private void findAndShowCurrent() {
        if (items == null || items.isEmpty()) {
            tvTitle.setText("No items");
            tvDesc.setText("");
            tvVotes.setText("");
            ivImage.setVisibility(View.GONE);
            btnVote.setEnabled(false);
            return;
        }

        if (voteIdParam != -1) {
            for (VoteDto v : items) {
                if (v.id == voteIdParam) {
                    currentItem = v;
                    break;
                }
            }
        }
        if (currentItem == null) currentItem = items.get(0);
        showItem(currentItem);
    }

    private void showItem(VoteDto item) {
        currentItem = item;
        tvTitle.setText(item.name);
        tvDesc.setText(item.description);
        tvVotes.setText("Votes: " + item.numberOfVotes);
/*        if (item.photoUrl != null && !item.photoUrl.isEmpty()) {
            ivImage.setVisibility(View.VISIBLE);
            Glide.with(this).load(item.photoUrl).fitCenter().into(ivImage);
        } else {
            ivImage.setVisibility(View.GONE);
        }*/

        // Disable vote button if device already voted (client-side quick block)
        String deviceId = getDeviceIdString();
        boolean already = item.whoVoted != null && item.whoVoted.contains(deviceId);
        btnVote.setEnabled(!already);
    }

    private void doVote() {
        if (currentItem == null) return;
        String deviceId = getDeviceIdString();
        repo.sendVote(currentItem.id, deviceId, new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse r = response.body();
                    if ("ok".equalsIgnoreCase(r.status)) {
                        Toast.makeText(VotingActivity.this, "Thanks! Vote counted.", Toast.LENGTH_SHORT).show();
                        // refresh data
                        fetchVotesInBackground();
                    } else {
                        Toast.makeText(VotingActivity.this, r.message, Toast.LENGTH_SHORT).show();
                        btnVote.setEnabled(false);
                    }
                } else if (response.code() == 400) {
                    Toast.makeText(VotingActivity.this, "Already voted", Toast.LENGTH_SHORT).show();
                    btnVote.setEnabled(false);
                } else {
                    Toast.makeText(VotingActivity.this, "Vote failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(VotingActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int findIndex(int id) {
        for (int i = 0; i < items.size(); i++) if (items.get(i).id == id) return i;
        return 0;
    }

    private String getDeviceIdString() {
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        if (androidId == null) {
            androidId = "unknown-" + System.currentTimeMillis();
        }
        return androidId;
    }
}
