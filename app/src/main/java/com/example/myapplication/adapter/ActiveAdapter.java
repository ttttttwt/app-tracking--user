package com.example.myapplication.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ActiveViewBinding;
import com.example.myapplication.network.model.ActiveNetwork;

import java.util.List;

public class ActiveAdapter extends RecyclerView.Adapter<ActiveAdapter.ActiveViewHolder> {
    List<ActiveNetwork> actives;

    public ActiveAdapter(List<ActiveNetwork> actives) {
        this.actives = actives;
    }

    public static class ActiveViewHolder extends RecyclerView.ViewHolder {
        ActiveViewBinding activeViewBinding;
        public ActiveViewHolder(@NonNull ActiveViewBinding activeViewBinding) {
            super(activeViewBinding.getRoot());
            this.activeViewBinding = activeViewBinding;
        }
    }

    @NonNull
    @Override
    public ActiveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActiveViewBinding binding = ActiveViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ActiveViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ActiveViewHolder holder, int position) {
        ActiveNetwork active = actives.get(position);
        if (active != null) {
            holder.activeViewBinding.dateText.setText(active.getDate());
            holder.activeViewBinding.timeText.setText(String.valueOf(active.getTime()));
            holder.activeViewBinding.speedText.setText(String.valueOf(active.getSpeed()));
            holder.activeViewBinding.distanceText.setText(String.valueOf(active.getDistance()));

        }
    }

    @Override
    public int getItemCount() {
        if (actives != null) {
            return actives.size();
        }
        return 0;
    }
}
