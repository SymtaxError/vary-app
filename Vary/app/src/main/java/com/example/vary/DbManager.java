package com.example.vary;


import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Менеджер, управляющий базой данных. Занимается чтением/записью данных в выделенном потоке.
 * Все callback'и вызываются в потоке исполнения (не переводятся в UI-thread).
 */
class DbManager {
    private static final int VERSION = 1;

    @SuppressLint("StaticFieldLeak")
    private static final DbManager INSTANCE = new DbManager();

    private static final String TEXT_COLUMN = "TEXT_COLUMN";
    private static final String TABLE_NAME = "TABLE_NAME";
    private static final String DB_NAME = "MyDatabase.db";

    static DbManager getInstance(Context context) {
        INSTANCE.context = context.getApplicationContext();
        return INSTANCE;
    }

    private final Executor executor = Executors.newSingleThreadExecutor();
    private Context context;
    private SQLiteDatabase database;

    void getCount(final CountListener countListener) {
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

    private void checkInitialized() {
        if (database != null) {
            return;
        }

        SQLiteOpenHelper helper = new SQLiteOpenHelper(context, DB_NAME, null, VERSION +2) {

            @Override
            public void onCreate(SQLiteDatabase db) {
                createDatabase(db);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            }
        };

        database = helper.getWritableDatabase();
    }

    private void createDatabase(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE '" + TABLE_NAME + "' (ID INTEGER PRIMARY KEY, " + TEXT_COLUMN + " TEXT NOT NULL)");
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
}

