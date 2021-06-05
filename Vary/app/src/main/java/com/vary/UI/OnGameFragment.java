package com.vary.UI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.vary.Models.CardModel;
import com.vary.Models.CurrentGameModel;
import com.vary.R;
import com.vary.Services.LocalService;
import com.vary.ViewModels.CardsViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("SameParameterValue")
public class OnGameFragment extends Fragment implements CardCallback {
    //TODO: анимации
    //TODO: динамический размер текста
    private final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.5F);
    private final AlphaAnimation swiped = new AlphaAnimation(1F, 0.6F);
    private int dropCardValue;
    TextView roundScoreView;
    TextView cardText;
    TextView timeLeft;
    private int roundScore;
    private CardsViewModel viewModel;
    private FrameLayout card;
    private RelativeLayout pause;
    private RelativeLayout preview;
    private RelativeLayout playersTask;
    private boolean paused = false;
    private boolean onPlayersTask = false;
    private boolean cardTextSetted = false;
    private boolean isLastCard = false;
    private boolean ended = false;
    private float dY;
    private float startY;
    private Boolean swipeable = true;
    private SpringAnimation yAnimation;
    private GameMode gameMode;
    private View topView, botView;
    float volume;
    int idSwipeUp, idSwipeDown, idEnding1, idEnding2, idEnding3, idEnded, idPauseIn, idPauseOut;
    SoundPool soundPool;


    private CallbackFragment callbackFunctions;
    private LocalService timerService;
    private boolean endNotPlayed = true;
    Snackbar cardsEndedSnackBar;

    public OnGameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private int dp() {
        return dp(200);
    }

    private int dp(int x) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return Math.round(x * metrics.density);
    }

    public void setCallbackFunctions(CallbackFragment callbackFunctions) {
        this.callbackFunctions = callbackFunctions;
    }

    public void setTimerService(LocalService service) {
        timerService = service;
    }

    @SuppressLint("SetTextI18n") //TODO refactor
    private void swipeUp() {
        Log.d("Sound", "state = " + viewModel.getSoundState());
        if (viewModel.getSoundState()) {
            if (viewModel.getLowerVolume())
                soundPool.play(idSwipeUp, volume / 3, volume / 3, 1, 0, 1f);
            else
                soundPool.play(idSwipeUp, volume, volume, 1, 0, 1f);
        }
        swipeable = false;
        topView.startAnimation(swiped);
        if (!isLastCard) {
            viewModel.answerCard(0);
            cardText.setText(viewModel.getCard());
            roundScoreView.setText(String.valueOf(++roundScore));
        } else {
            if (viewModel.getSteal() && getContext() != null) {
                CharSequence[] teamsNames = viewModel.getTeamsNamesChar(getContext());
                new MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialog)
                        .setTitle(getContext().getString(R.string.pick_team))
                        .setItems(teamsNames, (dialog, which) -> {
                            if (which < viewModel.getAmountOfTeams())
                                viewModel.answerCard(which);
                            else {
                                viewModel.declineCard();
                                viewModel.setAnsweredTeam(-1);
                            }
                            endFragment(); //TODO
                        }).setCancelable(false).show();
            }
        }
    }

    @SuppressLint("SetTextI18n") //TODO refactor
    private void swipeDown() {
        if (viewModel.getSoundState()) {
            if (viewModel.getLowerVolume())
                soundPool.play(idSwipeDown, volume / 3, volume / 3, 1, 0, 1f);
            else
                soundPool.play(idSwipeDown, volume, volume, 1, 0, 1f);
        }
        PenaltyType penalty = viewModel.getPenalty();
        if (penalty == PenaltyType.lose_points)
            roundScoreView.setText(String.valueOf(--roundScore));
        else if (penalty == PenaltyType.players_task)
            setPlayersTask(true);
        viewModel.declineCard();
        if (isLastCard)
            viewModel.setAnsweredTeam(-1);
        cardText.setText(viewModel.getCard());
        botView.startAnimation(swiped);
        swipeable = false;
        if (isLastCard && !onPlayersTask)
            endFragment(); //TODO
    }


    private String getGameModeStr() {
        String out;
        if (gameMode == GameMode.explain_mode)
            out = getResources().getString(R.string.explain_mode_str);
        else if (gameMode == GameMode.gesture_mode)
            out = getResources().getString(R.string.gesture_mode_str);
        else
            out = getResources().getString(R.string.one_word_mode_str);
        return out;
    }

    private String getGameModeStrPreview() {
        String out;
        if (gameMode == GameMode.explain_mode)
            out = getResources().getString(R.string.explain_mode_str_preview);
        else if (gameMode == GameMode.gesture_mode)
            out = getResources().getString(R.string.gesture_mode_str_preview);
        else
            out = getResources().getString(R.string.one_word_mode_str_preview);
        return out;
    }

    private int getGameModeImageId() {
        int out;
        if (gameMode == GameMode.explain_mode)
            out = R.drawable.ic_explain_mode;
        else if (gameMode == GameMode.gesture_mode)
            out = R.drawable.ic_gesture_mode;
        else
            out = R.drawable.ic_one_word_mode;
        return out;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_on_game, container, false);
        setViewModel();
        String snackBarText = getResources().getString(R.string.pool_ended_message);
        cardsEndedSnackBar = Snackbar.make(container, snackBarText, Snackbar.LENGTH_LONG);
        viewModel.setGameAction(GameActions.start_game_process);
        swiped.setRepeatCount(2);
        swiped.setDuration(100);
        topView = view.findViewById(R.id.game_button_limit_top);
        botView = view.findViewById(R.id.game_button_limit_bot);
        gameMode = viewModel.getGameMode();
        TextView gameModeView = view.findViewById(R.id.game_mode);
        gameModeView.setText(getGameModeStr());
        TextView teamName = view.findViewById(R.id.team_name_on_game);
        timeLeft = view.findViewById(R.id.time_left);
        teamName.setText(viewModel.getCurTeamName(0));
        card = new FrameLayout(view.getContext());
        cardText = new TextView(card.getContext());
        ImageView cardMount = new ImageView(card.getContext());
        cardMount.setBackgroundResource(R.drawable.game_shape);
        card.addView(cardMount);
        FrameLayout.LayoutParams cardTextLayout = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        cardText.setGravity(Gravity.CENTER);
        cardText.setLayoutParams(cardTextLayout);
        cardText.setTextColor(getResources().getColor(R.color.white));
        cardText.setTypeface(Typeface.DEFAULT_BOLD);
        cardText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        card.addView(cardText);
        FrameLayout root = view.findViewById(R.id.card_root);
        roundScoreView = view.findViewById(R.id.round_score);
        roundScore = viewModel.getRoundPoints();
        roundScoreView.setText(String.valueOf(roundScore));
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(dp(), dp());
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                fillPreview(view);
                root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                layoutParams.leftMargin = (root.getWidth() - layoutParams.width) / 2;
                layoutParams.topMargin = (root.getHeight() - layoutParams.height) / 2;
                card.setLayoutParams(layoutParams);
                root.addView(card);
                dropCardValue = root.getHeight() / 4;
                yAnimation = new SpringAnimation(card, SpringAnimation.Y, layoutParams.topMargin);
