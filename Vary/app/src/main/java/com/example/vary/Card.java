package com.example.vary;


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