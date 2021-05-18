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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vary.R;
import com.example.vary.ViewModels.CardsViewModel;

public class ResultRoundFragment extends Fragment {
    View view;
    private CardsViewModel viewModel;

    private static class RoundStatsAdapter extends RecyclerView.Adapter<RoundStatsAdapter.RoundStatsViewHolder> {
        CardsViewModel viewModel;

        public void setViewModel(CardsViewModel viewModel) {
            this.viewModel = viewModel;
        }

        static class RoundStatsViewHolder extends RecyclerView.ViewHolder {
            private final TextView wordNameView;
            public RoundStatsViewHolder(@NonNull View teamView) {
                super(teamView);
                this.wordNameView = teamView.findViewById(R.id.word_from_game);
            }

            public void bind(String wordName) {
                wordNameView.setText(wordName);
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
            String wordName = "Вставить название слова сюда";
            holder.bind(wordName);
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

        RecyclerView roundStatsList = view.findViewById(R.id.result_round_list);
        roundStatsList.setLayoutManager(new LinearLayoutManager(getContext()));

        RoundStatsAdapter roundStatsAdapter = new RoundStatsAdapter();
        roundStatsAdapter.setViewModel(viewModel);

        roundStatsList.setAdapter(roundStatsAdapter);

        Button okButton = view.findViewById(R.id.result_ok_button);

        return view;
    }
}
