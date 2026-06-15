package com.example.slatkishka.models;

import java.util.List;

// модел за листање на категориите во RecyclerView

public class CategoryModel {
    private String categoryTitle;
    private List<BusinessModel> businessList;

    public CategoryModel(String categoryTitle, List<BusinessModel> businessList) {
        this.categoryTitle = categoryTitle;
        this.businessList = businessList;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public List<BusinessModel> getBusinessList() {
        return businessList;
    }
}