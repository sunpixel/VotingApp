package com.example.votingapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @GET("votes")
    Call<List<VoteDto>> getVotes();

    @POST("vote")
    Call<ApiResponse> vote(@Body VoteRequestBody body);
}
