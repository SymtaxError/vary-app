package com.example.vary.UI;

// TODO: дохимичить с полноэкранным режимом

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.vary.Database.DbManager;
import com.example.vary.Models.CategoryModel;
import com.example.vary.Models.CurrentGameModel;
import com.example.vary.Network.LoadStatus;
import com.example.vary.R;
import com.example.vary.Services.LocalService;
import com.example.vary.ViewModels.CardsViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements CallbackFragment, CallbackSettings {
    public static final String prefs = "settingsPrefs";
    private static final int version = 0;
    private CardsViewModel viewModel;

    public LocalService mService;
    boolean mBound = false;

    public boolean allowStart = true;
    public static final String soundKey = "sound";
    public static final String checkUpdatesKey = "check_updates";
    public static final String pushKey = "push";
    boolean startGamePressed = false;

    public final String TAG = "MyLogger";
    SharedPreferences mPrefs;
    SharedPreferences.Editor editor;

    private final DbManager.CountListener countListener = new DbManager.CountListener() {
        @Override
        public void onGetCount(final int cardCount, final int catCount) {
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
//        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

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
        Observer<List<CategoryModel>> observer = new Observer<List<CategoryModel>>() {
            @Override
            public void onChanged(List<CategoryModel> categoryModels) {
                if (categoryModels.size() != 0) {
                    String countText = "Loaded from " + categoryModels.size() + " categories";
                    Toast.makeText(getApplicationContext(), countText, Toast.LENGTH_LONG).show();
                }
            }
        };

        viewModel.getCategories().observe(this, observer);
//        final DbManager manager = DbManager.getInstance(this);
//        manager.getCount(countListener);
        // binding service to activity
        Intent intent = new Intent(this, LocalService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        //TODO delete, test sound
//        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.beep_short_on);
//        mp.start();

        mPrefs = getPreferences(MODE_PRIVATE);
        editor = mPrefs.edit();

        readModel();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Observer<LoadStatus> observerLoadStatus = new Observer<LoadStatus>() {
            @Override
            public void onChanged(LoadStatus loadStatus) {
                Toast toast;
//                if (loadStatus.getError() != null && loadStatus.getNotification()) {
//                    toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_server_load) + loadStatus.getError(), Toast.LENGTH_LONG);
//                    toast.show();
//                }
                if (loadStatus.getError() != null && loadStatus.getNotification()) {
                    LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
//                    View promView = inflater.inflate(R.layout.load_confirm, null);
//
//                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
//                    alertDialogBuilder.setView(promView);
//
//                    String add = getResources().getString(R.string.yes);
//                    String cancel = getResources().getString(R.string.no);
//                    alertDialogBuilder
//                            .setCancelable(false)
//                            .setPositiveButton(add, (dialog, which) -> {
//                                viewModel.getNewCategories();
//                            });
//                    AlertDialog alertDialog = alertDialogBuilder.create();
//                    alertDialog.show();
                    View contextView = findViewById(R.id.context_view);
                    Snackbar bar = Snackbar.make(contextView, R.string.no_cards_message, Snackbar.LENGTH_LONG);
                    bar.setAction(R.string.download, v -> {
                        startGamePressed = false;
                        viewModel.getNewCategories();
                    });
                    allowStart = false;
                    bar.show();
//                    bar.dismiss();
                }
                else {
//                    if (bar.isShown()) {
                    allowStart = true;
                    if (startGamePressed) {
                        callback(GameActions.new_game_action);
                    }
                }
            }
        };

//        viewModel = new ViewModelProvider(this).get(CardsViewModel.class);
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
//                    .addToBackStack(null)
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
//            getSupportFragmentManager().popBackStack(); //TODO вернуть
//            getSupportFragmentManager().popBackStack(); //TODO вернуть
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
//                    .addToBackStack(null)
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
//                    .addToBackStack(null)
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
//        if (viewModel.getRoundTimeLeft() > 0) {
//            createGameProcess();
//        } else {
//            openRoundOrGameResult();
//        }
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
//                    .addToBackStack(null)
                    .commit();
        }
    }

    public void startMainFragment() {
        viewModel.setNewGame(editor);
        if (!Objects.requireNonNull(getSupportFragmentManager()
                .findFragmentById(R.id.container))
                .getClass()
                .equals(StartFragment.class)) {
            replaceToStartFragment(getSupportFragmentManager());
//            StartFragment fragment = new StartFragment();
//            fragment.setCallback(this);
//            FragmentManager manager = getSupportFragmentManager();
//            if (manager.getBackStackEntryCount() > 0) {
//                FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
//                manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
//            }
//            manager
//                    .beginTransaction()
//                    .replace(R.id.container, fragment)
////                    .addToBackStack(null)
//                    .commit();
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
//
//        if (Objects.requireNonNull(getSupportFragmentManager()
//                .findFragmentById(R.id.container))
//                .getClass()
//                .equals(StartFragment.class)) {
//            super.onBackPressed();
////            StartFragment fragment = (StartFragment) getSupportFragmentManager()
////                    .findFragmentById(R.id.container);
////            getSupportFragmentManager().beginTransaction().replace(R.id.container, fr)
//        } else if (Objects.requireNonNull(getSupportFragmentManager()
//                .findFragmentById(R.id.container))
//                .getClass()
//                .equals(OnGameFragment.class)) {
//            OnGameFragment fragment = (OnGameFragment) getSupportFragmentManager()
//                    .findFragmentById(R.id.container);
//            if (!fragment.isPaused()) {
//                fragment.toPause();
//            } else {
////                while (!Objects.requireNonNull(getSupportFragmentManager()
////                        .findFragmentById(R.id.container))
////                        .getClass()
////                        .equals(FragmentActivity.class))
////                    getSupportFragmentManager().popBackStack();
//                Fragment old_fragment = getSupportFragmentManager().findFragmentById(R.id.container);
//                getSupportFragmentManager().beginTransaction().remove(old_fragment).commit();
//                saveModel();
//                super.onBackPressed();
//            }
////        } else if (Objects.requireNonNull(getSupportFragmentManager()
////                .findFragmentById(R.id.container))
////                .getClass()
////                .equals(ResultRoundFragment.class)) {
////            ResultRoundFragment fragment = (ResultRoundFragment) getSupportFragmentManager()
////                    .findFragmentById(R.id.container);
////            Fragment old_fragment = getSupportFragmentManager().findFragmentById(R.id.container);
////            getSupportFragmentManager().beginTransaction().remove(old_fragment).commit();
////            super.onBackPressed();
//        } else {
//            saveModel();
//            super.onBackPressed();
//        }
//
//

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

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
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

            SharedPreferences sp = getSharedPreferences(prefs, MODE_PRIVATE);

            fragment.setSwitches(sp.getBoolean(soundKey, true),
                                 sp.getBoolean(checkUpdatesKey, true));

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
//                    .addToBackStack(null)
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
//                    .addToBackStack(null)
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
//        unbindService(connection);
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
        manager.setStreamMute(AudioManager.STREAM_NOTIFICATION, !setting);
    }
}