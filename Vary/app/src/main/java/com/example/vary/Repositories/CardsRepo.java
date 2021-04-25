package com.example.vary.Repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.vary.Models.CardModel;
import com.example.vary.Models.CategoryModel;

import java.util.Collections;
import java.util.List;

public class CardsRepo {
    private final static MutableLiveData<List<CardModel>> mCards = new MutableLiveData<>();
    private int cur_pos;
    private int amountOfCards;

    private static CardsRepo sInstance;

    //DB resource?

    public CardsRepo() {
    }

    public void fillCards(CategoryModel category, int amount) {
        //Если количество больше,чем загружено, дозагрузить
        amountOfCards = amount;
        mCards.postValue(category.getCards());
        // scan from db?
        cur_pos = 0;
    }

    public void mixCards() {
        if (mCards.getValue() != null) {
            Collections.shuffle(mCards.getValue());
        }
    }

    public String getCard() {
        if (cur_pos == getAmountOfCards()) {
            cur_pos = 0;
        }
        CardModel cards = mCards
                .getValue()
                .get(cur_pos);
        cur_pos += 1;
        return cards.getText();
    }

    public static synchronized CardsRepo getInstance() {
        if (sInstance == null) {
            sInstance = new CardsRepo();
        }
        return sInstance;
    }

    public LiveData<List<CardModel>> getCards()
    {
        return mCards;
    } //?

    public int getAmountOfCards() {
        return mCards.getValue().size();
    }

    public void saveCards() {
        //saving curr cards to db?
    }

}
