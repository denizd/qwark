package com.denizd.qwark.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.denizd.qwark.R
import com.denizd.qwark.model.CourseIcon
import com.google.android.material.card.MaterialCardView

internal class IconAdapter(private var icons: List<CourseIcon>, private val onClickListener: IconClickListener) : RecyclerView.Adapter<IconAdapter.IconViewHolder>() {

    private var selectedItem = 0

    internal class IconViewHolder(view: View, private val clickListener: IconClickListener) : RecyclerView.ViewHolder(view), View.OnClickListener {

        val imageView: ImageView = view.findViewById(R.id.image_view)
        val card: MaterialCardView = view.findViewById(R.id.card)
        var image: Int = 0
        var key: String = ""

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            clickListener.onIconClick(key, image, adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.icon_item, parent, false)
        return IconViewHolder(v, onClickListener)
    }

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        val currentItem = icons[position]

        val context = holder.card.context

        holder.imageView.setImageDrawable(context.getDrawable(currentItem.value))
        holder.image = currentItem.value
        holder.key = currentItem.key

        holder.card.setCardBackgroundColor(context.getColor(if (position == selectedItem) {
            R.color.colorAccent
        } else {
            R.color.colorCardBackground
        }))
        holder.imageView.setColorFilter(context.getColor(if (position == selectedItem) {
            R.color.colorBackground
        } else {
            R.color.colorText
        }))
    }

    override fun getItemCount(): Int = icons.size

    fun setIcons(icons: List<CourseIcon>) {
        this.icons = icons
        notifyDataSetChanged()
    }

    fun setSelectedItem(position: Int) {
        selectedItem = position
        notifyDataSetChanged()
    }

    internal interface IconClickListener {
        fun onIconClick(key: String, value: Int, position: Int)
    }
}