//                upAnimation = new SpringAnimation(card, SpringAnimation.Y, layoutParams.topMargin);
//                upAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
//                    @Override
//                    public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
////                        yAnimation.start();
//                        card.setY(layoutParams.topMargin);
//                    }
//                });
//                swipeUpAnimation = new SpringAnimation(card, SpringAnimation.Y, )
            }
        });

        card.setOnTouchListener((v, event) -> {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    dY = v.getY() - event.getRawY();
                    startY = event.getRawY();
                    yAnimation.cancel();
                    swipeable = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (!swipeable)
                        return true;
                    if (event.getRawY() - startY < -dropCardValue) {
                        swipeUp();
                        yAnimation.start();
                    } else if (event.getRawY() - startY > dropCardValue) {
                        swipeDown();
                        yAnimation.start();
                    } else {
                        card.animate()
                                .y(event.getRawY() + dY)
                                .setDuration(0)
                                .start();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    yAnimation.start();
                    break;
            }
            root.invalidate();
            return true;
        });
        pause = view.findViewById(R.id.pause);
        paused = false;
        pause.setOnClickListener(v -> {
            v.startAnimation(buttonClick);
            endPause();
        });
        playersTask = view.findViewById(R.id.players_task);
        playersTask.setOnClickListener(v -> {
            v.startAnimation(buttonClick);
            setPlayersTask(false);
        });
        preview = view.findViewById(R.id.preview);
        setPreview(true);
        preview.setOnClickListener(v -> {
            v.startAnimation(buttonClick);
            endPreview();
        });

        if (getContext() != null)
            setSounds(requireContext());

