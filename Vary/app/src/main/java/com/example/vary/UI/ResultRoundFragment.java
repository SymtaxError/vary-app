package com.example.vary.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vary.Models.CardModel;
import com.example.vary.R;
import com.example.vary.ViewModels.CardsViewModel;

import java.util.List;

public class ResultRoundFragment extends Fragment {
    View view;
    private CardsViewModel viewModel;

    private static class RoundStatsAdapter extends RecyclerView.Adapter<RoundStatsAdapter.RoundStatsViewHolder> {
        CardsViewModel viewModel;

        public void setViewModel(CardsViewModel viewModel) {
            this.viewModel = viewModel;
        }

        static class RoundStatsViewHolder extends RecyclerView.ViewHolder {
            private final TextView teamNameView;
            private final TextView teamPointsView;

            public RoundStatsViewHolder(@NonNull View teamView) {
                super(teamView);
                this.teamNameView = teamView.findViewById(R.id.team_name);
                this.teamPointsView = teamView.findViewById(R.id.team_points);
            }

            public void bind(String teamName, int points) {
                teamNameView.setText(teamName);
                teamPointsView.setText(String.valueOf(points));
            }
        }

        @NonNull
        @Override
        public RoundStatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result_team, parent, false);
            return new RoundStatsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RoundStatsViewHolder holder, int position) {
            String teamName = viewModel.getTeamName(position);
            int teamPoints = viewModel.getTeamPoints(position);
            holder.bind(teamName, teamPoints);
        }

        @Override
        public int getItemCount() {
            return viewModel.getSize();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_result_round, container, false);

        RecyclerView teamsStatsList = view.findViewById(R.id.result_round_list);
        teamsStatsList.setLayoutManager(new LinearLayoutManager(getContext()));

        RoundStatsAdapter roundStatsAdapter = new RoundStatsAdapter();
        roundStatsAdapter.setViewModel(viewModel);

        teamsStatsList.setAdapter(roundStatsAdapter);

        return view;
    }
}
