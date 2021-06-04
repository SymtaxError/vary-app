package com.example.vary.UI;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vary.Models.CurrentGameModel;
import com.example.vary.Models.TeamModel;
import com.example.vary.R;
import com.example.vary.ViewModels.CardsViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Objects;

public class ResultRoundFragment extends Fragment {
    View view;
    private final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.5F);
    private CardsViewModel viewModel;
    private CallbackFragment callbackFunctions;

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
                this.teamNameView.setText(teamName);
                this.teamPointsView.setText(String.valueOf(points));
            }
        }

        @NonNull
        @Override
        public RoundStatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result_round, parent, false);
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

    public void setCallback(CallbackFragment callback) {
        callbackFunctions = callback;
    }

    public void setViewModel(CardsViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_result_round, container, false);

        RecyclerView roundStatsList = view.findViewById(R.id.result_round_list);
        roundStatsList.setLayoutManager(new LinearLayoutManager(getContext()));

        RoundStatsAdapter roundStatsAdapter = new RoundStatsAdapter();
        roundStatsAdapter.setViewModel(viewModel);

        Observer<List<TeamModel>> observerCurrentGame = teams -> {
            if (teams != null) {
                roundStatsAdapter.notifyDataSetChanged();
            }
        };
        viewModel
                .getTeams()
                .observe(getViewLifecycleOwner(), observerCurrentGame);
        if (viewModel.getGameAction() != GameActions.open_round_or_game_result) {
            viewModel.changeTeamPoints();
            viewModel.setCurrentRoundPoints(0);
        }
        viewModel.setGameAction(GameActions.open_round_or_game_result);
        roundStatsList.setAdapter(roundStatsAdapter);

        Observer<CurrentGameModel> currentGameModelObserver = currentGameModel -> Log.d("Model", "Changed, current team points = " + currentGameModel.getCurrentRoundPoints());
        viewModel.getGameModel().observe(getViewLifecycleOwner(), currentGameModelObserver);

        Button nextRoundButton = view.findViewById(R.id.next_round_bottom_text);
        if (viewModel.endGame()) {
            viewModel.sortTeamsByPoints();
            nextRoundButton.setText("Закончить игру");
        }

        nextRoundButton.setOnClickListener(v -> {
            v.startAnimation(buttonClick);
            viewModel.setRoundTimeLeft(viewModel.getRoundDuration());
            if (viewModel.getRoundDuration() == 0)
                viewModel.setTimerCount(-1);
            if (!viewModel.nextRound()) {
                viewModel.removeTeams();
                callbackFunctions.callback(GameActions.open_menu_and_save);
            } else {
                callbackFunctions.callback(GameActions.start_game_process);
            }
        });

        View explain_logo = view.findViewById(R.id.explain_mode_result);
        View gesture_logo = view.findViewById(R.id.gesture_mode_result);
        View one_word_logo = view.findViewById(R.id.one_word_mode_result);


        ColorStateList activeColor = ContextCompat
                .getColorStateList(Objects.requireNonNull(getContext()), R.color.primary);
        ColorStateList nextColor = ContextCompat
                .getColorStateList(getContext(), R.color.text_bright);

        GameMode gameMode = viewModel.getNextGameMode();
        if (gameMode == GameMode.explain_mode) {
            explain_logo.setBackgroundTintList(activeColor);
            gesture_logo.setBackgroundTintList(nextColor);
            one_word_logo.setBackgroundTintList(nextColor);
        } else if (gameMode == GameMode.gesture_mode) {
            explain_logo.setVisibility(View.GONE);
            gesture_logo.setBackgroundTintList(activeColor);
            one_word_logo.setBackgroundTintList(nextColor);
        } else if (gameMode == GameMode.one_word_mode){
            explain_logo.setVisibility(View.GONE);
            gesture_logo.setVisibility(View.GONE);
            one_word_logo.setBackgroundTintList(activeColor);
        } else {
            explain_logo.setVisibility(View.GONE);
            gesture_logo.setVisibility(View.GONE);
            one_word_logo.setVisibility(View.GONE);
        }

        TextView cardsLeft = view.findViewById(R.id.cards_left);
        cardsLeft.setText(viewModel.getCardsLeft()+"");


        LinearLayout cards_info = view.findViewById(R.id.v_round_result_cards_info);
        LinearLayout modes_info = view.findViewById(R.id.round_result_modes_info);

        cards_info.setOnClickListener(v -> showCardsInfo());
        modes_info.setOnClickListener(v -> showModesInfo());

        return view;
    }

    private void showModesInfo() {
        View contextView = Objects.requireNonNull(getView()).findViewById(R.id.context_view_result_round);
        Snackbar bar = Snackbar.make(contextView, R.string.modes_tooltip, Snackbar.LENGTH_LONG);
//        bar.setTextColor(getResources().getColor(R.color.primary));
        bar.show();
    }

    private void showCardsInfo() {
        View contextView = Objects.requireNonNull(getView()).findViewById(R.id.context_view_result_round);
        Snackbar bar = Snackbar.make(contextView, R.string.cards_tooltip, Snackbar.LENGTH_LONG);
//        bar.setTextColor(getResources().getColor(R.color.primary));
        bar.show();
    }
}
