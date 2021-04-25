package com.example.vary.Database;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Card {
    @PrimaryKey
    @NonNull
    int id;
    String name;
    int version;
    int categoryId;
    int countUsages;
}