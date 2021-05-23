package com.example.vary.UI;

// TODO: дохимичить с полноэкранным режимом

import androidx.appcompat.app.AppCompatActivity;

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
import android.view.WindowManager;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.vary.Database.DbManager;
import com.example.vary.Models.CategoryModel;
import com.example.vary.Network.LoadStatus;
import com.example.vary.R;
import com.example.vary.Services.LocalService;
import com.example.vary.ViewModels.CardsViewModel;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements CallbackFragment, CallbackSettings {
    public static final String prefs = "settingsPrefs";
    private static final int version = 0;
    private CardsViewModel viewModel;

    public LocalService mService;
    boolean mBound = false;

    public static final String soundKey = "sound";
    public static final String checkUpdatesKey = "check_updates";
    public static final String pushKey = "push";

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
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.beep_short_on);
        mp.start();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Observer<LoadStatus> observerLoadStatus = new Observer<LoadStatus>() {
            @Override
            public void onChanged(LoadStatus loadStatus) {
                Toast toast;
                if (loadStatus.getError() != null) {
                    toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_server_load) + loadStatus.getError(), Toast.LENGTH_LONG);
                }
                else {
                    toast = Toast.makeText(getApplicationContext(), "Loaded ", Toast.LENGTH_LONG);
                }
                toast.show();
            }
        };

//        viewModel = new ViewModelProvider(this).get(CardsViewModel.class);
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
            case prepare_game:
                prepareGameProcess();
                break;
            case create_game_process:
                createGameProcess();
            case start_game_process:
                startGameProcess();
                break;
            case open_round_result:
                openTeamRound();
                break;
            default:
                break;
        }
    }

    private void createGameProcess() {
        if (!Objects.requireNonNull(getSupportFragmentManager()
                .findFragmentById(R.id.container))
                .getClass()
                .equals(OnGameFragment.class)) {
//            getSupportFragmentManager().popBackStack(); //TODO вернуть
////            getSupportFragmentManager().popBackStack(); //TODO вернуть
////            getSupportFragmentManager().popBackStack(); //TODO вернуть
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
        if (!Objects.requireNonNull(getSupportFragmentManager()
                .findFragmentById(R.id.container))
                .getClass()
                .equals(SetTeamsFragment.class)) {
            SetTeamsFragment fragment = new SetTeamsFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    void startNewGame() {
        if (viewModel.getSize() != 0) {
            viewModel.removeTeams();
        }
        if (!Objects.requireNonNull(getSupportFragmentManager()
                .findFragmentById(R.id.container))
                .getClass()
                .equals(SetTeamsFragment.class)) {
            SetTeamsFragment fragment = new SetTeamsFragment();
            fragment.setCallback(this);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (Objects.requireNonNull(getSupportFragmentManager()
                .findFragmentById(R.id.container))
                .getClass()
                .equals(StartFragment.class)) {
            super.onBackPressed(); //TODO так не должно быть! вообще может перенести во фрагмент?
            StartFragment fragment = (StartFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.container);
            fragment.checkContinueButtonVisibility(viewModel.getSize());
        } else if (Objects.requireNonNull(getSupportFragmentManager()
                .findFragmentById(R.id.container))
                .getClass()
                .equals(OnGameFragment.class)) {
            OnGameFragment fragment = (OnGameFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.container);
            if (!fragment.isPaused()) {
                fragment.toPause();
            } else {
//                while (!Objects.requireNonNull(getSupportFragmentManager()
//                        .findFragmentById(R.id.container))
//                        .getClass()
//                        .equals(FragmentActivity.class))
//                    getSupportFragmentManager().popBackStack();
                Fragment old_fragment = getSupportFragmentManager().findFragmentById(R.id.container);
                getSupportFragmentManager().beginTransaction().remove(old_fragment).commit();
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    void showRules() {
        // Запуск фрагмента? всплывающего окна? с правилами игры
    }

    void openSettings() {
        if (!Objects.requireNonNull(getSupportFragmentManager()
                .findFragmentById(R.id.container))
                .getClass()
                .equals(SetTeamsFragment.class)) {
            SettingsFragment fragment = new SettingsFragment();
            fragment.setCallback(this);

            SharedPreferences sp = this.getSharedPreferences(prefs, MODE_PRIVATE);

            fragment.setSwitches(sp.getBoolean(soundKey, true),
                                 sp.getBoolean(pushKey, true),
                                 sp.getBoolean(checkUpdatesKey, true));

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    void openTeamRound() {
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
    public void callback(SettingActions type, boolean setting) {
        SharedPreferences.Editor editor = this.getSharedPreferences(prefs, MODE_PRIVATE).edit();

        switch (type) {
            case sound_setting:
                editor.putBoolean(soundKey, setting);
                setSound(setting);
                break;
            case push_setting:
                // TODO push settings
                editor.putBoolean(pushKey, setting);
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
        AudioManager manager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        manager.setStreamMute(AudioManager.STREAM_NOTIFICATION, !setting);
    }

}