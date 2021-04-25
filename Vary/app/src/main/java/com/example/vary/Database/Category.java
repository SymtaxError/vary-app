package com.example.vary.Database;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Category {
    @PrimaryKey
    @NonNull
    int id;
    String name;
    Boolean isAdult;
    int version;
    int accessLevel;
}