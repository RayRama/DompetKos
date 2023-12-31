package com.dompetkos.app.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dompetkos.app.R
import com.dompetkos.app.adapters.TransactionsAdapter.TransactionViewHolder
import com.dompetkos.app.databinding.RowTransactionBinding
import com.dompetkos.app.models.Transaction
import com.dompetkos.app.utils.Constants
import com.dompetkos.app.utils.Helper
import com.dompetkos.app.views.activites.MainActivity
import com.dompetkos.app.views.fragments.EditTransactionFragment
import io.realm.RealmResults

class TransactionsAdapter(var context: Context?, var transactions: RealmResults<Transaction>?) :
    RecyclerView.Adapter<TransactionViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        return TransactionViewHolder(
            LayoutInflater.from(context).inflate(R.layout.row_transaction, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions?.get(position)
        holder.binding.transactionAmount.text = transaction?.amount.toString()
        holder.binding.accountLbl.text = transaction?.account
        holder.binding.transactionDate.text = Helper.formatDate(transaction?.date)
        holder.binding.transactionCategory.text = transaction?.category
        val transactionCategory = Constants.getCategoryDetails(transaction?.category)
        transactionCategory?.categoryImage?.let { holder.binding.categoryIcon.setImageResource(it) }
        holder.binding.categoryIcon.backgroundTintList =
            transactionCategory?.categoryColor?.let { context!!.getColorStateList(it) }
        holder.binding.accountLbl.backgroundTintList =
            context!!.getColorStateList(Constants.getAccountsColor(transaction?.account))
        if (transaction?.type == Constants.INCOME) {
            holder.binding.transactionAmount.setTextColor(context!!.getColor(R.color.greenColor))
        } else if (transaction?.type == Constants.EXPENSE) {
            holder.binding.transactionAmount.setTextColor(context!!.getColor(R.color.redColor))
        }

        // add update transaction
        holder.itemView.setOnClickListener {
            EditTransactionFragment(transaction!!).show(
                (context as MainActivity?)!!.supportFragmentManager,
                "EditTransactionFragment"
            )
//            Log.d("Demo", "TODO")
        }


        holder.itemView.setOnLongClickListener {
            val deleteDialog = AlertDialog.Builder(
                context
            ).create()
            deleteDialog.setTitle("Delete Transaction")
            deleteDialog.setMessage("Are you sure to delete this transaction?")
            deleteDialog.setButton(
                DialogInterface.BUTTON_POSITIVE,
                "Yes"
            ) { dialogInterface: DialogInterface?, i: Int ->
                (context as MainActivity?)!!.viewModel!!.deleteTransaction(
                    transaction
                )
            }
            deleteDialog.setButton(
                DialogInterface.BUTTON_NEGATIVE,
                "No"
            ) { dialogInterface: DialogInterface?, i: Int -> deleteDialog.dismiss() }
            deleteDialog.show()
            false
        }
    }

    override fun getItemCount(): Int {
        return transactions?.size ?: 0
    }

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: RowTransactionBinding

        init {
            binding = RowTransactionBinding.bind(itemView)
        }
    }
}