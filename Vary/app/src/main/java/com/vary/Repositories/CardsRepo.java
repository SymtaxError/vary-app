package com.vary.Repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.vary.Database.DbManager;
import com.vary.Models.CardModel;
import com.vary.UI.CardCallback;
import com.vary.UI.PenaltyType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CardsRepo {
    private final static MutableLiveData<List<CardModel>> mCards = new MutableLiveData<>();
    private int startRoundPosition = 0;
    private int currentPosition = 0;
    private int amountOfCards;
    private DbManager dbManager = null;
    private static CardsRepo sInstance;
    private CardCallback mCallback;

    private final DbManager.CardRepositoryListener cardRepositoryListener = new DbManager.CardRepositoryListener() {
        @Override
        public void onGetCards(List<CardModel> cardsModel) {
            Random rand = new Random();
            List<CardModel> randomCards = new ArrayList<>();
            for (int i = 0; i < amountOfCards; i++) {
                int randomIndex = rand.nextInt(cardsModel.size());
                randomCards.add(cardsModel.get(randomIndex));
                cardsModel.remove(randomIndex);
            }
            mCards.postValue(randomCards);
        }
    };

    public CardsRepo() {
    }

    public void newRoundMix() {
        List <CardModel> cards = mCards.getValue();
        if (cards != null) {
            cards = sortCards();
            startRoundPosition = currentPosition;
            List <CardModel> unusedCards = cards.subList(currentPosition, cards.size() - 1);
            Collections.shuffle(unusedCards);
            mCards.postValue(cards);
        }
    }

    private List<CardModel> sortCards() {
        List <CardModel> cards = mCards.getValue();
        if (cards != null) {
            int index = startRoundPosition;
            while (index < currentPosition) {
                CardModel card = cards.get(index);
                if (!card.getAnswerState()) {
                    card.setAnsweredTeam(-1);
                    cards.remove(index);
                    currentPosition--;
                    cards.add(card);
                } else
                    index++;
            }
        }
        return cards;
    }

    public void changeAnswerState(int pos) {
        List<CardModel> cards = mCards.getValue();
        if (cards != null) {
            cards.get(pos + startRoundPosition).changeAnswerState();
            mCards.postValue(cards);
        }
    }

    public boolean getAnswerState(int pos) {
        if (mCards.getValue() != null) {
            return mCards.getValue()
                    .get(pos + startRoundPosition)
                    .getAnswerState();
        }
        return false;
    }

    public void answerCard(int team) {
        List<CardModel> cards = mCards.getValue();
        if (cards != null) {
            cards.get(currentPosition).setAnswerState(true);
            cards.get(currentPosition).setAnsweredTeam(team);
            mCards.postValue(cards);
            currentPosition++;
        }
    }

    public void setAnsweredTeam(int team) {
        List<CardModel> cards = mCards.getValue();
        if (cards != null) {
            cards.get(currentPosition - 1).setAnswerState(team != -1);
            cards.get(currentPosition - 1).setAnsweredTeam(team);
            mCards.postValue(cards);
        }
    }

    public void declineCard() {
        List<CardModel> cards = mCards.getValue();
        if (cards != null) {
            cards.get(currentPosition).setAnswerState(false);
            cards.get(currentPosition).setAnsweredTeam(0);
            mCards.postValue(cards);
            currentPosition++;
        }
    }

    public void fillCards(String categoryName, int amount) {
        amountOfCards = amount;
        dbManager.getCards(categoryName);
        currentPosition = 0;
    }

    public void setCards(List<CardModel> cards) {
        mCards.postValue(cards);
    }

    public boolean newRoundRequiredAndSort() {
        sortCards();
        return newRoundRequired();
    }

    public boolean newRoundRequired() {
        return currentPosition == getAmountOfCards();
    }

    public int getCardsLeft() {
        return getAmountOfCards() - currentPosition;
    }



    public void endCards() {
        List<CardModel> cards = mCards.getValue();
        if (cards != null) {
            Log.d("Callback", "called");
            mCallback.callback();
        }
    }

    public void mixCards() {
        if (mCards.getValue() != null) {
            for (CardModel card : mCards.getValue()) {
                card.setAnswerState(false);
                card.setAnsweredTeam(0);
            }
            Collections.shuffle(mCards.getValue());
        }
        currentPosition = 0;
        startRoundPosition = 0;
    }

    public void setCardCallback(CardCallback callback) {
        mCallback = callback;
    }

    public String getCard() {
        List<CardModel> cards = mCards.getValue();
        if (cards != null) {
            if (currentPosition == getAmountOfCards()) {
                endCards();
            }
            if (currentPosition != getAmountOfCards()) {
                CardModel card = cards.get(currentPosition);
                return card.getText();
            }

        }
        return null;
    }

    public static synchronized CardsRepo getInstance() {
        if (sInstance == null) {
            sInstance = new CardsRepo();
        }
        return sInstance;
    }

    public LiveData<List<CardModel>> getCards() {
        return mCards;
    } //?

    public int getAmountOfCards() {
        if (mCards.getValue() != null) {
            return mCards.getValue().size();
        }
        return 0;
    }

    public int getAnsweredTeam(int pos) {
        if (mCards.getValue() != null) {
            return mCards.getValue()
                    .get(pos + startRoundPosition)
                    .getAnsweredTeam();
        }
        return 0;
    }

    public int countPoints(PenaltyType penalty) {
        int decreaseValue = 0;
        if (penalty == PenaltyType.lose_points) {
            decreaseValue = -1;
        }
        int points = 0;
        for (int index = startRoundPosition; index < currentPosition && getAnsweredTeam(index - startRoundPosition) == 0; index++) {
            points = (getAnswerState(index - startRoundPosition)) ? points + 1 : points + decreaseValue;
        }
        return points;
    }

    public void setDbManager(DbManager dbManager) {
        this.dbManager = dbManager;
        dbManager.setCardRepositoryListener(cardRepositoryListener);
    }

    public String getUsedCardByPosition(int position) {
        if (mCards.getValue() != null) {
            return mCards.getValue().get(position + startRoundPosition).getText();
        }
        return null;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public int getStartRoundPosition() {
        return startRoundPosition;
    }


    public void setCurrentPosition(int position) {
        currentPosition = position;
    }

    public void setStartRoundPosition(int position) {
        startRoundPosition = position;
    }

    public int getAmountOfUsedCards() {
        return currentPosition - startRoundPosition;
    }
}
