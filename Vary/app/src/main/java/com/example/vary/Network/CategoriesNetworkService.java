package com.example.vary.Network;

import android.net.ParseException;
import android.util.Log;

import com.example.vary.UI.LoadDataCallback;
import com.example.vary.Models.CardModel;
import com.example.vary.Models.CategoryModel;
import com.example.vary.UI.SetDataCallback;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class CategoriesNetworkService {
    private static CategoriesNetworkService mInstance;
    private static String mBaseUrl;
    private Retrofit mRetrofit;
    private static List<CategoryModel> mCategories = new ArrayList<>();
    private final static String TAG = "CardsNetworkService";

    private CategoriesAPI mCategoriesAPI;


    public CategoriesNetworkService(String url) {
        mBaseUrl = url;
        mRetrofit = new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();
        getCategoriesAPI();
    }


    public static CategoriesNetworkService getInstance(String url) {
        if (mInstance == null) {
            mInstance = new CategoriesNetworkService(url);
        }
        return mInstance;
    }

    public CategoriesAPI getCategoriesAPI() {
        if (mCategoriesAPI == null) {
            mCategoriesAPI = mRetrofit.create(CategoriesAPI.class);
        }
        return mCategoriesAPI;
    }


    public void getNewCategories(int version, LoadDataCallback callback, SetDataCallback callbackLoad) {
        mCategoriesAPI.getNewCategories(version).enqueue(new Callback<List<CategoriesAPI.CategoryPlain>>() {
            @Override
            public void onResponse(Call<List<CategoriesAPI.CategoryPlain>> call,
                                   Response<List<CategoriesAPI.CategoryPlain>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mCategories = transformDeck(response.body(), callback);
                    Log.d(TAG, "Categories size " + mCategories.size());
                    callbackLoad.onLoaded(mCategories, callback);
                }
            }

            @Override
            public void onFailure(Call<List<CategoriesAPI.CategoryPlain>> call, Throwable t) {
                Log.d(TAG, "Failed to load decks ", t);
                callback.onLoad(t);
            }
        });
    }

    private List<CategoryModel> transformDeck(List<CategoriesAPI.CategoryPlain> body, LoadDataCallback callback) {
        List<CategoryModel> result = new ArrayList<>();
        for (CategoriesAPI.CategoryPlain categoryPlain : body) {
            try {
                CategoryModel deck = map(categoryPlain, callback);
                result.add(deck);
                Log.d(TAG, "Loaded deck " + deck.getName());
            } catch (ParseException e) {
                callback.onLoad(null);
                e.printStackTrace();
            }
        }
        Log.d(TAG, "Loaded " + result.size() + " elems");
        return result;
    }

    private static CategoryModel map(CategoriesAPI.CategoryPlain categoryPlain, LoadDataCallback callback) throws ParseException {
        return new CategoryModel(
                categoryPlain.name,
                categoryPlain.version,
                categoryPlain.accessLevel,
                transformCard(categoryPlain.name, categoryPlain.cards, callback)
        );
    }

    private static CardModel map(String categoryName, CategoriesAPI.CardPlain cardPlain) throws ParseException {
        return new CardModel(
                cardPlain.version,
                cardPlain.id,
                cardPlain.name,
                categoryName
        );
    }

    private static List<CardModel> transformCard(String categoryId, List<CategoriesAPI.CardPlain> cardPlains, LoadDataCallback callback) {
        List<CardModel> result = new ArrayList<>();
        for (CategoriesAPI.CardPlain cardPlain : cardPlains) {
            try {
                CardModel card = map(categoryId, cardPlain);
                result.add(card);
                Log.d(TAG, "Loaded card " + card.getText() + " size " + result.size());
            } catch (ParseException e) {
                callback.onLoad(e);
                e.printStackTrace();
            }
        }
        return result;
    }

}
