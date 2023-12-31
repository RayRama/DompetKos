package com.dompetkos.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dompetkos.app.R
import com.dompetkos.app.adapters.CategoryAdapter.CategoryViewHolder
import com.dompetkos.app.databinding.SampleCategoryItemBinding
import com.dompetkos.app.models.Category

class CategoryAdapter(
    var context: Context?,
    var categories: ArrayList<Category>?,
    var categoryClickListener: CategoryClickListener
) : RecyclerView.Adapter<CategoryViewHolder>() {
    interface CategoryClickListener {
        fun onCategoryClicked(category: Category?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            LayoutInflater.from(context).inflate(R.layout.sample_category_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories!![position]
        holder.binding.categoryText.text = category?.categoryName
        category?.categoryImage?.let { holder.binding.categoryIcon.setImageResource(it) }
        holder.binding.categoryIcon.backgroundTintList =
            category?.categoryColor?.let { context!!.getColorStateList(it) }
        holder.itemView.setOnClickListener { c: View? ->
            categoryClickListener.onCategoryClicked(
                category
            )
        }
    }

    override fun getItemCount(): Int {
        return categories!!.size
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: SampleCategoryItemBinding

        init {
            binding = SampleCategoryItemBinding.bind(itemView)
        }
    }
}