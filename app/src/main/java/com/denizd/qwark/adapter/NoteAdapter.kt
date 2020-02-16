package com.denizd.qwark.adapter

import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.denizd.qwark.R
import com.denizd.qwark.util.QwarkUtil
import com.denizd.qwark.model.Note
import com.google.android.material.chip.Chip

internal class NoteAdapter(private var notes: List<Note>, private val onClickListener: OnNoteClickListener) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    internal class NoteViewHolder(view: View, private val clickListener: OnNoteClickListener) : RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener {

        val content: TextView = view.findViewById(R.id.content)
        val created: TextView = view.findViewById(R.id.created)
        val category: Chip = view.findViewById(R.id.category_chip)
        internal lateinit var currentNote: Note

        init {
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)

            category.setOnClickListener {
                clickListener.onCategoryChipClick(currentNote.category)
            }
        }

        override fun onClick(v: View?) {
            clickListener.onNoteClick(currentNote)
        }

        override fun onLongClick(v: View?): Boolean {
            clickListener.onNoteLongClick(currentNote)
            return true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.note_card, parent, false)
        return NoteViewHolder(v, onClickListener)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentItem = notes[position]

        val content = SpannableString(currentItem.content)

        val contentColour = ContextCompat.getColor(holder.content.context, if (currentItem.dismissed) {
            content.setSpan(StrikethroughSpan(), 0, content.length, 0)
            R.color.colorHint
        } else {
            R.color.colorText
        })

        holder.content.setText(content, TextView.BufferType.SPANNABLE)
        holder.content.setTextColor(contentColour)
        holder.created.text = QwarkUtil.getCreatedString(holder.created.context, currentItem.time)
        holder.category.text = if (currentItem.category.isEmpty()) "---" else currentItem.category
        holder.category.visibility = if (currentItem.category.isEmpty()) View.GONE else View.VISIBLE
        holder.currentNote = currentItem
    }

    override fun getItemCount(): Int = notes.size

    fun setNotes(notes: List<Note>) {
        this.notes = notes
        notifyDataSetChanged()
    }

    internal interface OnNoteClickListener {
        fun onNoteClick(note: Note)
        fun onNoteLongClick(note: Note)
        fun onCategoryChipClick(category: String)
    }
}