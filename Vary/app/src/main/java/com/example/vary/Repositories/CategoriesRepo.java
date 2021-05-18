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
import java.util.Objects;

public class CategoriesRepo implements SetDataCallback {
    private static CategoriesNetworkService categoriesNetworkService;
    private final static CardsRepo mCardsRepo = CardsRepo.getInstance();
    private static CategoriesRepo sInstance = null;
    private final static MutableLiveData<List<CategoryModel>> mCategories = new MutableLiveData<>();
    private final static LiveData<List<CardModel>> mCards = mCardsRepo.getCards();
    private DbManager dbManager = null;
    private static int version;

    private final DbManager.CategoryRepositoryListener categoryRepositoryListener = new DbManager.CategoryRepositoryListener() {
        @Override
        public void onGetCategoriesNoCards(List<CategoryModel> categoryModels) {
            if (categoryModels.size() > 0)
                mCategories.postValue(categoryModels); //TODO обработать
        }
    };


//    public CategoryModel getCategory(int index) {
//        if (index >= mCategories.getValue().size()) {
//            loadCategoriesFromDatabase(index);
//        }
//        return mCategories.getValue().get(index);
//    }

    public ArrayList<String> getCategoriesNames() {
        ArrayList<String> names = new ArrayList<>();

        for (int i = 0; i < getCategoriesSize(); i++) {
            names.add(Objects.requireNonNull(mCategories
                    .getValue())
                    .get(i)
                    .getName());
        }

        return names;
    }

    public void setDbManager(Context context) {
        dbManager = DbManager.getInstance(context);
        dbManager.setCategoryRepositoryListener(categoryRepositoryListener);
        mCardsRepo.setDbManager(dbManager);
        loadCategoriesFromDatabase();
    }

    public void setNetworkService(Context context) {
        categoriesNetworkService = CategoriesNetworkService.getInstance(context.getResources().getString(R.string.base_url));
    }

    public LiveData<List<CategoryModel>> getCategories() {
        return mCategories;
    }

    public void fillCards(int index, int amount) {
        CategoryModel category = Objects.requireNonNull(mCategories.getValue()).get(index);
        mCardsRepo.fillCards(category.getName(), amount, index);
    }

    public LiveData<List<CardModel>> getCards() {
        return mCardsRepo.getCards();
    }

    public void mixCards() {
        if (mCards != null) {
            mCardsRepo.mixCards();
        }
    }

    public void declineCard() {
        mCardsRepo.declineCard();
    }

    public void answerCard() {
        mCardsRepo.answerCard();
    }

    public LiveData<List<CardModel>> getAnsweredCards() {
        return mCardsRepo.getAnsweredCards();
    }

    public LiveData<List<CardModel>> getDeclinedCards() {
        return mCardsRepo.getDeclinedCards();
    }

    public void makeDeclined(int dec_pos) {
        mCardsRepo.makeDeclined(dec_pos);
    }

    public void makeAnswered(int ans_pos) {
        mCardsRepo.makeAnswered(ans_pos);
    }

    public void newRoundMix() {
        mCardsRepo.newRoundMix();
    }

    public String getCard() {
        return mCardsRepo.getCard();
    }

    public void saveState() {
        //а надо ли?
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

    public void onLoaded(List<CategoryModel> categories, LoadDataCallback callback) { //TODO вернуть из getNewCategories
        if (categories != null) {
            Log.d("network", "add " + categories.size());
            loadCategoriesToDatabase(categories);
            addCategories(categories);
            callback.onLoad(null);
        } else {
            Log.d("Sadddd", "Wrong data back");
        }
    }

    private void addCategories(List<CategoryModel> categories) {
        List<CategoryModel> categoriesCapture = mCategories.getValue();
        if (categoriesCapture == null) {
            categoriesCapture = new ArrayList<>();
        }
        for (CategoryModel category : categories) {
            CategoryModel oldCategory = null;
            if (categoriesCapture.size() > 0) {
                for (CategoryModel existingCategoryIter : categoriesCapture) {
                    if (category.getName().equals(existingCategoryIter.getName())) {
                        oldCategory = existingCategoryIter;
                    }
                }
            }
            if (oldCategory == null)
                categoriesCapture.add(category);
        }
        mCategories.postValue(categoriesCapture);
        updateVersion();
    }

    private void updateVersion() {
        List<CategoryModel> categoriesValue = mCategories.getValue();
        if (categoriesValue != null)
            for (CategoryModel category : categoriesValue) {
                int categoryVersion = category.getVersion();
                if (categoryVersion > version)
                    version = categoryVersion;
            }
        else
            version = -1;
    }

    private void updateVersion(List <CategoryModel> newCategories) {
        for (CategoryModel category : newCategories) {
            int categoryVersion = category.getVersion();
            if (categoryVersion > version)
                version = categoryVersion;
        }
    }

    private void loadCategoriesFromDatabase() {
        dbManager.getCategoriesNoCards(); //TODO если их нет - грузить с нетворка + обыграть на UI загрузку
        updateVersion();
    }

    public CategoriesAPI getCategoriesAPI() {
        return categoriesNetworkService.getCategoriesAPI();
    }

    public void getNewCategories(LoadDataCallback callback) { //TODO переделать
        //откуда брать версию? отсюда же
        updateVersion();
        categoriesNetworkService.getNewCategories(version, callback, this);
//        dbManager.getCategoriesVersion(new DbManager.VersionCallback() {
//            @Override
//            public void onVersionGot(int version) {
//                categoriesNetworkService.getNewCategories(version, callback, this {
//                    @Override
//                    public void onLoaded(List<CategoryModel> categories, LoadDataCallback callback) {
//                        if (categories != null) {
//                            Log.d("save loaded", "saved " + categories.size());
//                            mCategories.postValue(categories);
//                            loadCategoriesToDatabase();
//                            callback.onLoad(null);
//                            for (CategoryModel category : categories) {
//                                int categoryVersion = category.getVersion();
//                                if (categoryVersion > version)
//                                    version = categoryVersion;
//                            }
//                        } else {
//                            Log.d("Sadddd", "Wrong data back");
//                        }
//                    }
//                });
//            }
//        });
    }

    public int getAmountOfCards() {
        return mCardsRepo.getAmountOfCards();
    }

    private void loadCategoriesToDatabase(List<CategoryModel> categoryModelList) {
        dbManager.update(categoryModelList);
    }

    ;

    public int getCategoriesSize() {
        return mCategories
                .getValue()
                .size();
    }

    public String getAnsweredCardByPosition(int position) {
        return mCardsRepo.getAnsweredCardByPosition(position);
    }

    public String getDeclinedCardByPosition(int position) {
        return mCardsRepo.getDeclinedCardByPosition(position);
    }
}
