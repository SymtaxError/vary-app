package com.example.vary.Database;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

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
//    private static int categoriesVersion = -1;

    private static final String TEXT_COLUMN = "TEXT_COLUMN";
    private static final String TABLE_NAME = "TABLE_NAME";
    private static final String DB_NAME = "MyDatabase.db";
    private CategoryRepositoryListener categoryRepositoryListener;
    private CardRepositoryListener cardRepositoryListener;
//    private interface repositoryListener

//    public interface VersionCallback {
//        void onVersionGot(int version);
//    }

    public DbManager() {
//
//        INSTANCE.context = context.getApplicationContext();
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
//    private SQLiteDatabase database;

    public void getCount(final CountListener countListener) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                int cardCount = AppDatabase.getInstance(context).getCardDao().getCardsCount();
                int catCount = AppDatabase.getInstance(context).getCategoryDao().getCategoriesCount();
                countListener.onGetCount(cardCount, catCount);
//                getCardsCount(countListener);
//                insertInternal(text);
//                CardDao cd = CardDao.getCardCount();
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


//    void readAll(final ReadAllListener<String> listener) {
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
//                readAllRoom(listener);
////                readAllInternal(listener);
//            }
//        });
//    }

//    void clean() {
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
////                cleanInternal();
//                cleanRoom();
//            }
//        });
//    }

//    private void cleanRoom() {
//        SimpleEntityDao dao = AppDatabase.getInstance(context).getDao();
//        dao.delete(dao.getAllEntities().toArray(new SimpleEntity[0]));
//    }

//    private void checkInitialized() {
//        if (database != null) {
//            return;
//        }
//
//        SQLiteOpenHelper helper = new SQLiteOpenHelper(context, DB_NAME, null, VERSION) {
//
//            @Override
//            public void onCreate(SQLiteDatabase db) {
//                createDatabase(db);
//            }
//
//            @Override
//            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//            }
//        };
//
//        database = helper.getWritableDatabase();
//    }

//    private void createDatabase(SQLiteDatabase db) {
//        db.execSQL("CREATE TABLE '" + TABLE_NAME + "' (ID INTEGER PRIMARY KEY, " + TEXT_COLUMN + " TEXT NOT NULL)");
//    }

    public void setCardRepositoryListener(CardRepositoryListener cardRepositoryListener) {
        this.cardRepositoryListener = cardRepositoryListener;
    }

//    public void getCategoriesVersion(VersionCallback callback) {
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
//                List<CategoryModel> allCategories = AppDatabase.getInstance(context).getCategoryDao().getAllCategories();
//                int categoryVersion = 0;
//                for (CategoryModel category: allCategories) {
//                    if (category.getVersion() > categoryVersion)
//                        categoryVersion = category.getVersion();
//                }
//                categoriesVersion = categoryVersion;
//                callback.onVersionGot(categoryVersion);
//            }
//        });
//
//    }

    public void update(List<CategoryModel> newCategories) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<CategoryModel> oldCategories = AppDatabase.getInstance(context).getCategoryDao().getAllCategories();
                for (CategoryModel newCategory : newCategories) {
//                    CategoryModel oldCategory = null;
//                    for (CategoryModel oldCategoryIter : oldCategories) {
//                        if (newCategory.getName().equals(oldCategoryIter.getName())) {
//                            oldCategory = oldCategoryIter;
//                            break;
//                        }
//                    }
//                    if (oldCategory == null)
//                        AppDatabase.getInstance(context).getCategoryDao().insertAll(newCategory);
//                    else
//                        AppDatabase.getInstance(context).getCategoryDao().updateAll(newCategory);
                    AppDatabase.getInstance(context).getCategoryDao().insertAll(newCategory);

                    List<CardModel> cards = newCategory.getCards();
                    CardModel[] objects = cards.toArray(new CardModel[0]);
                    AppDatabase.getInstance(context).getCardDao().insertAll(objects);
                }
            }
        });

    }


//    private void insertInternal(String text) {
//        checkInitialized();
//
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(TEXT_COLUMN, text);
//        long insert = database.insert(TABLE_NAME, null, contentValues);
////        database.execSQL("INSERT INTO " + TABLE_NAME + " (" + TEXT_COLUMN + ") VALUES (?)", new Object[]{text});
//    }

//    private void insertRoom(String text) {
//        SimpleEntity simpleEntity = new SimpleEntity();
//        simpleEntity.text = text;
//        AppDatabase.getInstance(context).getDao().insertAll(simpleEntity);
//    }

//    private void readAllRoom(final ReadAllListener<String> listener) {
//        List<SimpleEntity> list = AppDatabase.getInstance(context).getDao().getAllEntities();
//        ArrayList<String> strings = new ArrayList<>();
//        for (SimpleEntity simpleEntity : list) {
//            strings.add(simpleEntity.text);
//        }
//        listener.onReadAll(strings);
//    }


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

