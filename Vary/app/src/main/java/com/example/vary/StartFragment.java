package com.example.vary;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.view.ViewGroup.MarginLayoutParams;

import java.util.List;

public class StartFragment extends Fragment {

    CallbackFragment fCallback;
    Button continue_game;
    View view;
    int width;
    private CardsViewModel viewModel;

    public StartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.start_fragment_placeholder, container, false);
        int margin = width / 3;

        continue_game = view.findViewById(R.id.continue_game);
        continue_game.setOnClickListener(v -> fCallback.callback(GameActions.continue_game_action));
        MarginLayoutParams params = (MarginLayoutParams) continue_game.getLayoutParams();  // Настройка отступов кнопок
        params.setMarginEnd(margin);
        params.setMarginStart(margin);
        continue_game.setLayoutParams(params);
        Button new_game = view.findViewById(R.id.new_game);
        new_game.setOnClickListener(v -> fCallback.callback(GameActions.new_game_action));
        new_game.setLayoutParams(params);

        bindButton(R.id.rules, GameActions.open_rules);
        bindImageButton(R.id.settings, GameActions.open_settings);

        Observer<List<CommandModel>> observer = new Observer<List<CommandModel>>() {
            @Override
            public void onChanged(List<CommandModel> commandModels) {
                checkContinueButtonVisibility(commandModels.size());
            }
        };

        viewModel = new ViewModelProvider(requireActivity()).get(CardsViewModel.class);

        viewModel
                .getCommands()
                .observe(getViewLifecycleOwner(), observer);

        return view;
    }


    public void setWidth(int width) {
        this.width = width;
    } // Определение ширины текущего экрана

    void bindButton(int id, GameActions action) {
        Button button = view.findViewById(id);
        button.setOnClickListener(v -> fCallback.callback(action));
    }

    void bindImageButton(int id, GameActions action) {
        ImageButton button = view.findViewById(id);
        button.setOnClickListener(v -> fCallback.callback(action));
    }

    void setCallback(CallbackFragment callback) {
        fCallback = callback;
    }

    void checkContinueButtonVisibility(int size) { //Настройка прозрачности кнопки "продолжить" в зависимости от наличия сохраненного состояния
        int amount = getResources().getInteger(R.integer.min_commands_amount);
        if (size <= amount) {
            continue_game.setVisibility(View.GONE);
        } else {
            continue_game.setVisibility(View.VISIBLE);
        }
    }

}