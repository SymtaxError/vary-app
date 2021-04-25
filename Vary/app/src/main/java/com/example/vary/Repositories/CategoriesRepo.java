package com.example.vary.Repositories;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.vary.Database.DbManager;
import com.example.vary.UI.LoadDataCallback;
import com.example.vary.Models.CardModel;
import com.example.vary.Models.CategoryModel;
import com.example.vary.Network.CategoriesAPI;
import com.example.vary.Network.CategoriesNetworkService;
import com.example.vary.R;
import com.example.vary.UI.SetDataCallback;

import java.util.ArrayList;
import java.util.List;

public class CategoriesRepo implements SetDataCallback {
    private static CategoriesNetworkService categoriesNetworkService;
    private final static CardsRepo mCardsRepo = CardsRepo.getInstance();
    private static CategoriesRepo sInstance = null;
    private final static MutableLiveData<List<CategoryModel>> mCategories = new MutableLiveData<>();
    private final static LiveData<List<CardModel>> mCards = mCardsRepo.getCards();
    private static DbManager dbManager = null;


    public CategoryModel getCategory(int index) {
        if (index >= mCategories.getValue().size()) {
            loadCategoriesFromDatabase(index);
        }
        return mCategories.getValue().get(index);
    }

    public ArrayList<String> getCategoriesNames() {
        ArrayList<String> names = new ArrayList<>();

        for (int i = 0; i < getCategoriesSize(); i++) {
            names.add(mCategories
                    .getValue()
                    .get(i)
                    .getName());
        }

        return names;
    }

    public void setDbManager(Context context) {
        dbManager = DbManager.getInstance(context);
    }

    public void setNetworkService(Context context) {
        categoriesNetworkService = CategoriesNetworkService.getInstance(context.getResources().getString(R.string.base_url));
    }

    public LiveData<List<CategoryModel>> getCategories() {
        return mCategories;
    }

    public void fillCards(int index, int amount) {
        List<CategoryModel> category = mCategories.getValue();
        if (category == null) {
            category = new ArrayList<>();
            List<CardModel> cards = new ArrayList<>();
            cards.add(new CardModel(1, "1", "Rain", "f"));
            cards.add(new CardModel(1, "2", "Storm", "f"));
            cards.add(new CardModel(1, "3", "Sun", "f"));
            cards.add(new CardModel(1, "4", "Cloudy", "f"));
            cards.add(new CardModel(1, "5", "Windy", "f"));
            category.add(new CategoryModel("Weather", 1, 0, cards));
            mCategories.postValue(category);
        }
        mCardsRepo.fillCards(category.get(index), amount);
    }

    public LiveData<List<CardModel>> getCards() {
        return mCardsRepo.getCards();
    }

    public void mixCards() {
        if (mCards != null) {
            mCardsRepo.mixCards();
        }
    }

    public String getCard()
    {
        return mCardsRepo.getCard();
    }

    public void saveState() {

    }

    public static synchronized CategoriesRepo getInstance() {
        if (sInstance == null) {

            Log.d("GetInstance", "created ");
            sInstance = new CategoriesRepo();

        }

        boolean voidCat = mCategories.getValue() == null;
        Log.d("GetInstance", "Categories are void inside " + voidCat);
        return sInstance;
    }

    public void onLoaded(List<CategoryModel> categories, LoadDataCallback callback) {
        if (categories != null) {
            Log.d("save loaded", "saved " + categories.size());
            mCategories.postValue(categories);
            loadCategoriesToDatabase();
            callback.onLoad(null);
        }
        else {
            Log.d("Sadddd", "Wrong data back");
        }
    }

    private void loadCategoriesFromDatabase(int index)
    {
        //DB
    }

    public CategoriesAPI getCategoriesAPI() {
        return categoriesNetworkService.getCategoriesAPI();
    }

    public void getNewCategories(int version, LoadDataCallback callback) {
        //откуда брать версию?
        categoriesNetworkService.getNewCategories(version, callback, this);
    }

    public int getAmountOfCards() {
        return mCardsRepo.getAmountOfCards();
    }

    private void loadCategoriesToDatabase(){
        //Загрузка полученного
    };

    public int getCategoriesSize() {
        return mCategories
                .getValue()
                .size();
    }
}
