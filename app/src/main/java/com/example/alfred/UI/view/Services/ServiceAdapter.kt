package com.example.alfred.UI.view.Services

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.example.alfred.R
import com.example.alfred.UI.viewmodel.service.ServicesViewModel
import com.example.alfred.data.model.CurrentService
import com.example.alfred.utilities.UtilityInjector


class ServiceAdapter (private val data: List<CurrentService>): RecyclerView.Adapter<ServiceAdapter.ViewHolder>() {

    private val items: MutableList<CardView>

    init {
        this.items = ArrayList()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.actual_service_view, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mensaje.text = "Servicio para el carro con placas " + data[position].idCar
        holder.direccion.text = "El estado del servicio es: "+ data[position].state
        if(data[position].state.toString() == "Finalizado")
        {
            holder.linea.setBackgroundColor(Color.RED)
        }


        items.add(holder.card)
    }



    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder
    internal constructor(
        itemView: View
    ): RecyclerView.ViewHolder(itemView){
        val card: CardView = itemView.findViewById<CardView>(R.id.card)
        val mensaje: TextView = itemView.findViewById<TextView>(R.id.mensajeServicioActual)
        val direccion: TextView = itemView.findViewById<TextView>(R.id.direccionServicioActual)
        val linea: LinearLayout = itemView.findViewById<LinearLayout>(R.id.lineaActualService)

    }

    }