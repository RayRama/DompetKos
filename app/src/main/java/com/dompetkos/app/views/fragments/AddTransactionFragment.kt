package com.dompetkos.app.views.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dompetkos.app.R
import com.dompetkos.app.adapters.AccountsAdapter
import com.dompetkos.app.adapters.CategoryAdapter
import com.dompetkos.app.databinding.FragmentAddTransactionBinding
import com.dompetkos.app.databinding.ListDialogBinding
import com.dompetkos.app.models.Account
import com.dompetkos.app.models.Transaction
import com.dompetkos.app.utils.Constants
import com.dompetkos.app.utils.Helper
import com.dompetkos.app.views.activites.MainActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar

class AddTransactionFragment : BottomSheetDialogFragment() {

    var binding: FragmentAddTransactionBinding? = null
    var transaction: Transaction? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddTransactionBinding.inflate(inflater)
        transaction = Transaction()
        binding!!.incomeBtn.setOnClickListener { view: View? ->
            binding!!.incomeBtn.background =
                requireContext().getDrawable(R.drawable.income_selector)
            binding!!.expenseBtn.background =
                requireContext().getDrawable(R.drawable.default_selector)
            binding!!.expenseBtn.setTextColor(requireContext().getColor(R.color.textColor))
            binding!!.incomeBtn.setTextColor(requireContext().getColor(R.color.greenColor))
            transaction!!.type = Constants.INCOME
        }
        binding!!.expenseBtn.setOnClickListener { view: View? ->
            binding!!.incomeBtn.background =
                requireContext().getDrawable(R.drawable.default_selector)
            binding!!.expenseBtn.background =
                requireContext().getDrawable(R.drawable.expense_selector)
            binding!!.incomeBtn.setTextColor(requireContext().getColor(R.color.textColor))
            binding!!.expenseBtn.setTextColor(requireContext().getColor(R.color.redColor))
            transaction!!.type = Constants.EXPENSE
        }
        binding!!.date.setOnClickListener {
            val datePickerDialog = DatePickerDialog(requireContext())
            datePickerDialog.setOnDateSetListener { datePicker: DatePicker, i: Int, i1: Int, i2: Int ->
                val calendar = Calendar.getInstance()
                calendar[Calendar.DAY_OF_MONTH] = datePicker.dayOfMonth
                calendar[Calendar.MONTH] = datePicker.month
                calendar[Calendar.YEAR] = datePicker.year

                //SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM, yyyy");
                val dateToShow = Helper.formatDate(calendar.time)
                binding!!.date.setText(dateToShow)
                transaction!!.date = calendar.time
                transaction!!.transactionId = calendar.time.time
            }
            datePickerDialog.show()
        }
        binding!!.category.setOnClickListener { c: View? ->
            val dialogBinding = ListDialogBinding.inflate(inflater)
            val categoryDialog = AlertDialog.Builder(context).create()
            categoryDialog.setView(dialogBinding.root)
            val categoryClickListener = object : CategoryAdapter.CategoryClickListener {
                override fun onCategoryClicked(category: com.dompetkos.app.models.Category?) {
                    binding!!.category.setText(category!!.categoryName)
                    transaction!!.category = category.categoryName
                    categoryDialog.dismiss()
                }
            }
            val categoryAdapter =
                CategoryAdapter(context, Constants.categories, categoryClickListener)
            dialogBinding.recyclerView.layoutManager = GridLayoutManager(context, 3)
            dialogBinding.recyclerView.adapter = categoryAdapter
            categoryDialog.show()
        }
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
                    transaction!!.account = account.accountName
                    accountsDialog.dismiss()
                }
            }
            val adapter = AccountsAdapter(context, accounts, accountClickListener)
            dialogBinding.recyclerView.layoutManager = LinearLayoutManager(context)
            //dialogBinding.recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
            dialogBinding.recyclerView.adapter = adapter
            accountsDialog.show()
        }
        binding!!.saveTransactionBtn.setOnClickListener { c: View? ->
            val amount = binding!!.amount.text.toString()
            val note = binding!!.note.text.toString()

            if (amount.isNotEmpty() && transaction!!.type != null && transaction!!.date != null && transaction!!.account != null && transaction!!.category != null) {
                val amountFinal = amount.toDouble()
                if (transaction!!.type == Constants.EXPENSE) {
                    transaction!!.amount = amountFinal * -1
                } else {
                    transaction!!.amount = amountFinal
                }
                transaction!!.note = note

                (activity as MainActivity?)!!.viewModel!!.addTransaction(transaction!!)
                (activity as MainActivity?)?.getTransactions
                dismiss()
            } else {
//                Log.d("Err", "Gagal")
                Snackbar.make(
                    binding!!.root,
                    "Please fill all fields",
                    Snackbar.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
        }
        return binding!!.root
    }
}