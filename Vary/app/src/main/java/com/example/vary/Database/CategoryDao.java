package com.example.vary.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CategoryDao {

    // Добавление категории в бд
    @Insert
    void insertAll(Category... categories);

    // Удаление категории из бд
    @Delete
    void delete(Category category);

    // Получение всех категорий из бд
    @Query("SELECT * FROM category")
    List<Category> getAllCategories();

    // Получение количества категорий
    @Query("SELECT COUNT(*) FROM category")
    int getCategoriesCount();

}