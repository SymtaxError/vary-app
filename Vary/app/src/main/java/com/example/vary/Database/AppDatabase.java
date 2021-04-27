package com.example.vary.Database;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.vary.Models.CardModel;
import com.example.vary.Models.CategoryModel;

import java.util.ArrayList;

@Database(entities = {CardModel.class, CategoryModel.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }


    public abstract CardModelDao getCardDao();

    public abstract CategoryModelDao getCategoryDao();


    private static AppDatabase create(final Context context) {
        return Room.databaseBuilder(
                context,
                AppDatabase.class,
                "card_repo.db")
                .build();
    }
}