package com.example.votingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class VoteDtoAdapter extends RecyclerView.Adapter<VoteDtoAdapter.Holder> {

    interface OnClick { void onClick(VoteDto v); }
    interface OnLongClick { void onLongClick(VoteDto v); }

    List<VoteDto> items;
    OnClick click;
    OnLongClick longClick;

    public VoteDtoAdapter(List<VoteDto> items, OnClick c, OnLongClick lc) {
        this.items = items;
        this.click = c;
        this.longClick = lc;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vote, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder h, int position) {
        VoteDto item = items.get(position);
        h.title.setText(item.name);
        h.desc.setText(item.description + " (" + item.numberOfVotes + " votes)");

        if (item.photoUrl != null && !item.photoUrl.isEmpty()) {
            h.iv.setVisibility(View.VISIBLE);
            Glide.with(h.iv.getContext()).load(item.photoUrl).centerCrop().into(h.iv);
        } else {
            h.iv.setVisibility(View.GONE);
        }

        h.itemView.setOnClickListener(v -> click.onClick(item));
        h.itemView.setOnLongClickListener(v -> {
            longClick.onLongClick(item);
            return true;
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class Holder extends RecyclerView.ViewHolder {
        TextView title, desc;
        ImageView iv;
        public Holder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.itemTitle);
            desc = itemView.findViewById(R.id.itemDesc);
            iv = itemView.findViewById(R.id.itemImage);
        }
    }
}
