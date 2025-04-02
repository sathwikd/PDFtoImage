package com.sathwikd.pdftoimage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class PageAdapter (
    private val pages: List<SinglePage>,
    private val onPageSelected: (Int, Boolean) -> Unit
) : RecyclerView.Adapter<PageAdapter.PageViewHolder>() {

    // ViewHolder class
    inner class PageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.pageCheckBox)
        val imageView: ImageView = view.findViewById(R.id.pageThumbnail) // Add ImageView for thumbnail
    }

    // Inflate the layout for each item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_page, parent, false)
        return PageViewHolder(view)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        val page = pages[position]
        holder.checkBox.text = "Page ${page.number}"
        holder.checkBox.isChecked = page.isSelected

        // Display the thumbnail
        holder.imageView.setImageBitmap(page.thumbnail)

        // Handle checkbox selection
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            page.isSelected = isChecked
            onPageSelected(page.number, isChecked)
        }
    }

    // Return total number of items
    override fun getItemCount(): Int = pages.size
}