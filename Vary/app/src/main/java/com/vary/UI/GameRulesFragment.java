package com.vary.UI;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.vary.R;



public class GameRulesFragment extends Fragment {
    CallbackFragment fCallback;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_game_rules, container, false);

        Button buttonBackToMain = view.findViewById(R.id.back_to_main_menu_button_rules);
        buttonBackToMain.setOnClickListener(v -> fCallback.callback(GameActions.open_menu));

        return view;
    }

    void setFCallback(CallbackFragment callback) {
        fCallback = callback;
    }

}