package com.example.vary;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Collections;
import java.util.List;

public class CardsRepo {
    private final static MutableLiveData<List<CardModel>> mCards = new MutableLiveData<>();
    private int cur_pos;

    //DB resource?

    public CardsRepo() {
    }

    public void fillCards(CategoryModel category) {
        // scan from db?
        cur_pos = 0;
    }

    public void mixCards() {
        if (mCards.getValue() != null) {
            Collections.shuffle(mCards.getValue());
        }
    }

    public CardModel getCard() {
        CardModel cards = mCards
                .getValue()
                .get(cur_pos);
        return cards;
    }

    public LiveData<List<CardModel>> getCards()
    {
        return mCards;
    } //?


    public void saveCards() {
        //saving curr cards to db?
    }

}
