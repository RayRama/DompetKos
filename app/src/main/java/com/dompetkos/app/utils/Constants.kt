package com.dompetkos.app.utils

import com.dompetkos.app.R
import com.dompetkos.app.models.Category

object Constants {
    const val APP_ID = "dompetkos-ewmuk"
    const val CLIENT_ID = "11656146194-kqpaghfhg3rknd0eb0hcej2uv22i6m00.apps.googleusercontent.com"
//    const val CLIENT_ID = "11656146194-qqfvjphheg9oganf8phibl0qu3ek3qsc.apps.googleusercontent.com"
//    const val CLIENT_ID = "11656146194-jhbimvrnl6fk954378stnl2m6pv52nkh.apps.googleusercontent.com"
//    const val CLIENT_ID = "11656146194-3vd01906t8j6fh1u1nkr3uau0c2nj2hm.apps.googleusercontent.com"

    var INCOME = "INCOME"
    var EXPENSE = "EXPENSE"
    var categories: ArrayList<Category>? = null
    var DAILY = 0
    var MONTHLY = 1
    var CALENDAR = 2
    var SUMMARY = 3
    var NOTES = 4
    var SELECTED_TAB = 0
    var SELECTED_TAB_STATS = 0
    var SELECTED_STATS_TYPE = INCOME
    fun setCategories() {
        categories = ArrayList()
        categories!!.add(Category("Salary", R.drawable.ic_salary, R.color.category1))
        categories!!.add(Category("Business", R.drawable.ic_business, R.color.category2))
        categories!!.add(Category("Investment", R.drawable.ic_investment, R.color.category3))
        categories!!.add(Category("Loan", R.drawable.ic_loan, R.color.category4))
        categories!!.add(Category("Rent", R.drawable.ic_rent, R.color.category5))
        categories!!.add(Category("Other", R.drawable.ic_other, R.color.category6))
    }

    fun getCategoryDetails(categoryName: String?): Category? {
        for (cat in categories!!) {
            if (cat.categoryName == categoryName) {
                return cat
            }
        }
        return null
    }

    fun getAccountsColor(accountName: String?): Int {
        return when (accountName) {
            "Bank" -> R.color.bank_color
            "Cash" -> R.color.cash_color
            "Card" -> R.color.card_color
            else -> R.color.default_color
        }
    }


}