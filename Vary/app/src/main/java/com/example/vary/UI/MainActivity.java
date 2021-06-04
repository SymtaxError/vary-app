package com.example.vary.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.util.Log;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.vary.Network.LoadStatus;
import com.example.vary.R;
import com.example.vary.Services.LocalService;
import com.example.vary.ViewModels.CardsViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements CallbackFragment, CallbackSettings {
    public static final String prefs = "settingsPrefs";
    private CardsViewModel viewModel;

    public LocalService mService;
    boolean mBound = false;

    public boolean allowStart = true;
    public static final String soundKey = "sound";
    public static final String checkUpdatesKey = "check_updates";
    boolean startGamePressed = false;

    public final String TAG = "MyLogger";
    SharedPreferences mPrefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
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

        viewModel = new ViewModelProvider(this).get(CardsViewModel.class);
//        Observer<List<CategoryModel>> observer = new Observer<List<CategoryModel>>() {
//            @Override
//            public void onChanged(List<CategoryModel> categoryModels) {
//                if (categoryModels.size() != 0) {
//                    String countText = "Loaded from " + categoryModels.size() + " categories";
//                }
//            }
//        };

//        viewModel.getCategories().observe(this, observer);
        Intent intent = new Intent(this, LocalService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        mPrefs = getPreferences(MODE_PRIVATE);
        editor = mPrefs.edit();

        readModel();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Observer<LoadStatus> observerLoadStatus = loadStatus -> {
//                Toast toast;
//                if (loadStatus.getError() != null && loadStatus.getNotification()) {
//                    toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_server_load) + loadStatus.getError(), Toast.LENGTH_LONG);
//                    toast.show();
//                }
            if (loadStatus.getError() != null && loadStatus.getNotification()) {
                LayoutInflater.from(getApplicationContext());
                View contextView = findViewById(R.id.context_view);
                Snackbar bar = Snackbar.make(contextView, R.string.no_cards_message, Snackbar.LENGTH_LONG);
                bar.setAction(R.string.download, v -> {
                    startGamePressed = false;
                    viewModel.getNewCategories();
                });
                allowStart = false;
                bar.show();
            }
            else {
                allowStart = true;
                if (startGamePressed) {
                    callback(GameActions.new_game_action);
                }
            }
        };

        viewModel
                .getLoadStatus()
                .observe(this, observerLoadStatus);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        saveModel();
        super.onSaveInstanceState(outState);
    }

    private void saveModel() {
        viewModel.saveState(editor);
        Log.v(TAG, "saved");
    }

    private void readModel() {
        viewModel.restoreState(mPrefs);
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
            case prepare_game:
                prepareGameProcess();
                break;
            case start_game_process:
                startGameProcess();
                break;
            case open_team_result:
                openTeamResult();
                break;
            case open_round_or_game_result:
                openRoundOrGameResult();
                break;
            case open_menu_and_save:
                saveModel();
            case open_menu:
                startMainFragment();
                break;
            default:
                break;
        }
    }


    void openGameSettings() {
        if (!Objects.requireNonNull(getSupportFragmentManager()
                .findFragmentById(R.id.container))
                .getClass()
                .equals(GameSettingsFragment.class)) {
            Fragment old_fragment = getSupportFragmentManager().findFragmentById(R.id.container);
            if (old_fragment != null) {
                getSupportFragmentManager().beginTransaction().remove(old_fragment).commit();
                getSupportFragmentManager().popBackStack();
            }
            GameSettingsFragment fragment = new GameSettingsFragment();
            fragment.setCallback(this);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
    }

    void startGameProcess() {

        if (!Objects.requireNonNull(getSupportFragmentManager()
                .findFragmentById(R.id.container))
                .getClass()
                .equals(OnGameFragment.class)) {
            OnGameFragment fragment = new OnGameFragment();
            fragment.setCallbackFunctions(this);
            fragment.setTimerService(mService);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
    }

    void prepareGameProcess() {
        if (!Objects.requireNonNull(getSupportFragmentManager()
                .findFragmentById(R.id.container))
                .getClass()
                .equals(PrepareGameFragment.class)) {
            PrepareGameFragment fragment = new PrepareGameFragment();
            fragment.setCallback(this);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
    }


    void continueGame() {
        if (!allowStart) {
            viewModel.getNewCategories();
            return;
        }
        viewModel.continueOldGame();
        callback(viewModel.getGameAction());
    }


    void startNewGame() {
        if (!allowStart) {
            startGamePressed = true;
            viewModel.getNewCategories();
            return;
        }
        if (viewModel.getAmountOfTeams() != 0)
            viewModel.removeTeams();
        viewModel.setNewGame(editor);
        if (!Objects.requireNonNull(getSupportFragmentManager()
                .findFragmentById(R.id.container))
                .getClass()
                .equals(SetTeamsFragment.class)) {
            SetTeamsFragment fragment = new SetTeamsFragment();
            fragment.setCallback(this);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
    }

    public void startMainFragment() {
        if (viewModel.getGameMode() == GameMode.end)
            viewModel.setNewGame(editor);
        if (!Objects.requireNonNull(getSupportFragmentManager()
                .findFragmentById(R.id.container))
                .getClass()
                .equals(StartFragment.class)) {
            replaceToStartFragment(getSupportFragmentManager());
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager manager = getSupportFragmentManager();
        Fragment activeFragment = manager.findFragmentById(R.id.container);
        assert activeFragment != null;
        Class<? extends Fragment> activeFragmentClass = activeFragment.getClass();
        saveModel();

        if (activeFragmentClass.equals(StartFragment.class))
            super.onBackPressed();
        else if (activeFragmentClass.equals(OnGameFragment.class))
        {
            OnGameFragment fragment = (OnGameFragment) activeFragment;
            if (!fragment.isPaused())
                fragment.toPause();
            else
                replaceToStartFragment(manager);
        }
        else
            replaceToStartFragment(manager);

    }

    private void replaceToStartFragment(FragmentManager manager) {
        StartFragment fragment = new StartFragment();
        fragment.setCallback(this);
        fragment.setWidth(getWidth());
        manager.beginTransaction().replace(R.id.container, fragment).commit();
    }

    void showRules() {
        if (!Objects.requireNonNull(getSupportFragmentManager()
                .findFragmentById(R.id.container))
                .getClass()
                .equals(GameRulesFragment.class)) {
            GameRulesFragment fragment = new GameRulesFragment();
            fragment.setFCallback(this);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
    }

    void openSettings() {
        if (!Objects.requireNonNull(getSupportFragmentManager()
                .findFragmentById(R.id.container))
                .getClass()
                .equals(SettingsFragment.class)) {
            SettingsFragment fragment = new SettingsFragment();
            fragment.setCallback(this);
            fragment.setFCallback(this);

            SharedPreferences sp = getSharedPreferences(prefs, MODE_PRIVATE);

            fragment.setSwitches(sp.getBoolean(soundKey, true),
                                 sp.getBoolean(checkUpdatesKey, true));

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
    }

    void openTeamResult() {
        if (!Objects.requireNonNull(getSupportFragmentManager()
                .findFragmentById(R.id.container))
                .getClass()
                .equals(ResultTeamFragment.class)) {
            ResultTeamFragment fragment = new ResultTeamFragment();
            fragment.setCallback(this);
            fragment.setViewModel(viewModel);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
    }

    void openRoundOrGameResult() { //TODO добавить бекстек
        if (!Objects.requireNonNull(getSupportFragmentManager()
                .findFragmentById(R.id.container))
                .getClass()
                .equals(ResultRoundFragment.class)) {
            ResultRoundFragment fragment = new ResultRoundFragment();
            fragment.setCallback(this);
            fragment.setViewModel(viewModel);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
    }

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocalService.LocalBinder binder = (LocalService.LocalBinder) service;
            mService = binder.getService();
            mService.setViewModel(viewModel);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        mBound = false;
    }

    @Override
    protected void onPause() {
        saveModel();
        super.onPause();
    }

    @Override
    public void callback(SettingActions type, boolean setting) {
        SharedPreferences.Editor editor = getSharedPreferences(prefs, MODE_PRIVATE).edit();

        switch (type) {
            case sound_setting:
                editor.putBoolean(soundKey, setting);
                setSound(setting);
                break;
            case check_updates_setting:
                // TODO udpates settings
                editor.putBoolean(checkUpdatesKey, setting);
            default:
                break;
        }
        editor.apply();
    }


    public void setSound(boolean setting) {
        AudioManager manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        manager.setStreamMute(AudioManager.STREAM_MUSIC, !setting);
    }
}