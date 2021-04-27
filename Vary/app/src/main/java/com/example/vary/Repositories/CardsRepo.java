package com.example.vary.Repositories;

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

    //DB resource?

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
        }
        CardModel cards = mCards
                .getValue()
                .get(currentPosition);
        currentPosition += 1;
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
}
