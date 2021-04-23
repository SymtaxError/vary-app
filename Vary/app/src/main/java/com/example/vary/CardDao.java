package com.example.vary;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CardDao {

    // Добавление карточки
    @Insert
    void insertAll(Card... cards);

    // Удаление карточки
    @Delete
    void delete(Card person);

//    // Получение всех карточек TODO delete
//    @Query("SELECT * FROM card")
//    List<Card> getAllCards();
//
    // Получение количества карточек  TODO delete
    @Query("SELECT COUNT(*) FROM card")
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
    @Query("SELECT * FROM card WHERE categoryId = :categoryId")
    List<Card> getAllCategoryCards(int categoryId);


}