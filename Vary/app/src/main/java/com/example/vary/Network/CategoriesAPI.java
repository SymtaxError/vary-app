package com.example.vary.Network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CategoriesAPI {
    class CardPlain {
        public String id;
        public String name;
        public int version;
    }

    class CategoryPlain {
        public String name;
        public int version;
        public int accessLevel;
        List<CardPlain> cards;
    }

    @GET("/categories")
    Call<List<CategoryPlain>> getNewCategories(@Query("version") int version);

    @GET("/categories/version")
    Call<Integer> getVersion();

}
