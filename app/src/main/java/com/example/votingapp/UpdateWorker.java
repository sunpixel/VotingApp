package com.example.votingapp;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class UpdateWorker extends Worker {

    private static final String TAG = "UpdateWorker";
    private static final String BASE_URL = "http://10.0.2.2:8000/"; // adjust if needed

    public UpdateWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        VoteRepository repo = new VoteRepository(getApplicationContext(), BASE_URL);
        try {
            // synchronous call since Worker's doWork runs off main thread
            Call<List<VoteDto>> call = repo.api.getVotes(); // we made 'api' public? if not, call repo.fetchVotes with sync alternative
            Response<List<VoteDto>> resp = call.execute();
            if (resp.isSuccessful()) {
                // Save to a simple cache: write to a local file or SharedPreferences as JSON.
                List<VoteDto> list = resp.body();
                CacheHelper.saveVotes(getApplicationContext(), list);
                return Result.success();
            } else {
                Log.w(TAG, "Update failed: " + resp.code());
                return Result.retry();
            }
        } catch (Exception e) {
            Log.e(TAG, "UpdateWorker exception", e);
            return Result.retry();
        }
    }
}
