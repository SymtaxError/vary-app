package com.example.vary;

import android.net.ParseException;
import android.util.Log;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class CategoriesNetworkService {
    private static CategoriesNetworkService mInstance;
    private static final String mBaseUrl = "http:";
    private Retrofit mRetrofit;
    private final static MutableLiveData<List<CategoryModel>> mDecks = new MutableLiveData<>();
    private final static String TAG = "CardsNetworkService";
//    private volatile Executor executor = Executors.newSingleThreadExecutor();

    private CategoriesAPI mCategoriesAPI;


    public CategoriesNetworkService() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();
    }

    public static CategoriesNetworkService getInstance() {
        if (mInstance == null) {
            mInstance = new CategoriesNetworkService();
        }
        return mInstance;
    }

    public CategoriesAPI getCategoriesAPI() {
        if (mCategoriesAPI == null) {
            mCategoriesAPI = mRetrofit.create(CategoriesAPI.class);
        }
        return mCategoriesAPI;
    }

//
//    public LiveData<List<CardModel>> getCategories() {
//        return Transformations.map(mCards, new Function<List<CardModel>, List<CardModel>>() {
//            @Override
//            public List<CardModel> apply(List<CardModel> input) {
//                return input;
//            }
//        });
//    }
//
//    public void loadCards() {
//        mCategoriesAPI.getNewCards().enqueue(new Callback<List<CategoriesAPI.CardPlain>>() {
//            @Override
//            public void onResponse(Call<List<CategoriesAPI.CardPlain>> call,
//                                   Response<List<CategoriesAPI.CardPlain>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    mCards.postValue(transformCard(response.body()));
//                } else {
//                    Log.e(TAG, "Error response card");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<CategoriesAPI.CardPlain>> call, Throwable t) {
//                Log.e(TAG, "Failed to load cards", t);
//            }
//        });
//    }

//    public void refresh(final int id) {
//        mCardsAPI.getLesson(id).enqueue(new Callback<LessonApi.LessonPlain>() {
//            @Override
//            public void onResponse(Call<LessonApi.LessonPlain> call, Response<LessonApi.LessonPlain> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    try {
//                        Lesson newLesson = map(response.body());
//                        List<Lesson> oldList = mLessons.getValue();
//                        for (int i = 0; i < oldList.size(); i++) {
//                            if (oldList.get(i).getId() == id) {
//                                oldList.set(i, newLesson);
//                            }
//                        }
//                        mLessons.postValue(oldList);
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<LessonApi.LessonPlain> call, Throwable t) {
//                Log.d("Test", "Failed to get ", t);
//            }
//        });
//    }

    public void getNewCategories(int version, LoadDataCallback callback) {
        mCategoriesAPI.getNewCategories(new Version(version)).enqueue(new Callback<List<CategoriesAPI.CategoryPlain>>() {
            @Override
            public void onResponse(Call<List<CategoriesAPI.CategoryPlain>> call,
                                   Response<List<CategoriesAPI.CategoryPlain>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mDecks.postValue(transformDeck(response.body(), callback));
                }
            }

            @Override
            public void onFailure(Call<List<CategoriesAPI.CategoryPlain>> call, Throwable t) {
                Log.d(TAG, "Failed to load decks ", t);
                callback.onError(t);
//                loadStatus.postValue(new LoadStatus(t));
                t.printStackTrace();
            }
        });
    }

    private List<CategoryModel> transformDeck(List<CategoriesAPI.CategoryPlain> body, LoadDataCallback callback) {
        List<CategoryModel> result = new ArrayList<>();
        for (CategoriesAPI.CategoryPlain categoryPlain : body) {
            try {
                CategoryModel deck = map(categoryPlain, callback);
                result.add(deck);
                Log.e(TAG, "Loaded deck" + deck.mId);
            } catch (ParseException e) {
                callback.onError(e);
                e.printStackTrace();
            }
        }
        return result;
    }

    private static CategoryModel map(CategoriesAPI.CategoryPlain categoryPlain, LoadDataCallback callback) throws ParseException {
        return new CategoryModel(
                categoryPlain.id,
                categoryPlain.name,
                categoryPlain.version,
                categoryPlain.adult,
                categoryPlain.accessLevel,
                transformCard(categoryPlain.cards, callback)
        );
    }

    private static CardModel map(CategoriesAPI.CardPlain cardPlain) throws ParseException {
        return new CardModel(
                cardPlain.version,
                cardPlain.id,
                cardPlain.text
        );
    }

    private static List<CardModel> transformCard(List<CategoriesAPI.CardPlain> cardPlains, LoadDataCallback callback) {
        List<CardModel> result = new ArrayList<>();
        for (CategoriesAPI.CardPlain cardPlain : cardPlains) {
            try {
                CardModel card = map(cardPlain);
                result.add(card);
                Log.e(TAG, "Loaded card" + card.mText);
            } catch (ParseException e) {
                callback.onError(e);
                e.printStackTrace();
            }
        }
        return result;
    }

}
