package com.example.vary;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;


public class GameSettingsFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener {
    private TextView amountCards;
    private TextView time;
    private SeekBar timeBar;
    private SeekBar amountCardsBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_settings, container, false);

        Button cardDeckButton = view.findViewById(R.id.button_deck_card);
        cardDeckButton.setOnClickListener(this::onCardDeckButtonClick);

        Button startGameButton = view.findViewById(R.id.start_game_button);
        startGameButton.setOnClickListener(this::onStartGameButtonClick);

        amountCards = view.findViewById(R.id.amount_cards);
        amountCards.setText(getResources().getText(R.string.amount_cards) + "   " + getResources().getInteger(R.integer.defalut_amount_card));

        amountCardsBar = view.findViewById(R.id.bar_amount_cards);
        amountCardsBar.setOnSeekBarChangeListener(this);

        timeBar = view.findViewById(R.id.time_round);
        timeBar.setOnSeekBarChangeListener(this);

        time = view.findViewById(R.id.time_round_text);
        time.setText(getResources().getText(R.string.time_round)  + "   " + getResources().getInteger(R.integer.default_time));

        Spinner commandsSpinner = view.findViewById(R.id.choose_command_spinner);

        ArrayAdapter<?> arrayAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, CommandsSource.getCommandNames());
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        commandsSpinner.setAdapter(arrayAdapter);

        Switch theftRightSwitch = view.findViewById(R.id.theft_right);
        theftRightSwitch.setOnCheckedChangeListener(this);

        RadioGroup penaltyGroup = view.findViewById(R.id.penalty_group);
        penaltyGroup.setOnCheckedChangeListener(this::onPenaltyGroupClicked);

        return view;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.equals(amountCardsBar)) {
            progress /= getResources().getInteger(R.integer.card_amount_step);
            progress *= getResources().getInteger(R.integer.card_amount_step);
            amountCards.setText(getResources().getText(R.string.amount_cards) + "   " + progress);
        } else if (seekBar.equals(timeBar)) {
            progress /= getResources().getInteger(R.integer.time_step);
            progress *= getResources().getInteger(R.integer.time_step);
            time.setText(getResources().getText(R.string.time_round) + "   " +  progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // TODO заглушка для "право на кражу"
    }

    public void onPenaltyGroupClicked( RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.no_penalty:
                // TODO отсутствие штрафа
                break;
            case  R.id.loss_points_penalty:
                // TODO потеря очков
                break;
            case R.id.players_task_penalty:
                // TODO вопрос от игроков
                break;
        }
    }

    public void onCardDeckButtonClick(View view) {
        // TODO нажали на кнопку выбрать деку
    }

    public void onStartGameButtonClick(View view) {
        // TODO выбрали настройки и нажали на далее
            OnGameFragment fragment = new OnGameFragment();
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
    }
}