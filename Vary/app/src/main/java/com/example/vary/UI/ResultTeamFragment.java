package com.example.vary.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vary.R;
import com.example.vary.ViewModels.CardsViewModel;

public class ResultTeamFragment extends Fragment {
    View view;
    private CardsViewModel viewModel;

    private static class TeamStatsAdapter extends RecyclerView.Adapter<TeamStatsAdapter.TeamStatsViewHolder> {
        CardsViewModel viewModel;

        public void setViewModel(CardsViewModel viewModel) {
            this.viewModel = viewModel;
        }

        static class TeamStatsViewHolder extends RecyclerView.ViewHolder {
            private final TextView teamNameView;
            private final TextView teamPointsView;

            public TeamStatsViewHolder(@NonNull View teamView) {
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
        public TeamStatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result_team, parent, false);
            return new TeamStatsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TeamStatsViewHolder holder, int position) {
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
        view = inflater.inflate(R.layout.fragment_result_team, container, false);

        RecyclerView teamsStatsList = view.findViewById(R.id.result_round_list);
        teamsStatsList.setLayoutManager(new LinearLayoutManager(getContext()));

        TeamStatsAdapter teamStatsAdapter = new TeamStatsAdapter();
        teamStatsAdapter.setViewModel(viewModel);

        teamsStatsList.setAdapter(teamStatsAdapter);

        return view;
    }
}
