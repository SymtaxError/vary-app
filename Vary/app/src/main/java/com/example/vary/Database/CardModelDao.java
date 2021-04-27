package com.example.vary.Database;

import androidx.room.Dao;
import androidx.room.Delete;
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

    // Удаление карточки
    @Delete
    void delete(CardModel person);

//    // Получение всех карточек TODO delete
//    @Query("SELECT * FROM card")
//    List<Card> getAllCards();
//
    // Получение количества карточек  TODO delete
    @Query("SELECT COUNT(*) FROM cardmodel")
    int getCardsCount();
//
//    // Получение количества карточек из определенной категории TODO delete
//    @Query("SELECT COUNT(*) FROM card WHERE categoryId = :categoryId")
//    int getCardsCategoryCount(int categoryId);
//
//    // Получение карточки с определенным id TODO delete
//    @Query("SELECT * FROM card WHERE id = :id")
//    Card getCardById(int id);

    // Получение всех карточек определенной категории
    @Query("SELECT * FROM cardmodel WHERE mCategory = :category")
    List<CardModel> getAllCategoryCards(String category);


}