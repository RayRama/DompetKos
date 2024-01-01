package com.dompetkos.app.viewmodels

//import io.realm.kotlin.Realm
//import io.realm.kotlin.RealmConfiguration
//import io.realm.kotlin.UpdatePolicy
//import io.realm.kotlin.query.RealmResults

import android.app.Application
import android.content.ContentValues
import android.content.Context.MODE_PRIVATE
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.dompetkos.app.models.Transaction
import com.dompetkos.app.utils.Constants
import com.dompetkos.app.utils.Helper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import io.realm.ImportFlag
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.Calendar
import java.util.Date
import com.google.api.services.drive.model.File as DriveFile


class MainViewModel(application: Application) : AndroidViewModel(application) {
    var transactions = MutableLiveData<RealmResults<Transaction>?>()
    var categoriesTransactions = MutableLiveData<RealmResults<Transaction>?>()
    var totalIncome = MutableLiveData<Double?>()
    var totalExpense = MutableLiveData<Double?>()
    var totalAmount = MutableLiveData<Double?>()
    var realm: Realm? = null
    var calendar: Calendar? = null

    var isUploading = MutableLiveData<Boolean>()
    var lastBackupDate = MutableLiveData<String>()

    private val EXPORT_REALM_FILE_NAME = "dompetkos.realm"
    private val IMPORT_REALM_FILE_NAME =
        "default.realm" // Eventually replace this if you're using a custom db name

    init {
        Realm.init(application)
        setupDatabase()
    }

    private fun getDriveService(): Drive? {
        GoogleSignIn.getLastSignedInAccount(getApplication())?.let { googleAccount ->
            val credential = GoogleAccountCredential.usingOAuth2(
                getApplication(),
                listOf(DriveScopes.DRIVE_FILE)
            )
            credential.selectedAccount = googleAccount.account!!
            return Drive.Builder(
                NetHttpTransport(),
                JacksonFactory.getDefaultInstance(),
                credential
            )
                .setApplicationName("DompetKos")
                .build()
        }
        return null
    }

//    private fun createFolder(): String? {
//        val driveService = getDriveService()
//        var folderId: String? = null
//        if (driveService != null) {
//            try {
//                val folderMetadata = DriveFile()
//                    .setMimeType("application/vnd.google-apps.folder")
//                    .setName("DompetKos")
//                val googleFile = driveService.files().create(folderMetadata)
//                    .setFields("id")
//                    .execute()
//                folderId = googleFile.id
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//        return folderId
//    }

