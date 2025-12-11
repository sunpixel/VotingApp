package com.example.votingapp;

import java.io.Serializable;
import java.util.List;

// DTO соответствует JSON с сервера
public class VoteDto implements Serializable {
    public int id;
    public String name;
    public String description;
    public String photoUrl; // может быть null
    public int numberOfVotes;
    public List<String> whoVoted;
}
