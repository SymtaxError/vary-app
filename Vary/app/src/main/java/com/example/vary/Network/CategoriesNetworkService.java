package com.example.vary.Network;

import android.net.ParseException;
import android.util.Log;

import com.example.vary.Models.CardModel;
import com.example.vary.Models.CategoryModel;
import com.example.vary.UI.SetDataCallback;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class CategoriesNetworkService {
    private static CategoriesNetworkService mInstance;
    private final Retrofit mRetrofit;
    private static List<CategoryModel> mCategories = new ArrayList<>();
    private final static String TAG = "CardsNetworkService";

    private CategoriesAPI mCategoriesAPI;


    public CategoriesNetworkService(String url) {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(url)
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


    public void getNewCategories(int version, SetDataCallback callbackLoad) {
        mCategoriesAPI.getNewCategories(version).enqueue(new Callback<List<CategoriesAPI.CategoryPlain>>() {
            @Override
            public void onResponse(@NotNull Call<List<CategoriesAPI.CategoryPlain>> call,
                                   @NotNull Response<List<CategoriesAPI.CategoryPlain>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mCategories = transformDeck(response.body(), callbackLoad);
                    Log.d(TAG, "Categories size " + mCategories.size());
                    callbackLoad.onLoaded(mCategories);
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<CategoriesAPI.CategoryPlain>> call, @NotNull Throwable t) {
                Log.d(TAG, "Failed to load decks ", t);
                callbackLoad.onLoaded(t);
            }
        });
    }

    public void getVersion(SetDataCallback callbackLoad) {
        mCategoriesAPI.getVersion().enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NotNull Call<Integer> call, @NotNull Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callbackLoad.onLoaded(response.body());
                }
            }

            @Override
            public void onFailure(@NotNull Call<Integer> call, @NotNull Throwable t) {
                callbackLoad.onLoaded(t);
            }
        });
    }

    private List<CategoryModel> transformDeck(List<CategoriesAPI.CategoryPlain> body, SetDataCallback callback) {
        List<CategoryModel> result = new ArrayList<>();
        for (CategoriesAPI.CategoryPlain categoryPlain : body) {
            try {
                CategoryModel deck = map(categoryPlain, callback);
                result.add(deck);
                Log.d(TAG, "Loaded deck " + deck.getName());
            } catch (ParseException e) {
                callback.onLoaded(e);
            }
        }
        Log.d(TAG, "Loaded " + result.size() + " elems");
        return result;
    }

    private static CategoryModel map(CategoriesAPI.CategoryPlain categoryPlain, SetDataCallback callback) throws ParseException {
        CategoryModel categoryModel = new CategoryModel(
                categoryPlain.name,
                categoryPlain.version,
                categoryPlain.accessLevel
        );
        categoryModel.mCards = transformCard(categoryPlain.name, categoryPlain.cards, callback);
        return categoryModel;
    }

    private static CardModel map(String categoryName, CategoriesAPI.CardPlain cardPlain) throws ParseException {
        return new CardModel(
                cardPlain.version,
                cardPlain.id,
                cardPlain.name,
                categoryName
        );
    }

    private static List<CardModel> transformCard(String categoryId, List<CategoriesAPI.CardPlain> cardPlains, SetDataCallback callback) {
        List<CardModel> result = new ArrayList<>();
        for (CategoriesAPI.CardPlain cardPlain : cardPlains) {
            try {
                CardModel card = map(categoryId, cardPlain);
                result.add(card);
                Log.d(TAG, "Loaded card " + card.getText() + " size " + result.size());
            } catch (ParseException e) {
                callback.onLoaded(e);
//            } catch (NullPointerException e) {
//                Log.d(TAG, "NULL loaded card, error");
            } catch (Exception e) {
                Log.d(TAG, "An error:\n"+e.getMessage());
            }
        }
        return result;
    }

}