    private fun createFolder(): String? {
        val driveService = getDriveService()
        var folderId: String? = null
        if (driveService != null) {
            try {
                // Check if the folder 'DompetKos' already exists
                val folderList = driveService.files().list()
                    .setQ("name='DompetKos' and mimeType='application/vnd.google-apps.folder' and trashed=false")
                    .execute()

                if (folderList.files.isNotEmpty()) {
                    // Folder already exists, get its ID
                    folderId = folderList.files[0].id
                } else {
                    // Folder doesn't exist, create a new one
                    val folderMetadata = DriveFile()
                        .setMimeType("application/vnd.google-apps.folder")
                        .setName("DompetKos")
                    val googleFile = driveService.files().create(folderMetadata)
                        .setFields("id")
                        .execute()
                    folderId = googleFile.id
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return folderId
    }

    private fun uploadFile(file: File, type: String?, folderId: String? = null): DriveFile? {
        getDriveService()?.let { driveService ->
            try {
                val gfile = DriveFile()
                gfile.name = file.name
                folderId?.let {
                    gfile.parents = listOf(folderId)
                }
                val mediaContent = FileContent(type, file)
                val result = driveService.files().create(gfile, mediaContent).execute()

                if (result == null) {
                    Log.e(ContentValues.TAG, "Error while trying to upload file")
                    return null
                }

                return result
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
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

    fun checkIsSignedIn(application: Application) {
        val account = GoogleSignIn.getLastSignedInAccount(application)
        if (account != null) {
            Toast.makeText(application, "Signed in as ${account.displayName}", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(application, "Not signed in", Toast.LENGTH_SHORT).show()
        }
    }

    fun saveLastBackup(date: String) {
        val sharedPreferences = getApplication<Application>().getSharedPreferences(
            "com.dompetkos.app",
            MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString("lastBackup", date)
        editor.apply()
    }

    fun getLastBackup(): String? {
        val sharedPreferences = getApplication<Application>().getSharedPreferences(
            "com.dompetkos.app",
            MODE_PRIVATE
        )
        val lastBackup = sharedPreferences.getString("lastBackup", null)
        lastBackupDate.value = lastBackup.toString()
        return null
    }

    fun backup(application: Application) {
        val exportRealmFile: File
        try {
            val externalStorageRootDir = application.getExternalFilesDir(null)
            val folder = File(externalStorageRootDir, "dompetkos")

            // Memeriksa dan membuat direktori induk jika belum ada
            if (!folder.exists()) {
                val success = folder.mkdirs()
                if (!success) {
                    // Tidak berhasil membuat direktori, Anda dapat menangani kondisi ini
                    // seperti memberikan pesan kesalahan atau tindakan lain yang sesuai
                    Toast.makeText(application, "Failed to create directory", Toast.LENGTH_SHORT)
                        .show()
                    return
                }
            }

            exportRealmFile = File(folder, EXPORT_REALM_FILE_NAME)

            // if backup file already exists, delete it
            exportRealmFile.delete()

            // copy current realm to backup file
            realm!!.writeCopyTo(exportRealmFile)

            val inputStream = FileInputStream(exportRealmFile)
//            val folderId = "com.dompetkos.app"
            val account = GoogleSignIn.getLastSignedInAccount(application)

            if (account != null) {
                isUploading.value = true

                GlobalScope.launch(Dispatchers.IO) {
                    val driveService = getDriveService()
                    val folderId = createFolder()

                    if (folderId != null) {
                        // Folder exists, proceed with backup

                        // Check if the file already exists in the folder
                        val fileList = driveService?.files()?.list()
                            ?.setQ("'$folderId' in parents and trashed=false")?.execute()
                        val files = fileList?.files
                        var fileExists = false
                        files?.forEach { file ->
                            if (file.name == EXPORT_REALM_FILE_NAME) {
                                fileExists = true
                                // Found the file, delete it before upload
                                driveService.files()?.delete(file.id)?.execute()
                            }
                        }

                        // Upload the file if it doesn't exist or after deleting the existing one
                        if (!fileExists) {
                            val uploadFile = uploadFile(exportRealmFile, "application/octet-stream", folderId)

                            uploadFile?.let {
                                withContext(Dispatchers.Main) {
                                    isUploading.value = false
                                    saveLastBackup(Helper.formatDateWithTime(Calendar.getInstance().time))
                                    Toast.makeText(application, "File uploaded to Folder DompetKos on Drive", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            // File exists, perform upload after deleting the existing file
                            val uploadFile = uploadFile(exportRealmFile, "application/octet-stream", folderId)

                            uploadFile?.let {
                                withContext(Dispatchers.Main) {
                                    isUploading.value = false
                                    saveLastBackup(Helper.formatDateWithTime(Calendar.getInstance().time))
                                    Toast.makeText(application, "File uploaded and replaced", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } else {
                        // Folder doesn't exist, handle accordingly (create folder or show error message)
                        withContext(Dispatchers.Main) {
                            isUploading.value = false
                            Toast.makeText(application, "Failed to create folder", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            } else {
                Toast.makeText(application, "Not signed in", Toast.LENGTH_SHORT).show()
            }

            // Provide notification or message to user about successful backup
//            val msg = "File exported to Path: ${folder.absolutePath}/$EXPORT_REALM_FILE_NAME"
//            Toast.makeText(application, msg, Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun setupDatabase() {
        val config = RealmConfiguration.Builder()
            .allowQueriesOnUiThread(true)
            .allowWritesOnUiThread(true)
            .build()

        realm = Realm.getInstance(config)

    }
}