package com.example.alfred.UI.view.Allies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.alfred.R
import com.example.alfred.data.model.Service

class CustomAdapter (private val data: List<Service>): RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    private val items: MutableList<CardView>

    init {
        this.items = ArrayList()
    }
    private lateinit var mListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view, parent, false)
        return ViewHolder(v, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nombreServicio.text = data[position].name
        holder.precioServicio.text = "$"+data[position].price

        items.add(holder.card)
    }

    override fun getItemCount(): Int {
        return data.size
    }



    inner class ViewHolder
    internal constructor(
        itemView: View, listener: onItemClickListener
    ): RecyclerView.ViewHolder(itemView){
        val card: CardView = itemView.findViewById<CardView>(R.id.cards)
        val nombreServicio: TextView = itemView.findViewById<TextView>(R.id.nombreServicio)
        val precioServicio: TextView = itemView.findViewById<TextView>(R.id.precio)

        init{
            itemView.setOnClickListener{
                listener.onItemClick(absoluteAdapterPosition)
            }
        }
    }


}