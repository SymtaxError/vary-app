package com.example.vary;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.ArrayList;

@Database(entities = {Card.class, Category.class /* AThirdEntityType.class */}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    public abstract CardDao getCardDao();

    public abstract CategoryDao getCategoryDao();

    private static void addTestData(AppDatabase db) { //TODO DELETE!
        CardDao cardDao = db.getCardDao();
        if (cardDao.getCardsCount() == 0) {

            Category catEasy = new Category();
            catEasy.id = 0;
            catEasy.name = "Простой";
            Category catMedium = new Category();
            catMedium.id = 1;
            catMedium.name = "Средний";
            db.getCategoryDao().insertAll(catMedium, catEasy);


            ArrayList<String> cardsMed = new ArrayList<String>();
            cardsMed.add("заправка");
            cardsMed.add("сленг");
            cardsMed.add("адепт");
            cardsMed.add("трость");
            cardsMed.add("игрушка");
            cardsMed.add("ров");
            cardsMed.add("супруг");
            cardsMed.add("принципиальность");
            cardsMed.add("величина");
            cardsMed.add("проза");
            cardsMed.add("озорник");
            cardsMed.add("смокинг");
            cardsMed.add("впечатлительность");
            cardsMed.add("знахарь");
            cardsMed.add("капельница");
            cardsMed.add("военкомат");
            cardsMed.add("передряга");
            cardsMed.add("поборник");
            cardsMed.add("коэффициент");
            cardsMed.add("редактор");
            cardsMed.add("гангстер");
            cardsMed.add("коллектив");
            cardsMed.add("роскошь");
            cardsMed.add("устье");
            cardsMed.add("перенос");

            int i = 0;
            Card card = new Card();
            card.categoryId = 1;
            for (String element : cardsMed) {
                card.id = i++;
                card.name = element;
                cardDao.insertAll(card);
            }
        }
    }

    private static AppDatabase create(final Context context) {
        AppDatabase db = Room.databaseBuilder(
                context,
                AppDatabase.class,
                "card_repo.db")
                .build();
        addTestData(db);
        return db;
    }
}