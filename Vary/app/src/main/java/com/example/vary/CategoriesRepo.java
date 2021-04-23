package com.example.vary;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class CategoriesRepo {
    private final CategoriesNetworkService categoriesNetworkService = new CategoriesNetworkService();
    private final static MutableLiveData<List<CategoryModel>> mCategories = new MutableLiveData<>();
    private final CardsRepo mCardsRepo = new CardsRepo();
    private final static MutableLiveData<List<CardModel>> mCards = new MutableLiveData<>();

    public LiveData<List<CategoryModel>> getCategories() {
        return mCategories;
    }

    public CategoryModel getCategory(int index) {
        if (index >= mCategories.getValue().size()) {
            loadCategoriesFromDatabase(index);
        }
        return mCategories.getValue().get(index);
    }

    public void getCards(int index) {
        mCardsRepo.fillCards(mCategories.getValue().get(index));
    }

    public void mixCards() {
        if (mCards != null) {
            mCardsRepo.mixCards();
        }
    }

    public CardModel getCard()
    {
        return mCardsRepo.getCard();
    }

    public void saveState() {

    }

    private void loadCategoriesFromDatabase(int index)
    {
        //DB
    }

    public CategoriesAPI getCategoriesAPI() {
        return categoriesNetworkService.getCategoriesAPI();
    }

    public void getNewCategories(int version) {
        //откуда брать версию?
        categoriesNetworkService.getNewCategories(version);
//        if (loadStatus.getValue().error == null)
            loadCategoriesToDatabase();
    }

    private void loadCategoriesToDatabase(){
        //Загрузка полученного
    };
}
