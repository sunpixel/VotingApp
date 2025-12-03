package com.example.votingapp;

import androidx.annotation.DrawableRes;

public class Vote {
    public String id;
    public String title;
    public String description;
    public int votes;
    @DrawableRes
    public int imageRes; // 0 if none

    public Vote(String id, String title, String description, int imageRes) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.votes = 0;
        this.imageRes = imageRes;
    }
}