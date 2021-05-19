package com.example.vary.UI;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;

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

import java.util.List;

public class OnGameFragment extends Fragment {
    //TODO: анимации
    //TODO: динамический размер текста
    private int _yDelta;
    private int dropCardValue;
    TextView roundScoreView;
    TextView cardText;
    private int roundScore;
    private CardsViewModel viewModel;
    private int category;
    private FrameLayout card;
    private RelativeLayout pause;
    private boolean paused = false;
    private boolean previewed;
    private RelativeLayout preview;
    private boolean cardTextSetted = false;

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
    }

    @SuppressLint("SetTextI18n") //TODO refactor
    private void swipeDown(View v) {
        viewModel.declineCard();
        cardText.setText(viewModel.getCard());
        roundScoreView.setText(String.valueOf(--roundScore));
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_on_game, container, false);
        setViewModel();
        TextView teamName = view.findViewById(R.id.team_name_on_game);
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
        RelativeLayout root = view.findViewById(R.id.card_root);
        roundScoreView = view.findViewById(R.id.round_score);
        roundScore = 0;
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(dp(200), dp(200));
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                layoutParams.leftMargin = (root.getWidth() - layoutParams.width) / 2;
                layoutParams.topMargin = (root.getHeight() - layoutParams.height) / 2;
                card.setLayoutParams(layoutParams);
                root.addView(card);
                dropCardValue = dp(150);
            }
        });
        View.OnTouchListener cardSwipeListener = (v, event) -> {
            final int Y = (int) event.getRawY();
            Log.println(Log.DEBUG, "TopMargin", "" + layoutParams.topMargin + " " + dropCardValue);
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
                    _yDelta = Y - lParams.topMargin;
                    break;
                case MotionEvent.ACTION_UP:
                    if (layoutParams.topMargin < dropCardValue) {
                        swipeUp(v);
                    } else if (layoutParams.topMargin + v.getHeight() / 2 > root.getHeight() - dropCardValue) {
                        swipeDown(v);
                    }
                    layoutParams.leftMargin = (root.getWidth() - layoutParams.width) / 2;
                    layoutParams.topMargin = (root.getHeight() - layoutParams.height) / 2;
                    card.setLayoutParams(layoutParams);
                    break;
                case MotionEvent.ACTION_MOVE:
                    RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) v.getLayoutParams();
                    layoutParams1.topMargin = Y - _yDelta;
                    if (layoutParams1.topMargin > 0 & layoutParams1.topMargin + v.getHeight() < root.getHeight())
                        v.setLayoutParams(layoutParams1);
                    break;
            }
            root.invalidate();
            return true;
        };
        card.setOnTouchListener(cardSwipeListener);
        pause = view.findViewById(R.id.pause);
        setPause(false);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endPause();
            }
        });
        preview = view.findViewById(R.id.preview);
        setPreview(true);
        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endPreview();
            }
        });
        return view;
    }

    boolean isPaused() {
        return paused;
    }

    public void toPause() {
        //TODO pause timer service
        setPause(true);
    }

    public void endPause() {
        //TODO resume timer service
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
            setPreview(false);
            card.setVisibility(View.INVISIBLE);
            pause.setVisibility(View.VISIBLE);
        }
        else {
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
                if (timerCount == 0) {
                    callbackFunctions.callback(GameActions.open_round_result);
                }
                Log.d("Timer is ticking...", timerCount.toString());
            }
        };

        Observer<CurrentGameModel> observerCurrentGame = new Observer<CurrentGameModel>() {
            @Override
            public void onChanged(CurrentGameModel gameModel) {
                timerService.runTask(gameModel.getRoundDuration());
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
    }

    @Override
    public void onPause() {
        setPause(true);
        super.onPause();
    }

}