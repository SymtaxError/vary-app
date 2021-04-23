package com.example.vary;

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
        public UUID id;
        public String text;
        public int version;
    }

    class CategoryPlain {
        public UUID id;
        public int version;
        public boolean adult;
        public int accessLevel;
        public String name;
        List<CardPlain> cards;
    }

//    @GET("/check") //передача версии ???
//    Call<List<CategoryPlain>> getNewCategories(@Query("version") int version);

    @POST("/check")
    Call<List<CategoryPlain>> getNewCategories(@Body Version version);

}
