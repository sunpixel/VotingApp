package com.example.votingapp;

public class VoteRequestBody {
    public int voteId;
    public String deviceId;

    public VoteRequestBody(int voteId, String deviceId) {
        this.voteId = voteId;
        this.deviceId = deviceId;
    }
}
