package com.example.votingapp;

import android.content.Context;
import android.util.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VoteRepository {

    private static final String TAG = "VoteRepository";
    public final ApiService api;

    public VoteRepository(Context context, String baseUrl) {
        api = RetrofitClient.getClient(baseUrl).create(ApiService.class);
    }

    public void fetchVotes(Callback<List<VoteDto>> callback) {
        api.getVotes().enqueue(callback);
    }

    public void sendVote(int voteId, String deviceId, Callback<ApiResponse> callback) {
        VoteRequestBody body = new VoteRequestBody(voteId, deviceId);
        api.vote(body).enqueue(callback);
    }
}
