package com.example.vary.Repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.vary.Database.DbManager;
import com.example.vary.Models.CardModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CardsRepo {
    private final static MutableLiveData<List<CardModel>> mCards = new MutableLiveData<>();
    private int currentPosition;
    private int amountOfCards;
    private DbManager dbManager = null;
    private static CardsRepo sInstance;
    private MutableLiveData<List<CardModel>> answered = new MutableLiveData<>();
    private MutableLiveData<List<CardModel>> declined = new MutableLiveData<>();

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
        List <CardModel> answer = answered.getValue();
        if (answer != null) {
            cards.addAll(currentPosition, answer);
            currentPosition += answer.size();
        }
        List<CardModel> decline = declined.getValue();
        if (decline != null) {
            cards.addAll(decline);
        }
        List <CardModel> unusedCards = cards.subList(currentPosition, cards.size() - 1);
        Collections.shuffle(unusedCards);
        answered.postValue(new ArrayList<>());
        declined.postValue(new ArrayList<>());
        mCards.postValue(cards);
    }

    public LiveData<List<CardModel>> getAnsweredCards() {
        return answered;
    }

    public LiveData<List<CardModel>> getDeclinedCards() {
        return declined;
    }

    public void makeAnswered(int dec_pos) {
        List<CardModel> decline = declined.getValue();
        List<CardModel> answer = answered.getValue();
        if (answer == null) {
            answer = new ArrayList<>();
        }
        answer.add(answer.get(dec_pos));
        decline.remove(dec_pos);
        answered.postValue(answer);
        declined.postValue(decline);
    }

    public void makeDeclined(int ans_pos) {
        List<CardModel> decline = declined.getValue();
        List<CardModel> answer = answered.getValue();
        if (decline == null)
        {
            decline = new ArrayList<>();
        }
        decline.add(answer.get(ans_pos));
        answer.remove(ans_pos);
        answered.postValue(answer);
        declined.postValue(decline);
    }

    public void answerCard() {
        List<CardModel> cards = mCards.getValue();
        CardModel curCard = cards.remove(currentPosition);
        List<CardModel> answer = answered.getValue();
        if (answer == null) {
            answer = new ArrayList<>();
        }
        answer.add(curCard);
        answered.postValue(answer);
        mCards.postValue(cards);
    }

    public void declineCard() {
        List<CardModel> cards = mCards.getValue();
        Log.d("Lalala", "heh " + cards);
        CardModel curCard = cards.remove(currentPosition);
        List<CardModel> decline = declined.getValue();
        if (decline == null)
        {
            decline = new ArrayList<>();
        }
        decline.add(curCard);
        Log.d("Lalala", "heh " + cards);
        mCards.postValue(cards);
        declined.postValue(decline);
    }

    public void fillCards(String categoryName, int amount, int index) {
        //Если количество больше,чем загружено, дозагрузить
        amountOfCards = amount;
//        mCards.postValue();
        // scan from db?
        dbManager.getCards(categoryName);
        currentPosition = 0;
    }

    public void mixCards() {
        if (mCards.getValue() != null) {
            Collections.shuffle(mCards.getValue());
        }
    }

    public String getCard() {
        if (currentPosition == getAmountOfCards()) {
            currentPosition = 0;
            newRoundMix();
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

    public String getAnsweredCardByPosition(int position) {
        return answered.getValue().get(position).getText();
    }

    public String getDeclinedCardByPosition(int position) {
        return declined.getValue().get(position).getText();
    }
}
