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

    @Query("SELECT COUNT(*) FROM cardmodel")
    int getCardsCount();

    // Получение всех карточек определенной категории
    @Query("SELECT * FROM cardmodel WHERE mCategory = :category")
    List<CardModel> getAllCategoryCards(String category);


}