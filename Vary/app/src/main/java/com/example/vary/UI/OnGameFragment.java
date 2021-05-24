package com.example.vary.UI;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
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
import android.widget.Toast;

import com.example.vary.Models.CardModel;
import com.example.vary.Models.CurrentGameModel;
import com.example.vary.R;
import com.example.vary.Services.LocalService;
import com.example.vary.ViewModels.CardsViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OnGameFragment extends Fragment implements CardCallback {
    //TODO: анимации
    //TODO: динамический размер текста
    private final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.5F);
    private AlphaAnimation swiped = new AlphaAnimation(1F, 0.6F);
    private int _yDelta;
    private int dropCardValue;
    TextView roundScoreView;
    TextView cardText;
    TextView timeLeft;
    private int roundScore;
    private CardsViewModel viewModel;
    private int category;
    private FrameLayout card;
    private RelativeLayout pause;
    private boolean paused = false;
    private boolean previewed;
    private RelativeLayout preview;
    private boolean cardTextSetted = false;
    private float dY;
    private float startY;
    private Boolean swipeable = true;
    private SpringAnimation yAnimation;
    private GameMode gameMode;
    private View topView, botView;



    private CallbackFragment callbackFunctions;
    private LocalService timerService;

    public OnGameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private int dp(int x) {
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        return Math.round(x * metrics.density);
    }

    public void setCallbackFunctions(CallbackFragment callbackFunctions) {
        this.callbackFunctions = callbackFunctions;
    }

    public void setTimerService(LocalService service) {
        timerService = service;
    }

    @SuppressLint("SetTextI18n") //TODO refactor
    private void swipeUp(View v) {
//        cardText.setText("CARD "+Math.abs(new Random().nextInt()%100));
        viewModel.answerCard();
        cardText.setText(viewModel.getCard());
        roundScoreView.setText(String.valueOf(++roundScore));
        swipeable = false;
        topView.startAnimation(swiped);
    }

    @SuppressLint("SetTextI18n") //TODO refactor
    private void swipeDown(View v) {
        viewModel.declineCard();
        cardText.setText(viewModel.getCard());
        roundScoreView.setText(String.valueOf(--roundScore));
        botView.startAnimation(swiped);
        swipeable = false;
    }


    private String getGameModeStr()
    {
        String out;
        if (gameMode == GameMode.explain_mode)
            out = getContext().getResources().getString(R.string.explain_mode_str);
        else if (gameMode == GameMode.gesture_mode)
            out = getContext().getResources().getString(R.string.gesture_mode_str);
        else
            out = getContext().getResources().getString(R.string.one_word_mode_str);
        return out;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_on_game, container, false);
        setViewModel();
        viewModel.setGameAction(GameActions.create_game_process);
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
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(dp(200), dp(200));
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                layoutParams.leftMargin = (root.getWidth() - layoutParams.width) / 2;
                layoutParams.topMargin = (root.getHeight() - layoutParams.height) / 2;
                card.setLayoutParams(layoutParams);
                root.addView(card);
                dropCardValue = root.getHeight()/4;
                yAnimation = new SpringAnimation(card, SpringAnimation.Y, root.getY()+layoutParams.height/2);
            }
        });

        card.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
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
                        if (event.getRawY()-startY < -dropCardValue) {
                            swipeUp(v);
                        } else if (event.getRawY()-startY > dropCardValue) {
                            swipeDown(v);
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
                ;
                root.invalidate();
                return true; }});
        pause = view.findViewById(R.id.pause);
        paused = false;
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                endPause();
            }
        });
        preview = view.findViewById(R.id.preview);
        setPreview(true);
        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                endPreview();
            }
        });
        return view;
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

    boolean isPreviewed() {
        return previewed;
    }

    public void toPreview() {
        setPreview(true);
    }

    public void endPreview() {
        setPreview(false);
        timerService.resumeTask();
    }

    private void setPreview(boolean newPreviewValue) {
        if (newPreviewValue) {
            card.setVisibility(View.INVISIBLE);
            preview.setVisibility(View.VISIBLE);
        }
        else {
            if (!cardTextSetted) {
                try {
                    cardText.setText(viewModel.getCard());
                    cardTextSetted = true;
                    card.setVisibility(View.VISIBLE);
                    preview.setVisibility(View.INVISIBLE);
                    previewed = false;
                } catch (Exception e){
                    Log.println(Log.ERROR, "Database", "Not loaded");
                }
            }
            else {
                card.setVisibility(View.VISIBLE);
                preview.setVisibility(View.INVISIBLE);
                previewed = false;
            }
        }
    }


    private void setPause(boolean newPauseValue) {
        if (newPauseValue) {
            timerService.pauseTask();
            setPreview(false);
            card.setVisibility(View.INVISIBLE);
            pause.setVisibility(View.VISIBLE);
        }
        else {
            timerService.resumeTask();
            card.setVisibility(View.VISIBLE);
            pause.setVisibility(View.INVISIBLE);
        }
        paused = newPauseValue;
    }

    protected void setViewModel() {
        Observer<List<CardModel>> observer = new Observer<List<CardModel>>() {
            @Override
            public void onChanged(List<CardModel> cardModels) {
            }
        };

        Observer<Integer> observerTimer = new Observer<Integer>() {
            @Override
            public void onChanged(Integer timerCount) {
                if (timerCount == -1) {
                    viewModel.setRoundTimeLeft(viewModel.getRoundDuration());
                    return; //TODO убрать костыль
                }
                if (timerCount == 0) {
                    viewModel.setTimerCount(-1);
                    callbackFunctions.callback(GameActions.open_team_result);
                }
                timeLeft.setText(timerCount.toString());
                Log.d("Timer is ticking...", timerCount.toString());
            }
        };

        Observer<CurrentGameModel> observerCurrentGame = new Observer<CurrentGameModel>() {
            @Override
            public void onChanged(CurrentGameModel gameModel) {
                timerService.runTask(gameModel.getRoundTimeLeft());
                timerService.pauseTask();
                Log.d("OnGame", "duration = "+gameModel.getRoundDuration() + ", time left = "+gameModel.getRoundTimeLeft());
                //TODO вписать изменение gamemodel
            }
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

    @Override
    public void onPause() {
        viewModel.setCurrentRoundPoints(roundScore);
        setPause(true);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        viewModel.setCurrentRoundPoints(roundScore);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void callback() {
        Toast.makeText(getContext(), "Карты закончились", Toast.LENGTH_SHORT).show();
//        viewModel.setRoundTimeLeft(0);
        callbackFunctions.callback(GameActions.open_team_result);
        //TODO переход
    }
}