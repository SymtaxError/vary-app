package com.example.vary;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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

import java.util.Random;

public class OnGameFragment extends Fragment {
    //TODO: анимации
    //TODO: динамический размер текста
    private int _xDelta;
    private int _yDelta;
    private int layoutSizeY;
    private int viewSizeY;
    private int dropCardValue;
    TextView roundScoreView;
    TextView cardText;
    private int roundScore;


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

    @SuppressLint("SetTextI18n") //TODO refactor
    private void swipeUp(View v) {
        cardText.setText("CARD "+Math.abs(new Random().nextInt()%100));
        roundScoreView.setText(String.valueOf(++roundScore));
    }

    @SuppressLint("SetTextI18n") //TODO refactor
    private void swipeDown(View v) {
        cardText.setText("CARD "+Math.abs(new Random().nextInt()%100));
        roundScoreView.setText(String.valueOf(--roundScore));
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_on_game, container, false);
        FrameLayout card = new FrameLayout(view.getContext());
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

        cardText.setText("CARD"); //TODO delete
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
                dropCardValue = dp(100);
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
        return view;
    }

}