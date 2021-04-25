package com.example.vary.Network;

import com.squareup.moshi.Json;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.GET;
import retrofit2.http.POST;
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

}
