package com.example.votingapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VoteAdapter extends RecyclerView.Adapter<VoteAdapter.Holder> {

    public interface OnVoteClick {
        void onVote(Vote v);
    }

    public interface OnEditClick {
        void onEdit(Vote v);
    }

    List<Vote> items;
    OnVoteClick voteListener;
    OnEditClick editListener;

    public VoteAdapter(List<Vote> items,
                       OnVoteClick voteListener,
                       OnEditClick editListener) {

        this.items = items;
        this.voteListener = voteListener;
        this.editListener = editListener;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vote, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder h, int position) {
        Vote item = items.get(position);

        h.tvTitle.setText(item.title);
        h.tvDesc.setText(item.description + " (" + item.votes + " votes)");

        if (item.imageRes != 0) {
            h.ivImage.setImageResource(item.imageRes);
            h.ivImage.setVisibility(View.VISIBLE);
        } else {
            h.ivImage.setVisibility(View.GONE);
        }

        // Click = vote
        h.itemView.setOnClickListener(v -> voteListener.onVote(item));

        // Long click = edit
        h.itemView.setOnLongClickListener(v -> {
            editListener.onEdit(item);
            return true;
        });

        // Button inside item to trigger notification
        h.btnNotify.setOnClickListener(v -> {
            NotificationHelper.notifyInbox(
                    h.itemView.getContext(),
                    item.title,
                    item.description,
                    "Votes: " + item.votes
            );

            Toast.makeText(h.itemView.getContext(),
                    "Notification sent for " + item.title,
                    Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class Holder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvDesc;
        ImageView ivImage, btnNotify;

        public Holder(View v) {
            super(v);
            tvTitle = v.findViewById(R.id.itemTitle);
            tvDesc = v.findViewById(R.id.itemDesc);
            ivImage = v.findViewById(R.id.itemImage);
            btnNotify = v.findViewById(R.id.btnNotify);
        }
    }
}
