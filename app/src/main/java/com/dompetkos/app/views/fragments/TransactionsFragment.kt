package com.dompetkos.app.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dompetkos.app.adapters.TransactionsAdapter
import com.dompetkos.app.databinding.FragmentTransactionsBinding
import com.dompetkos.app.utils.Constants
import com.dompetkos.app.utils.Helper
import com.dompetkos.app.viewmodels.MainViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import java.util.Calendar

class TransactionsFragment : Fragment() {

    var binding: FragmentTransactionsBinding? = null
    var myCalendar: Calendar? = null

    /*
    0 = Daily
    1 = Monthly
    2 = Calendar
    3 = Summary
    4 = Notes
     */
    var viewModel: MainViewModel? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransactionsBinding.inflate(inflater)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        myCalendar = Calendar.getInstance()
        updateDate()
        binding!!.nextDateBtn.setOnClickListener { c: View? ->
            if (Constants.SELECTED_TAB == Constants.DAILY) {
                myCalendar!!.add(Calendar.DATE, 1)
            } else if (Constants.SELECTED_TAB == Constants.MONTHLY) {
                myCalendar!!.add(Calendar.MONTH, 1)
            }
            updateDate()
        }
        binding!!.previousDateBtn.setOnClickListener { c: View? ->
            if (Constants.SELECTED_TAB == Constants.DAILY) {
                myCalendar!!.add(Calendar.DATE, -1)
            } else if (Constants.SELECTED_TAB == Constants.MONTHLY) {
                myCalendar!!.add(Calendar.MONTH, -1)
            }
            updateDate()
        }
        binding!!.floatingActionButton.setOnClickListener { c: View? ->
            AddTransactionFragment().show(
                parentFragmentManager, null
            )
//            viewModel!!.checkPath()

//            viewModel!!.copyRealmFile()
            // copy files from checkPath to internal storage



//            Log.d("Path", "path")
        }


        binding!!.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.text == "Monthly") {
                    Constants.SELECTED_TAB = 1
                    updateDate()
                } else if (tab.text == "Daily") {
                    Constants.SELECTED_TAB = 0
                    updateDate()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        binding!!.transactionsList.layoutManager = LinearLayoutManager(context)
        viewModel!!.transactions.observe(viewLifecycleOwner) { transactions ->
            val transactionsAdapter = TransactionsAdapter(activity, transactions)
            binding!!.transactionsList.adapter = transactionsAdapter
            if (transactions?.size!! > 0) {
                binding!!.emptyState.visibility = View.GONE
            } else {
                binding!!.emptyState.visibility = View.VISIBLE
            }
        }
        viewModel!!.totalIncome.observe(viewLifecycleOwner) { aDouble ->
            binding!!.incomeLbl.text = aDouble.toString()
        }
        viewModel!!.totalExpense.observe(viewLifecycleOwner) { aDouble ->
            binding!!.expenseLbl.text = aDouble.toString()
        }
        viewModel!!.totalAmount.observe(viewLifecycleOwner) { aDouble ->
            binding!!.totalLbl.text = aDouble.toString()
        }
        viewModel!!.getTransactions(myCalendar)
        return binding!!.root
    }

    fun updateDate() {
        if (Constants.SELECTED_TAB == Constants.DAILY) {
            binding!!.currentDate.text = Helper.formatDate(myCalendar!!.time)
        } else if (Constants.SELECTED_TAB == Constants.MONTHLY) {
            binding!!.currentDate.text = Helper.formatDateByMonth(
                myCalendar!!.time
            )
        }
        viewModel!!.getTransactions(myCalendar)
    }
}