//        AudioManager manager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (!manager.isStreamMute(AudioManager.STREAM_MUSIC)) {
//                setSounds(getContext());
//            } else
//
//        } else
//            setSounds(getContext());

        return view;
    }

    private void setSounds(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        float actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = actualVolume / maxVolume;
        AudioAttributes audioAttrib = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder().setAudioAttributes(audioAttrib).setMaxStreams(1).build();
        idSwipeDown = soundPool.load(getContext(), R.raw.swipe_down, 1);
        idSwipeUp = soundPool.load(getContext(), R.raw.swipe_up, 1);
        idEnding1 = soundPool.load(getContext(), R.raw.sound_end_1, 1);
        idEnding2 = soundPool.load(getContext(), R.raw.sound_end_2, 1);
        idEnding3 = soundPool.load(getContext(), R.raw.sound_end_3, 1);
        idEnded = soundPool.load(getContext(), R.raw.sound_end, 1);
        idPauseIn = soundPool.load(getContext(), R.raw.pause_in, 1);
        idPauseOut = soundPool.load(getContext(), R.raw.pause_out, 1);

    }


    private void fillPreview(View view) {
        ImageView previewImage = view.findViewById(R.id.preview_ic);
        TextView previewTitle = view.findViewById(R.id.preview_title);
        TextView previewContent = view.findViewById(R.id.preview_content);
        previewTitle.setText(getGameModeStrPreview());
        previewImage.setImageResource(getGameModeImageId());
        previewContent.setText(getGameModeStrPreviewContent());
    }

    private String getGameModeStrPreviewContent() {
        return getResources().getString(R.string.preview_text);
    }

    boolean isPaused() {
        return paused;
    }

    public void toPause() {
        setPause(true);
    }

    public void endPause() {
        setPause(false);
    }

    public void endPreview() {
        setPreview(false);
        timerService.resumeTask();
    }

    private void setPreview(boolean newPreviewValue) {
        if (newPreviewValue) {
            card.setVisibility(View.INVISIBLE);
            playersTask.setVisibility(View.INVISIBLE);
            preview.setVisibility(View.VISIBLE);
        } else {
            if (!cardTextSetted) {
                try {
                    cardText.setText(viewModel.getCard());
                    cardTextSetted = true;
                    card.setVisibility(View.VISIBLE);
                    playersTask.setVisibility(View.INVISIBLE);
                    preview.setVisibility(View.INVISIBLE);
                } catch (Exception e) {
                    Log.println(Log.ERROR, "Database", "Not loaded");
                }
            } else {
                card.setVisibility(View.VISIBLE);
                preview.setVisibility(View.INVISIBLE);
                playersTask.setVisibility(View.INVISIBLE);
            }
        }
    }


    private void setPlayersTask(boolean newPlayersTaskValue) {
        onPlayersTask = newPlayersTaskValue;
        if (newPlayersTaskValue) {
            timerService.pauseTask();
            card.setVisibility(View.INVISIBLE);
            pause.setVisibility(View.INVISIBLE);
            playersTask.setVisibility(View.VISIBLE);
        } else if (ended) {
            endFragment();
        } else {
            timerService.resumeTask();
            card.setVisibility(View.VISIBLE);
            pause.setVisibility(View.INVISIBLE);
            playersTask.setVisibility(View.INVISIBLE);
        }
    }

    private void setPause(boolean newPauseValue) {
        if (newPauseValue) {
            if (!paused && viewModel.getSoundState()) {
                if (viewModel.getLowerVolume())
                    soundPool.play(idPauseIn, volume / 3, volume / 3, 1, 0, 1f);
                else
                    soundPool.play(idPauseIn, volume, volume, 1, 0, 1f);
            }
            setPreview(false);
            timerService.pauseTask();
            pause.setVisibility(View.VISIBLE);
            card.setVisibility(View.INVISIBLE);
        } else {
            if (viewModel.getSoundState()) {
                if (viewModel.getLowerVolume())
                    soundPool.play(idPauseOut, volume / 3, volume / 3, 1, 0, 1f);
                else
                    soundPool.play(idPauseOut, volume, volume, 1, 0, 1f);
            }
            timerService.resumeTask();
            card.setVisibility(View.VISIBLE);
            pause.setVisibility(View.INVISIBLE);
        }
        playersTask.setVisibility(View.INVISIBLE);
        paused = newPauseValue;
    }

    protected void setViewModel() {
        Observer<List<CardModel>> observer = cardModels -> cardText.setText(viewModel.getCard());

        Observer<Integer> observerTimer = timerCount -> {
            if (timerCount < 5 && viewModel.getSoundState())
                playEndSound(timerCount);
            if (timerCount < 0) {
                viewModel.setRoundTimeLeft(viewModel.getRoundDuration());
                return; //TODO убрать костыль
            }

            if (timerCount == 0) {
                timeEnded();
            }
            timeLeft.setText(String.format("%02d:%02d", timerCount / 60, timerCount % 60));
            Log.d("Timer is ticking...", timerCount.toString());
        };

        Observer<CurrentGameModel> observerCurrentGame = gameModel -> {
            timerService.runTask(gameModel.getRoundTimeLeft());
            timerService.pauseTask();
            Log.d("OnGame", "duration = " + gameModel.getRoundDuration() + ", time left = " + gameModel.getRoundTimeLeft());
            //TODO вписать изменение gamemodel
        };


        viewModel = new ViewModelProvider(requireActivity()).get(CardsViewModel.class);
        viewModel
                .getCards()
                .observe(getViewLifecycleOwner(), observer);
        viewModel
                .getTimerCount()
                .observe(getViewLifecycleOwner(), observerTimer);
        viewModel
                .getGameModel()
                .observe(getViewLifecycleOwner(), observerCurrentGame);
        viewModel.setCardsCallback(this);
    }

    private void playEndSound(int timerCount) {
        float vol = (viewModel.getLowerVolume()) ? volume / 3 : volume;
        if (timerCount == 3)
            soundPool.play(idEnding1, vol, vol, 1, 0, 1f);
        else if (timerCount == 2)
            soundPool.play(idEnding2, vol, vol, 1, 0, 1f);
        else if (timerCount == 1)
            soundPool.play(idEnding3, vol, vol, 1, 0, 1f);
        else if (timerCount == 0 && endNotPlayed) {
            soundPool.play(idEnded, vol, vol, 1, 0, 1f);
            endNotPlayed = false;
        }

    }

    @Override
    public void onPause() {
        viewModel.setCurrentRoundPoints(roundScore);
        setPause(true);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        timerService.stopTask();
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        viewModel.setCurrentRoundPoints(roundScore);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void callback() {
        cardsEndedSnackBar.show();
        paused = true;
        endFragment();
    }

    private void timeEnded() {
        viewModel.setTimerCount(0);
        ended = true;
        timerService.stopTask();
        if (viewModel.getSteal()) {
            isLastCard = true;
        } else {
            endFragment();
        }
    }

    private void endFragment() {
        if (!onPlayersTask) {
            if (!isLastCard)
                viewModel.setTimerCount(-2);
            else
                viewModel.setTimerCount(-1);
            callbackFunctions.callback(GameActions.open_team_result);
        }
    }
}