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
    private boolean ended = false;

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

    public void answerCard() {
        List<CardModel> cards = mCards.getValue();
        if (cards != null) {
            cards.get(currentPosition).setAnswerState(true);
            mCards.postValue(cards);
            currentPosition++;
        }
    }

    public void declineCard() {
        List<CardModel> cards = mCards.getValue();
        if (cards != null) {
            cards.get(currentPosition).setAnswerState(false);
            mCards.postValue(cards);
            currentPosition++;
        }
    }

    public void fillCards(String categoryName, int amount) {
        //Если количество больше,чем загружено, дозагрузить
        amountOfCards = amount;
//        mCards.postValue();
        // scan from db?
        dbManager.getCards(categoryName);
        currentPosition = 0;
    }

    public boolean newRoundRequired() {

        if (currentPosition == getAmountOfCards()) {
            mixCards();
            return true;
        }
        return false;
    }

    public void endCards() {
        boolean answered = true;
        List<CardModel> cards = mCards.getValue();
        if (cards != null) {
            for (int i = startRoundPosition; i < getAmountOfCards() && answered; i++) {
                if (! cards
                        .get(i)
                        .getAnswerState()) {
                    answered = false;
                }
            }
            if (!answered) {
                ended = true;
                Log.d("Cards", "Mixed");
                int oldStartRound = startRoundPosition;
                newRoundMix();
                startRoundPosition = oldStartRound;
                Log.d("Cards", "new cur pos = " + currentPosition);
            }
            else {
                Log.d("Callback", "called");
                mCallback.callback();
            }
        }
    }

    public void mixCards() {
        if (mCards.getValue() != null) {
            for (CardModel card : mCards.getValue())
                card.setAnswerState(false);
            Collections.shuffle(mCards.getValue());
            currentPosition = 0;
            startRoundPosition = 0;
        }
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
        if (ended) {
            return getAmountOfCards() - startRoundPosition;
        }
        return currentPosition - startRoundPosition;
    }
}
