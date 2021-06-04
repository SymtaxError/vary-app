package com.example.vary.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.vary.Models.CardModel;

import java.util.List;

@Dao
public interface CardModelDao {

    // Добавление карточки
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(CardModel... cards);

// --Commented out by Inspection START (04.06.2021, 21:47):
//    @Query("SELECT COUNT(*) FROM cardmodel")
//    int getCardsCount();
// --Commented out by Inspection STOP (04.06.2021, 21:47)

    // Получение всех карточек определенной категории
    @Query("SELECT * FROM cardmodel WHERE mCategory = :category")
    List<CardModel> getAllCategoryCards(String category);


}