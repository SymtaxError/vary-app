package com.example.vary.Repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.vary.Database.DbManager;
import com.example.vary.Models.CardModel;
import com.example.vary.UI.CardCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class
CardsRepo {
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
        int index = startRoundPosition;
        while (index < currentPosition) {
            CardModel card = cards.get(index);
            if (!card.getAnswerState()) {
                cards.remove(index);
                currentPosition--;
                cards.add(card);
            }
            else
                index++;
        }
        startRoundPosition = currentPosition;
        List <CardModel> unusedCards = cards.subList(currentPosition, cards.size() - 1);
        Collections.shuffle(unusedCards);
        mCards.postValue(cards);
    }

    public void changeAnswerState(int pos) {
        List<CardModel> cards = mCards.getValue();
        cards.get((pos + startRoundPosition) % getAmountOfCards()).changeAnswerState();
        mCards.postValue(cards);
    }

    public boolean getAnswerState(int pos) {
        return mCards.getValue()
                .get(pos + startRoundPosition)
                .getAnswerState();
    }

    public void answerCard() {
        List<CardModel> cards = mCards.getValue();
        cards.get(currentPosition).setAnswerState(true);
        mCards.postValue(cards);
        currentPosition++;
    }

    public void declineCard() {
        List<CardModel> cards = mCards.getValue();
        cards.get(currentPosition).setAnswerState(false);
        mCards.postValue(cards);
        currentPosition++;
    }

    public void fillCards(String categoryName, int amount, int index) {
        //Если количество больше,чем загружено, дозагрузить
        amountOfCards = amount;
//        mCards.postValue();
        // scan from db?
        dbManager.getCards(categoryName);
        currentPosition = 0;
    }

    public void endCards() {
        boolean answered = true;
        for (int i = startRoundPosition; i < getAmountOfCards() && answered; i++) {
            if (!mCards.getValue().get(i).getAnswerState()) {
                answered = false;
            }
        }
        if (!answered) {
            int oldStartRound = startRoundPosition;
            newRoundMix();
            startRoundPosition = oldStartRound;
        }
        else {
            mCallback.callback();
            currentPosition = 0;
        }
    }

    public void mixCards() {
        if (mCards.getValue() != null) {
            for (CardModel card : mCards.getValue())
                card.setAnswerState(false);
            Collections.shuffle(mCards.getValue());
        }
    }

    public void setCardCallback(CardCallback callback) {
        mCallback = callback;
    }

    public String getCard() {
        if (currentPosition == getAmountOfCards()) {
            endCards();
        }
        CardModel cards = mCards
                .getValue()
                .get(currentPosition);
        return cards.getText();
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
        return mCards.getValue().size();
    }

    public void saveCards() {
        //saving curr cards to db?
    }

    public void setDbManager(DbManager dbManager) {
        this.dbManager = dbManager;
        dbManager.setCardRepositoryListener(cardRepositoryListener);
    }

    public String getUsedCardByPosition(int position) {
        return mCards.getValue().get(position + startRoundPosition).getText();
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int position) {
        currentPosition = position;
    }

    public int getAmountOfUsedCards() {
        if (currentPosition < startRoundPosition) {
            return getAmountOfCards() - startRoundPosition + currentPosition;
        }
        return currentPosition - startRoundPosition;
    }
}
