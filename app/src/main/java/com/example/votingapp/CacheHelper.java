package com.example.votingapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class CacheHelper {

    private static final String PREF = "vote_cache";
    private static final String KEY_VOTES = "votes_json";
    private static final Gson gson = new Gson();

    public static void saveVotes(Context ctx, List<VoteDto> votes) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String json = gson.toJson(votes);
        sp.edit().putString(KEY_VOTES, json).apply();
    }

    public static List<VoteDto> loadVotes(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String json = sp.getString(KEY_VOTES, null);
        if (json == null) return Collections.emptyList();
        Type type = new TypeToken<List<VoteDto>>(){}.getType();
        return gson.fromJson(json, type);
    }
}
