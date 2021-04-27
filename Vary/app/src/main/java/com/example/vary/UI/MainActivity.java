package com.example.vary.UI;

// TODO: дохимичить с полноэкранным режимом

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.vary.Database.DbManager;
import com.example.vary.Network.LoadStatus;
import com.example.vary.R;
import com.example.vary.ViewModels.CardsViewModel;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements CallbackFragment {
    public static final String prefs = "settingsPrefs";
    private static final int version = 0;
    private CardsViewModel viewModel;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        StartFragment fragment = (StartFragment) getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragment == null) {
            fragment = new StartFragment();
            fragment.setCallback(this);
            fragment.setWidth(getWidth());
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
        final DbManager manager = DbManager.getInstance(this);
//
        manager.getCount(countListener);

        //TODO delete, test sound
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.beep_short_on);
        mp.start();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Observer<LoadStatus> observerLoadStatus = new Observer<LoadStatus>() {
            @Override
            public void onChanged(LoadStatus loadStatus) {
                if (loadStatus.getError() != null) {
                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_server_load) + loadStatus.getError(), Toast.LENGTH_LONG );
                    toast.show();
                }
                else {

                    Toast toast = Toast.makeText(getApplicationContext(), "Loaded ", Toast.LENGTH_LONG );
                    toast.show();
                }
            }
        };

        viewModel = new ViewModelProvider(this).get(CardsViewModel.class);
        viewModel
                .getLoadStatus()
                .observe(this, observerLoadStatus);


    }

    public int getWidth() {
        DisplayMetrics displayMetrics = getApplicationContext()
                .getResources()
                .getDisplayMetrics();
        return (int) (displayMetrics.widthPixels / displayMetrics.density);
    }

    public void callback(GameActions type) {
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
            case open_game_settings:
                openGameSettings();
                break;
            case start_game_process:
                startGameProcess();
            default:
                break;
        }
    }

    void openGameSettings() {
        if (!Objects.requireNonNull(getSupportFragmentManager()
                .findFragmentById(R.id.container))
                .getClass()
                .equals(GameSettingsFragment.class)) {
            GameSettingsFragment fragment = new GameSettingsFragment();
            fragment.setCallback(this::callback);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    void startGameProcess() {
        if (!Objects.requireNonNull(getSupportFragmentManager()
                .findFragmentById(R.id.container))
                .getClass()
                .equals(OnGameFragment.class)) {
            OnGameFragment fragment = new OnGameFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
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
            viewModel.removeCommands();
        }
        if (!Objects.requireNonNull(getSupportFragmentManager()
                .findFragmentById(R.id.container))
                .getClass()
                .equals(SetCommandsFragment.class)) {
            SetCommandsFragment fragment = new SetCommandsFragment();
            fragment.setCallback(this::callback);
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
            fragment.setSharedPreferences(getSharedPreferences(prefs, MODE_PRIVATE));
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}