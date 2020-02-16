package com.denizd.qwark.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.denizd.qwark.R
import com.denizd.qwark.model.Participation
import com.denizd.qwark.util.QwarkUtil
import java.util.*

class ParticipationAdapter(private var participations: List<Participation>, private val onClickListener: ParticipationClickListener) : RecyclerView.Adapter<ParticipationAdapter.ParticipationViewHolder>() {

    class ParticipationViewHolder(view: View, private val onClickListener: ParticipationClickListener) : RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener {
        val imageView: ImageView = view.findViewById(R.id.image_view)
        val time: TextView = view.findViewById(R.id.time)
        val participationText: TextView = view.findViewById(R.id.participation)
        lateinit var participation: Participation

        init {
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            onClickListener.onParticipationClick(participation)
        }

        override fun onLongClick(v: View?): Boolean {
            onClickListener.onParticipationLongClick(participation)
            return true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipationViewHolder =
        ParticipationViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.participation_card,
            parent,
            false
        ), onClickListener)

    override fun onBindViewHolder(holder: ParticipationViewHolder, position: Int) {
        val currentItem = participations[position]

        holder.imageView.setImageDrawable(holder.imageView.context.getDrawable(
            with (currentItem) {
                when {
                    timesHandRaised > timesSpoken -> R.drawable.hand
                    timesHandRaised < timesSpoken -> R.drawable.speech
                    else -> R.drawable.cross
                }
            }
        ))
        holder.time.text = QwarkUtil.getSimpleDateTimeFormat().format(Date(currentItem.time))
        holder.participationText.text = holder.participationText.context.getString(
            R.string.participation_placeholder,
            currentItem.timesHandRaised.toString(),
            currentItem.timesSpoken.toString()
        )
        holder.participation = currentItem
    }

    override fun getItemCount(): Int = participations.size

    fun setParticipations(participations: List<Participation>) {
        this.participations = participations
        notifyDataSetChanged()
    }

    interface ParticipationClickListener {
        fun onParticipationClick(participation: Participation)
        fun onParticipationLongClick(participation: Participation)
    }
}