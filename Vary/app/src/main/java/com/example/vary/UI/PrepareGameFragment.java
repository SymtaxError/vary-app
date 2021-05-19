package com.example.vary.UI;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.vary.Models.CardModel;
import com.example.vary.R;
import com.example.vary.ViewModels.CardsViewModel;

import java.util.List;


public class PrepareGameFragment extends Fragment {
    CallbackFragment fCallback;
    View view;
    private CardsViewModel viewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_prepare_game, container, false);

        bindButton(R.id.start_game_process_button, GameActions.start_game_process);
        TextView teamBeginName = view.findViewById(R.id.start_team);
        setViewModel();

        teamBeginName.setText("Начинает команда \n\n" + viewModel.getCurTeamName(0));

        return view;
    }


    void setCallback(CallbackFragment callback) {
        fCallback = callback;
    }

    void bindButton(int id, GameActions action) {
        Button button = view.findViewById(id);
        button.setOnClickListener(v -> fCallback.callback(action));
    }

    protected void setViewModel() {
        Observer<List<CardModel>> observer = new Observer<List<CardModel>>() {
            @Override
            public void onChanged(List<CardModel> cardModels) {
            }
        };

        viewModel = new ViewModelProvider(requireActivity()).get(CardsViewModel.class);
        viewModel
                .getCards()
                .observe(getViewLifecycleOwner(), observer);
    }

}