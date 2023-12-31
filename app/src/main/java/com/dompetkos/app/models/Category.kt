package com.dompetkos.app.models

class Category {
    var categoryName: String? = null
    var categoryImage = 0
    var categoryColor = 0

    constructor()
    constructor(categoryName: String?, categoryImage: Int, categoryColor: Int) {
        this.categoryName = categoryName
        this.categoryImage = categoryImage
        this.categoryColor = categoryColor
    }

//    fun getCategoryName(): String? {
//        return categoryName
//    }
//
//    fun setCategoryName(categoryName: String?) {
//        this.categoryName = categoryName
//    }
//
//    fun getCategoryImage(): Int {
//        return categoryImage
//    }
//
//    fun setCategoryImage(categoryImage: Int) {
//        this.categoryImage = categoryImage
//    }
//
//    fun getCategoryColor(): Int {
//        return categoryColor
//    }
//
//    fun setCategoryColor(categoryColor: Int) {
//        this.categoryColor = categoryColor
//    }
}