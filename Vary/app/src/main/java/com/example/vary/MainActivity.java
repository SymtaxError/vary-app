package com.example.vary;

// TODO: дохимичить с полноэкранным режимом

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private final DbManager.CountListener countListener = new DbManager.CountListener() {
        @Override
        public void onGetCount (final int cardCount, final int catCount) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
    //                    showStringList(allItems);
                    String countText = "Loaded " + cardCount + " cards in " + catCount + " categories";
                    Toast.makeText(getApplicationContext(), countText, Toast.LENGTH_LONG).show();
                }
            });
        }
    };
    private CardsViewModel viewModel;
    private int version = 0;
    private final String opened_key = "opened";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restorePreferences();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        StartFragment fragment = (StartFragment) getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragment == null) {
            fragment = new StartFragment();
            fragment.setCallback(this::callback);
            fragment.setWidth(getWidth());
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
        final DbManager manager = DbManager.getInstance(this);
//
        manager.getCount(countListener);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Observer<List<CommandModel>> observer = new Observer<List<CommandModel>>() {
            @Override
            public void onChanged(List<CommandModel> commandModels) {
            }
        };

        viewModel = new ViewModelProvider(this).get(CardsViewModel.class);
        viewModel
                .getCommands()
                .observe(this, observer);


    }

    public int getWidth() {
        DisplayMetrics displayMetrics = getApplicationContext()
                .getResources()
                .getDisplayMetrics();
        return (int) (displayMetrics.widthPixels / displayMetrics.density);
    }

    void callback(GameActions type) {
        switch (type) {
            case new_game_action:
                startNewGame();
                break;
            case open_settings:
                openSettings();
                break;
            case continue_game_action:
                continueGame();
                break;
            case open_rules:
                showRules();
                break;
            default:
                break;
        }
    }

    void restorePreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(
                getResources().getString(R.string.settings_file),
                Context.MODE_PRIVATE);
        boolean opened = sharedPreferences.getBoolean(opened_key, false);
        if (!opened) {
            Editor editor = sharedPreferences.edit();
            editor.putBoolean(opened_key, true);
            editor.commit();

            Log.d("prefs", "first check");
        }
        else {
            Log.d("prefs", "already visited");
        }

    }

    void continueGame() {
        if (!Objects.requireNonNull(getSupportFragmentManager()
                .findFragmentById(R.id.container))
                .getClass()
                .equals(SetCommandsFragment.class)) {
            SetCommandsFragment fragment = new SetCommandsFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    void startNewGame() {
        if (viewModel.getSize() != 0) {
            CommandsRepo
                    .getInstance()
                    .removeCommands();
        }
        if (!Objects.requireNonNull(getSupportFragmentManager()
                .findFragmentById(R.id.container))
                .getClass()
                .equals(SetCommandsFragment.class)) {
            SetCommandsFragment fragment = new SetCommandsFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (Objects.requireNonNull(getSupportFragmentManager()
                .findFragmentById(R.id.container))
                .getClass()
                .equals(StartFragment.class)) {
            StartFragment fragment = (StartFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.container);
            fragment.checkContinueButtonVisibility(viewModel.getSize());
        }
    }

    void showRules() {
        // Запуск фрагмента? всплывающего окна? с правилами игры
    }

    void openSettings() {
        if (!Objects.requireNonNull(getSupportFragmentManager()
                .findFragmentById(R.id.container))
                .getClass()
                .equals(SetCommandsFragment.class)) {
            SettingsFragment fragment = new SettingsFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}