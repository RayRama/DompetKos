package com.dompetkos.app.views.activites

import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dompetkos.app.R
import com.dompetkos.app.databinding.ActivityMainBinding
import com.dompetkos.app.utils.Constants
import com.dompetkos.app.viewmodels.MainViewModel
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
    var viewModel: MainViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show()
            }
            transaction.commit()
            true
        }
    }

    val getTransactions: Unit
        get() {
            viewModel!!.getTransactions(calendar)
        }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}