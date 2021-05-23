package com.example.vary.UI;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.vary.Models.CategoryModel;
import com.example.vary.Models.TeamModel;
import com.example.vary.R;
import com.example.vary.ViewModels.CardsViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class GameSettingsFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener {
    private TextView amountCards;
    private TextView time;
    private SeekBar timeBar;
    private SeekBar amountCardsBar;
    private CardsViewModel viewModel;
    private ArrayAdapter<String> arrayAdapterTeams;
    private ArrayAdapter<String> arrayAdapterCategories;
    private ArrayList<String> mTeamsNames = new ArrayList<>();
    private ArrayList<String> mCategoriesNames = new ArrayList<>();
    private PenaltyType penalty;
    private int amountOfCards;
    private int roundDuration;
    private int startTeam;
    private int startCategory;
    private boolean steal;
    CallbackFragment fCallback;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_game_settings, container, false);

        Spinner cardDeckSpinner = view.findViewById(R.id.spinner_deck_card);
//        Button cardDeckButton = view.findViewById(R.id.button_deck_card);
//        cardDeckButton.setOnClickListener(this::onCardDeckButtonClick);

        arrayAdapterCategories = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, mCategoriesNames);
        arrayAdapterCategories.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        cardDeckSpinner.setAdapter(arrayAdapterCategories);
        cardDeckSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                startCategory = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        amountOfCards = 50;
        roundDuration = 60;

        bindButton(R.id.start_game_button, GameActions.prepare_game);

        amountCards = view.findViewById(R.id.amount_cards);
        amountCards.setText(getResources().getText(R.string.amount_cards) + "   " + getResources().getInteger(R.integer.defalut_amount_card));

        amountCardsBar = view.findViewById(R.id.bar_amount_cards);
        amountCardsBar.setOnSeekBarChangeListener(this);

        timeBar = view.findViewById(R.id.time_round);
        timeBar.setOnSeekBarChangeListener(this);

        time = view.findViewById(R.id.time_round_text);
        time.setText(getResources().getText(R.string.time_round)  + "   " + getResources().getInteger(R.integer.default_time));

        Spinner teamsSpinner = view.findViewById(R.id.choose_team_spinner);

        arrayAdapterTeams = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, mTeamsNames);
        arrayAdapterTeams.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        teamsSpinner.setAdapter(arrayAdapterTeams);
        teamsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                startTeam = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        setObservers();
        Switch theftRightSwitch = view.findViewById(R.id.theft_right);
        theftRightSwitch.setOnCheckedChangeListener(this);

        RadioGroup penaltyGroup = view.findViewById(R.id.penalty_group);
        penaltyGroup.setOnCheckedChangeListener(this::onPenaltyGroupClicked);

        return view;
    }

    protected void setObservers() {
        Observer<List<TeamModel>> observer = new Observer<List<TeamModel>>() {
            @Override
            public void onChanged(List<TeamModel> teamModels) {
                if (teamModels != null) {
                    mTeamsNames = viewModel.getTeamsNames();
                    arrayAdapterTeams.clear();
                    arrayAdapterTeams.add("Случайная");
                    arrayAdapterTeams.addAll(mTeamsNames);
                    arrayAdapterTeams.notifyDataSetChanged();
                }
            }
        };

        Observer<List<CategoryModel>> observerCategories = new Observer<List<CategoryModel>>() {
            @Override
            public void onChanged(List<CategoryModel> categoryModels) {
                if (categoryModels != null) {
                    mCategoriesNames = viewModel.getCategoriesNames();
                    arrayAdapterCategories.clear();
                    arrayAdapterCategories.addAll(mCategoriesNames);
                    arrayAdapterCategories.notifyDataSetChanged();
                }
            }
        };
        viewModel = new ViewModelProvider(requireActivity()).get(CardsViewModel.class);
        viewModel
                .getTeams()
                .observe(getViewLifecycleOwner(), observer);
        viewModel
                .getCategories()
                .observe(getViewLifecycleOwner(), observerCategories);

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.equals(amountCardsBar) && progress > 10) {
            progress /= getResources().getInteger(R.integer.card_amount_step);
            progress *= getResources().getInteger(R.integer.card_amount_step);
            amountOfCards = progress;
            amountCards.setText(getResources().getText(R.string.amount_cards) + "   " + progress);
        } else if (seekBar.equals(timeBar) && progress > 10) {
            progress /= getResources().getInteger(R.integer.time_step);
            progress *= getResources().getInteger(R.integer.time_step);
            roundDuration = progress;
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
        steal = isChecked;
    }

    void setCallback(CallbackFragment callback) {
        fCallback = callback;
    }

    void bindButton(int id, GameActions action) {
        Button button = view.findViewById(id);
        button.setOnClickListener(v -> {
            Log.d("Settings", "round duration: " + roundDuration);
            if (startTeam == 0) {
                Random random = new Random();
                startTeam = random.nextInt(mTeamsNames.size());
            } else {
                startTeam--;
            }
            viewModel.setCurrentGame(startCategory, amountOfCards, roundDuration, penalty, steal, startTeam);
            fCallback.callback(action);
        });
    }

    public void onPenaltyGroupClicked( RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.no_penalty:
                penalty = PenaltyType.no_penalty;
                break;
            case  R.id.loss_points_penalty:
                penalty = PenaltyType.lose_points;
                break;
            case R.id.players_task_penalty:
                penalty = PenaltyType.players_task;
                break;
        }
    }

    public void onCardDeckButtonClick(View view) {
        // TODO нажали на кнопку выбрать деку
    }

    @Override
    public void onDestroyView() {

        // если выбрана случайная команда в спинере

        super.onDestroyView();
    }
}