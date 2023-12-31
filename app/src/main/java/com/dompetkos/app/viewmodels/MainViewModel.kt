package com.dompetkos.app.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.dompetkos.app.models.Transaction
import com.dompetkos.app.utils.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import io.realm.ImportFlag
import io.realm.Realm
//import io.realm.kotlin.Realm
//import io.realm.kotlin.RealmConfiguration
//import io.realm.kotlin.UpdatePolicy
//import io.realm.kotlin.query.RealmResults
import io.realm.RealmConfiguration
import io.realm.RealmResults
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration
import io.realm.mongodb.Credentials
import io.realm.mongodb.sync.SyncConfiguration
import java.io.File
import java.util.Calendar
import java.util.Date

class MainViewModel(application: Application) : AndroidViewModel(application) {
    var transactions = MutableLiveData<RealmResults<Transaction>?>()
    var categoriesTransactions = MutableLiveData<RealmResults<Transaction>?>()
    var totalIncome = MutableLiveData<Double?>()
    var totalExpense = MutableLiveData<Double?>()
    var totalAmount = MutableLiveData<Double?>()
    var realm: Realm? = null
    var calendar: Calendar? = null

    init {
        Realm.init(application)
        setupDatabase()
    }

    fun getTransactions(calendar: Calendar?, type: String?) {
        this.calendar = calendar
        calendar!![Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        var newTransactions: RealmResults<Transaction>? = null
        if (Constants.SELECTED_TAB_STATS == Constants.DAILY) {
            newTransactions = realm!!.where(Transaction::class.java)
                .greaterThanOrEqualTo("date", calendar.time)
                .lessThan("date", Date(calendar.time.time + 24 * 60 * 60 * 1000))
                .equalTo("type", type)
                .findAll()
        } else if (Constants.SELECTED_TAB_STATS == Constants.MONTHLY) {
            calendar[Calendar.DAY_OF_MONTH] = 0
            val startTime = calendar.time
            calendar.add(Calendar.MONTH, 1)
            val endTime = calendar.time
            newTransactions = realm!!.where(Transaction::class.java)
                .greaterThanOrEqualTo("date", startTime)
                .lessThan("date", endTime)
                .equalTo("type", type)
                .findAll()
        }
        categoriesTransactions.value = newTransactions
    }

    fun getTransactions(calendar: Calendar?) {
        this.calendar = calendar
        calendar!![Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        var income = 0.0
        var expense = 0.0
        var total = 0.0
        var newTransactions: RealmResults<Transaction>? = null
        if (Constants.SELECTED_TAB == Constants.DAILY) {
            // Select * from transactions
            // Select * from transactions where id = 5
            newTransactions = realm!!.where(Transaction::class.java)
                .greaterThanOrEqualTo("date", calendar.time)
                .lessThan("date", Date(calendar.time.time + 24 * 60 * 60 * 1000))
                .findAll()
            income = realm!!.where(Transaction::class.java)
                .greaterThanOrEqualTo("date", calendar.time)
                .lessThan("date", Date(calendar.time.time + 24 * 60 * 60 * 1000))
                .equalTo("type", Constants.INCOME)
                .sum("amount")
                .toDouble()
            expense = realm!!.where(Transaction::class.java)
                .greaterThanOrEqualTo("date", calendar.time)
                .lessThan("date", Date(calendar.time.time + 24 * 60 * 60 * 1000))
                .equalTo("type", Constants.EXPENSE)
                .sum("amount")
                .toDouble()
            total = realm!!.where(Transaction::class.java)
                .greaterThanOrEqualTo("date", calendar.time)
                .lessThan("date", Date(calendar.time.time + 24 * 60 * 60 * 1000))
                .sum("amount")
                .toDouble()
        } else if (Constants.SELECTED_TAB == Constants.MONTHLY) {
            calendar[Calendar.DAY_OF_MONTH] = 0
            val startTime = calendar.time
            calendar.add(Calendar.MONTH, 1)
            val endTime = calendar.time
            newTransactions = realm!!.where(Transaction::class.java)
                .greaterThanOrEqualTo("date", startTime)
                .lessThan("date", endTime)
                .findAll()
            income = realm!!.where(Transaction::class.java)
                .greaterThanOrEqualTo("date", startTime)
                .lessThan("date", endTime)
                .equalTo("type", Constants.INCOME)
                .sum("amount")
                .toDouble()
            expense = realm!!.where(Transaction::class.java)
                .greaterThanOrEqualTo("date", startTime)
                .lessThan("date", endTime)
                .equalTo("type", Constants.EXPENSE)
                .sum("amount")
                .toDouble()
            total = realm!!.where(Transaction::class.java)
                .greaterThanOrEqualTo("date", startTime)
                .lessThan("date", endTime)
                .sum("amount")
                .toDouble()
        }
        totalIncome.value = income
        totalExpense.value = expense
        totalAmount.value = total
        transactions.value = newTransactions
        //        RealmResults<Transaction> newTransactions = realm.where(Transaction.class)
//                .equalTo("date", calendar.getTime())
//                .findAll();
    }

    fun addTransaction(transaction: Transaction) {
        realm!!.executeTransaction { realm ->
            realm.copyToRealmOrUpdate(transaction)
        }
    }

    fun editTransaction(transaction: Transaction) {
        realm!!.executeTransaction { realm ->
            realm.copyToRealmOrUpdate(transaction, ImportFlag.CHECK_SAME_VALUES_BEFORE_SET)
        }
    }

    fun deleteTransaction(transaction: Transaction?) {
        realm!!.beginTransaction()
        transaction!!.deleteFromRealm()
        realm!!.commitTransaction()
        getTransactions(calendar)
    }

    fun checkPath() {
        var pathR = RealmConfiguration.Builder().build().path
        Log.d("Realm Path", pathR)
    }

    fun copyRealmFile() {

        val externalPath = File(
            "/storage/emulated/0/Android/data/com.dompetkos.app/files/" + "default.realm"
        )

        realm!!.writeCopyTo(externalPath)

        Log.d("Realm Path", externalPath.toString())
    }

    fun setupDatabase() {
        val config = RealmConfiguration.Builder()
            .allowQueriesOnUiThread(true)
            .allowWritesOnUiThread(true)
            .build()

        realm = Realm.getInstance(config)

    }
}