package com.dompetkos.app.models

class Account {
    var accountAmount = 0.0
    var accountName: String? = null

    constructor()
    constructor(accountAmount: Double, accountName: String?) {
        this.accountAmount = accountAmount
        this.accountName = accountName
    }

//    fun getAccountAmount(): Double {
//        return accountAmount
//    }
//
//    fun setAccountAmount(accountAmount: Double) {
//        this.accountAmount = accountAmount
//    }
//
//    fun getAccountName(): String? {
//        return accountName
//    }
//
//    fun setAccountName(accountName: String?) {
//        this.accountName = accountName
//    }
}