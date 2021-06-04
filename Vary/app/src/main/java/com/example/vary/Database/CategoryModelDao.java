package com.example.vary.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.vary.Models.CategoryModel;

import java.util.List;

@Dao
public interface CategoryModelDao {
    // Добавление категории в бд
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(CategoryModel... categories);

    // Получение всех категорий из бд
    @Query("SELECT * FROM categorymodel")
    List<CategoryModel> getAllCategories();

    // Получение количества категорий
    @Query("SELECT COUNT(*) FROM categorymodel")
    int getCategoriesCount();
}