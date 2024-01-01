package com.dompetkos.app.views.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dompetkos.app.R
import com.dompetkos.app.adapters.AccountsAdapter
import com.dompetkos.app.adapters.CategoryAdapter
import com.dompetkos.app.databinding.FragmentEditTransactionBinding
import com.dompetkos.app.databinding.ListDialogBinding
import com.dompetkos.app.models.Account
import com.dompetkos.app.models.Transaction
import com.dompetkos.app.utils.Constants
import com.dompetkos.app.utils.Helper
import com.dompetkos.app.views.activites.MainActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar
import java.util.Date

class EditTransactionFragment(recentTransaction: Transaction) : BottomSheetDialogFragment() {

    var binding: FragmentEditTransactionBinding? = null

    // get recent transaction data
    var transaction: Transaction? = recentTransaction


    var tempTransaction: Transaction? = null
    var tempAmount: Double? = transaction!!.amount
    var tempType: String? = transaction!!.type
    var tempDate: Date? = transaction!!.date
    var tempCategory: String? = transaction!!.category
    var tempAccount: String? = transaction!!.account

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // passing recent transaction data to view
        binding = FragmentEditTransactionBinding.inflate(inflater)
        tempTransaction = Transaction()
        binding!!.incomeBtn.setOnClickListener { view: View? ->
            binding!!.incomeBtn.background =
                requireContext().getDrawable(R.drawable.income_selector)
            binding!!.expenseBtn.background =
                requireContext().getDrawable(R.drawable.default_selector)
            binding!!.expenseBtn.setTextColor(requireContext().getColor(R.color.textColor))
            binding!!.incomeBtn.setTextColor(requireContext().getColor(R.color.greenColor))
//            transaction!!.type = Constants.INCOME
            tempType = Constants.INCOME
        }

        binding!!.expenseBtn.setOnClickListener { view: View? ->
            binding!!.incomeBtn.background =
                requireContext().getDrawable(R.drawable.default_selector)
            binding!!.expenseBtn.background =
                requireContext().getDrawable(R.drawable.expense_selector)
            binding!!.incomeBtn.setTextColor(requireContext().getColor(R.color.textColor))
            binding!!.expenseBtn.setTextColor(requireContext().getColor(R.color.redColor))
//            transaction!!.type = Constants.EXPENSE
            tempType = Constants.EXPENSE
        }

        // Get recent transaction date
        binding!!.date.setText(Helper.formatDate(transaction!!.date))
        binding!!.date.setOnClickListener {
            val datePickerDialog = DatePickerDialog(requireContext())
            datePickerDialog.setOnDateSetListener { datePicker: DatePicker, i: Int, i1: Int, i2: Int ->
                val calendar = Calendar.getInstance()
                calendar[Calendar.DAY_OF_MONTH] = datePicker.dayOfMonth
                calendar[Calendar.MONTH] = datePicker.month
                calendar[Calendar.YEAR] = datePicker.year

                val dateToShow = Helper.formatDate(calendar.time)
                binding!!.date.setText(dateToShow)
//                transaction!!.date = calendar.time
                tempDate = calendar.time
            }
            datePickerDialog.show()
        }

        // Get recent transaction amount
        binding!!.amount.setText((transaction!!.amount * -1).toString())

        // Get recent transaction category
        binding!!.category.setText(transaction!!.category)
        binding!!.category.setOnClickListener {c: View? ->
            val dialogBinding = ListDialogBinding.inflate(inflater)
            val categoryDialog = AlertDialog.Builder(context).create()
            categoryDialog.setView(dialogBinding.root)
            val categoryClickListener = object : CategoryAdapter.CategoryClickListener {
                override fun onCategoryClicked(category: com.dompetkos.app.models.Category?) {
                    binding!!.category.setText(category!!.categoryName)
//                    transaction!!.category  = category.categoryName
                    tempCategory = category.categoryName
                    categoryDialog.dismiss()
                }
            }
            val categoryAdapter =
                CategoryAdapter(context, Constants.categories, categoryClickListener)
            dialogBinding.recyclerView.layoutManager = GridLayoutManager(context, 3)
            dialogBinding.recyclerView.adapter = categoryAdapter
            categoryDialog.show()
        }

        // Get recent transaction account
        binding!!.account.setText(transaction!!.account)
        binding!!.account.setOnClickListener { c: View? ->
            val dialogBinding = ListDialogBinding.inflate(inflater)
            val accountsDialog = AlertDialog.Builder(context).create()
            accountsDialog.setView(dialogBinding.root)
            val accounts = ArrayList<Account>()
            accounts.add(Account(0.0, "Cash"))
            accounts.add(Account(0.0, "Bank"))
            accounts.add(Account(0.0, "Dana"))
            accounts.add(Account(0.0, "Gopay"))
            accounts.add(Account(0.0, "Lainnya"))
            val accountClickListener = object : AccountsAdapter.AccountsClickListener {
                override fun onAccountSelected(account: Account) {
                    binding!!.account.setText(account.accountName)
//                    transaction!!.account = account.accountName
                    tempAccount = account.accountName
                    accountsDialog.dismiss()
                }
            }
            val adapter = AccountsAdapter(context, accounts, accountClickListener)
            dialogBinding.recyclerView.layoutManager = LinearLayoutManager(context)
            //dialogBinding.recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
            dialogBinding.recyclerView.adapter = adapter
            accountsDialog.show()
        }

        // Get recent transaction note
        binding!!.note.setText(transaction!!.note)

        // Update recent transaction
        binding!!.editTransactionBtn.setOnClickListener { c: View? ->
            val amount = binding!!.amount.text.toString()
            val note = binding!!.note.text.toString()

            if (amount.isNotEmpty() && tempType != null && tempDate != null && tempAccount != null && tempCategory != null) {
                val amountFinal = amount.toDouble()
                tempTransaction!!._id = transaction!!._id
                tempTransaction!!.transactionId = transaction!!.transactionId
                tempTransaction!!.type = tempType
                tempTransaction!!.date = tempDate
                tempTransaction!!.category = tempCategory
                tempTransaction!!.account = tempAccount
                tempTransaction!!.note = note
//
                if (transaction!!.type == Constants.EXPENSE) {
                    tempTransaction!!.amount = amountFinal * -1
                } else {
                    tempTransaction!!.amount = amountFinal
                }

                (activity as MainActivity?)!!.viewModel!!.editTransaction(tempTransaction!!)
                (activity as MainActivity?)?.getTransactions
                dismiss()
            } else {
                Snackbar.make(
                    binding!!.root,
                    "Please fill all the fields",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }



        return binding!!.root
    }

}