package com.dompetkos.app.views.activites

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.dompetkos.app.R
import com.dompetkos.app.databinding.ActivityMainBinding
import com.dompetkos.app.utils.Constants
import com.dompetkos.app.viewmodels.MainViewModel
import com.dompetkos.app.views.fragments.SettingsFragment
import com.dompetkos.app.views.fragments.StatsFragment
import com.dompetkos.app.views.fragments.TransactionsFragment
import java.util.Calendar


class MainActivity : AppCompatActivity() {
    var binding: ActivityMainBinding? = null
    var calendar: Calendar? = null
    /*
    0 = Daily
    1 = Monthly
    2 = Calendar
    3 = Summary
    4 = Notes
     */
    private val REQUEST_EXTERNAL_STORAGE: Int = 1
    private val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private lateinit var progressBar: ProgressBar

    var viewModel: MainViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkStoragePermissions(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        setSupportActionBar(binding!!.toolBar)
        supportActionBar!!.title = "Transactions"
        Constants.setCategories()
        calendar = Calendar.getInstance()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.content, TransactionsFragment())
        transaction.commit()
        binding!!.bottomNavigationView.setOnItemSelectedListener { item ->
            val transaction = supportFragmentManager.beginTransaction()
            if (item.itemId == R.id.transactions) {
                supportFragmentManager.popBackStack()
            } else if (item.itemId == R.id.stats) {
                transaction.replace(R.id.content, StatsFragment())
                transaction.addToBackStack(null)
            } else if (item.itemId == R.id.accounts) {
                Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show()
            } else if (item.itemId == R.id.more) {
                transaction.replace(R.id.content, SettingsFragment())
                transaction.addToBackStack(null)
            }
            transaction.commit()
            true
        }

        progressBar = findViewById(R.id.loadingProgressBar)

        viewModel!!.isUploading.observe(this) { isUploading ->
            if (isUploading) {
                progressBar.visibility = ProgressBar.VISIBLE
            } else {
                progressBar.visibility = ProgressBar.GONE
            }
        }
    }

    val getTransactions: Unit
        get() {
            viewModel!!.getTransactions(calendar)
        }

    private fun checkStoragePermissions(activity: Activity) {
        // Check if we have read and write permissions
        val readPermission = ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val writePermission = ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (readPermission != PackageManager.PERMISSION_GRANTED ||
            writePermission != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
                Log.d("MainActivity", "Permission granted")
            } else {
//                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                Log.d("MainActivity", "Permission denied")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}