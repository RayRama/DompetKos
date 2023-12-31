package com.dompetkos.app.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.bson.types.ObjectId
import java.util.Date

open class Transaction : RealmObject {
    var type: String? = null
    var category: String? = null
    var account: String? = null
    var note: String? = null
    var date: Date? = null
    var amount = 0.0

    @PrimaryKey
    var _id: ObjectId? = ObjectId()
    var transactionId: Long? = null

    constructor()
    constructor(
        type: String?,
        category: String?,
        account: String?,
        note: String?,
        date: Date?,
        amount: Double,
        _id: ObjectId,
        transactionId: Long
    ) {
        this.type = type
        this.category = category
        this.account = account
        this.note = note
        this.date = date
        this.amount = amount
        this._id = _id
        this.transactionId = transactionId
    }

//    fun getType(): String? {
//        return type
//    }
//
//    fun setType(type: String?) {
//        this.type = type
//    }
//
//    fun getCategory(): String? {
//        return category
//    }
//
//    fun setCategory(category: String?) {
//        this.category = category
//    }
//
//    fun getAccount(): String? {
//        return account
//    }
//
//    fun setAccount(account: String?) {
//        this.account = account
//    }
//
//    fun getNote(): String? {
//        return note
//    }
//
//    fun setNote(note: String?) {
//        this.note = note
//    }
//
//    fun getDate(): Date? {
//        return date
//    }
//
//    fun setDate(date: Date?) {
//        this.date = date
//    }
//
//    fun getAmount(): Double {
//        return amount
//    }
//
//    fun setAmount(amount: Double) {
//        this.amount = amount
//    }
//
//    fun getId(): Long {
//        return id
//    }
//
//    fun setId(id: Long) {
//        this.id = id
//    }
//
//    override fun toString(): String {
//        return "Transaction{" +
//                "type='" + type + '\'' +
//                ", category='" + category + '\'' +
//                ", account='" + account + '\'' +
//                ", note='" + note + '\'' +
//                ", date=" + date +
//                ", amount=" + amount +
//                ", id=" + id +
//                '}'
//    }
}