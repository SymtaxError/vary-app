package com.example.vary.UI;

import com.example.vary.Models.CategoryModel;

import java.util.List;

public interface SetDataCallback {
    void onLoaded(List<CategoryModel> categories, LoadDataCallback callback);
    void onLoaded(Integer version);
    void onLoaded(Throwable t);
}
