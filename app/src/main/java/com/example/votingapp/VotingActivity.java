package com.example.votingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class VotingActivity extends AppCompatActivity {

    List<Vote> items;
    int currentIndex = 0;

    TextView tvTitle, tvDesc, tvCount;
    Button btnVote, btnNext, btnPrev, btnToggleImage, btnBack;

    boolean showImages = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);

        items = VoteStore.items;
        if (items == null) items = java.util.Collections.emptyList();

        tvTitle = findViewById(R.id.tvTitle);
        tvDesc = findViewById(R.id.tvDesc);
        tvCount = findViewById(R.id.tvCount);
        btnVote = findViewById(R.id.btnVote);
        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);
        btnToggleImage = findViewById(R.id.btnToggleImage);
        btnBack = findViewById(R.id.btnBack);

        btnVote.setOnClickListener(v -> {
            Vote item = items.get(currentIndex);
            item.votes++;
            updateUI();
            NotificationHelper.notifyBigText(this, "Vote cast", item.title + " now has " + item.votes + " votes");
        });

        btnNext.setOnClickListener(v -> {
            if (currentIndex < items.size() - 1) currentIndex++;
            updateUI();
        });
        btnPrev.setOnClickListener(v -> {
            if (currentIndex > 0) currentIndex--;
            updateUI();
        });

        btnToggleImage.setOnClickListener(v -> {
            showImages = !showImages;
            updateUI();
        });

        btnBack.setOnClickListener(v -> finish());


        updateUI();
    }

    void updateUI() {
        if (items.isEmpty()) {
            tvTitle.setText("No items");
            tvDesc.setText("");
            tvCount.setText("");
            return;
        }
        Vote item = items.get(currentIndex);
        tvTitle.setText(item.title);
        tvDesc.setText(item.description);
        tvCount.setText("Votes: " + item.votes);

        // optionally show image by changing a visibility flag; here we'll update a small image view in layout
        android.widget.ImageView iv = findViewById(R.id.ivItem);
        if (showImages && item.imageRes != 0) {
            iv.setImageResource(item.imageRes);
            iv.setVisibility(View.VISIBLE);
        } else {
            iv.setVisibility(View.GONE);
        }
    }
}