package com.example.vary.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.vary.Models.CardModel;
import com.example.vary.Models.CategoryModel;

@Database(entities = {CardModel.class, CategoryModel.class}, version = 1)

/*
thJavac
/Users/sntgl/StudioProjects/Symtax-Error-Android/Vary/app/src/main/java/com/example/vary/Database/AppDatabase.java:13: warning: Schema export directory is not provided to the annotation processor so we cannot export the schema. You can either provide `room.schemaLocation` annotation processor argument OR set exportSchema to false.
public abstract class AppDatabase extends RoomDatabase {
                ^
Note: Some input files use or override a deprecated API.
Note: Recompile with -Xlint:deprecation for details.
1 warning
 */
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