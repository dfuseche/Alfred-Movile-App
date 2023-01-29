package com.example.alfred.UI.view.Allies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.alfred.R
import com.example.alfred.data.model.Ally
import com.example.alfred.data.model.Service
import org.w3c.dom.Text

class CustomAdapterAllies (private val data: List<Ally>): RecyclerView.Adapter<CustomAdapterAllies.ViewHolder>() {

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
            .inflate(R.layout.ally_card, parent, false)
        return ViewHolder(v, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.ciudadAliado.text = data[position].city
        holder.direccionAliado.text = data[position].address
        holder.nombreAliado.text = data[position].name
        holder.celularAliado.text = data[position].phoneNumber


        items.add(holder.card)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun filter(s: CharSequence?){
        data.filter{
            x->
            x.name?.contains(s.toString()) ?: false
        }
    }

            inner class ViewHolder
    internal constructor(
        itemView: View, listener: onItemClickListener
    ): RecyclerView.ViewHolder(itemView){
        val card: CardView = itemView.findViewById<CardView>(R.id.ally_card)
        val ciudadAliado: TextView = itemView.findViewById<TextView>(R.id.ciudadAliado)
        val direccionAliado: TextView = itemView.findViewById<TextView>(R.id.direccionAliado)
        val nombreAliado: TextView = itemView.findViewById<TextView>(R.id.nombreAliado)
        val celularAliado: TextView = itemView.findViewById<TextView>(R.id.celularAliado)

        init{
            itemView.setOnClickListener{
                listener.onItemClick(absoluteAdapterPosition)
            }
        }
    }


}