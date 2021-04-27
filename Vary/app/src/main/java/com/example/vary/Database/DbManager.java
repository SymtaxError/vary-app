package com.example.vary.Database;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import com.example.vary.Models.CardModel;
import com.example.vary.Models.CategoryModel;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Менеджер, управляющий базой данных. Занимается чтением/записью данных в выделенном потоке.
 * Все callback'и вызываются в потоке исполнения (не переводятся в UI-thread).
 */
public class DbManager {
    private static int VERSION = 1;

    @SuppressLint("StaticFieldLeak")
    private static DbManager INSTANCE = null;

    private CategoryRepositoryListener categoryRepositoryListener;
    private CardRepositoryListener cardRepositoryListener;

    public DbManager() {
    }

    public static synchronized DbManager getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DbManager();
            INSTANCE.context = context.getApplicationContext();
            VERSION = AppDatabase.getInstance(context).getOpenHelper().getReadableDatabase().getVersion();
            Log.println(Log.DEBUG, "DB", "VERSION " + VERSION);
        }
        return INSTANCE;
    }

    public void setCategoryRepositoryListener(CategoryRepositoryListener listener) {
        this.categoryRepositoryListener = listener;
    }

    private final Executor executor = Executors.newSingleThreadExecutor();
    private Context context;

    public void getCount(final CountListener countListener) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                int cardCount = AppDatabase.getInstance(context).getCardDao().getCardsCount();
                int catCount = AppDatabase.getInstance(context).getCategoryDao().getCategoriesCount();
                countListener.onGetCount(cardCount, catCount);
            }
        });
    }

    public void getCategoriesNoCards() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<CategoryModel> allCategories = AppDatabase.getInstance(context).getCategoryDao().getAllCategories();
                categoryRepositoryListener.onGetCategoriesNoCards(allCategories);
            }
        });
    }

    public void getCards(String categoryName) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<CardModel> allCards = AppDatabase.getInstance(context).getCardDao().getAllCategoryCards(categoryName);
                cardRepositoryListener.onGetCards(allCards);
            }
        });
    }

    public void setCardRepositoryListener(CardRepositoryListener cardRepositoryListener) {
        this.cardRepositoryListener = cardRepositoryListener;
    }


    public void update(List<CategoryModel> newCategories) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                for (CategoryModel newCategory : newCategories) {
                    AppDatabase.getInstance(context).getCategoryDao().insertAll(newCategory);
                    List<CardModel> cards = newCategory.getCards();
                    CardModel[] objects = cards.toArray(new CardModel[0]);
                    AppDatabase.getInstance(context).getCardDao().insertAll(objects);
                }
            }
        });

    }


    public interface ReadAllListener<T> {
        void onReadAll(final Collection<T> allItems);
    }

    public interface CountListener {
        void onGetCount(int cardCount, int catCount);
    }

    public interface CategoryRepositoryListener {
        void onGetCategoriesNoCards(List<CategoryModel> categoryModels);
    }

    public interface CardRepositoryListener {
        void onGetCards(List<CardModel> cardsModel);
    }
}

