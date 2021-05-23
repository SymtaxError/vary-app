package com.example.vary.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class ResultTeamFragment extends Fragment {
    View view;
    private CardsViewModel viewModel;
    private CallbackFragment callbackFunctions;

    private static class TeamStatsAdapter extends RecyclerView.Adapter<TeamStatsAdapter.TeamStatsViewHolder> {
        CardsViewModel viewModel;

        public void setViewModel(CardsViewModel viewModel) {
            this.viewModel = viewModel;
        }

        static class TeamStatsViewHolder extends RecyclerView.ViewHolder {
            private final TextView wordNameView;

            public TeamStatsViewHolder(@NonNull View teamView) {
                super(teamView);
                this.wordNameView = teamView.findViewById(R.id.word_from_game);
            }

            public void bind(String wordName, View.OnClickListener listener) {
                wordNameView.setText(wordName);
                wordNameView.setOnClickListener(listener);
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
            String wordName = viewModel.getUsedCardByPosition(position) + " " + viewModel.getAnswerState(position);
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewModel.changeAnswerState(position);
                }
            };
            holder.bind(wordName, listener);
        }

        @Override
        public int getItemCount() {
            return viewModel.getAmountOfUsedCards();
        }
    }

    public void setCallback(CallbackFragment callback) {
        callbackFunctions = callback;
    }

    public void setViewModel(CardsViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_result_team, container, false);

        RecyclerView teamStatsList = view.findViewById(R.id.result_team_list);
        teamStatsList.setLayoutManager(new LinearLayoutManager(getContext()));

        TeamStatsAdapter teamStatsAdapter = new TeamStatsAdapter();
        teamStatsAdapter.setViewModel(viewModel);

        Observer<List<CardModel>> observerCurrentGame = new Observer<List<CardModel>>() {
            @Override
            public void onChanged(List<CardModel> cardModels) {
                if (cardModels != null) {
                    teamStatsAdapter.notifyDataSetChanged();
                }
            }
        };
        viewModel
                .getCards()
                .observe(getViewLifecycleOwner(), observerCurrentGame);

        teamStatsList.setAdapter(teamStatsAdapter);

        Button okButton = view.findViewById(R.id.team_result_bottom_text);
        okButton.setOnClickListener(v -> callbackFunctions.callback(GameActions.open_round_or_game_result));

        return view;
    }
}
