package com.example.alfred

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alfred.UI.view.Allies.CustomAdapter
import com.example.alfred.UI.view.Home.ActivityHome
import com.example.alfred.UI.view.Home.PopUpNetworkActivity
import com.example.alfred.UI.view.Services.ActivityRequestService
import com.example.alfred.UI.viewmodel.ally.AllyViewModel
import com.example.alfred.UI.viewmodel.service.ServicesViewModel
import com.example.alfred.data.model.Ally
import com.example.alfred.data.model.Service
import com.example.alfred.databinding.ActivityAliadosBinding
import com.example.alfred.databinding.ActivityDetalleAliadoBinding
import com.example.alfred.utilities.UtilityInjector
import com.example.trapp.utilities.NetworkTracker
import kotlinx.coroutines.launch

class ActivityDetalleAliado : AppCompatActivity() {
    var idAlly = ""
    var serviceList: List<Service> = emptyList<Service>()
    var idService: String? = ""

    private lateinit var binding: ActivityDetalleAliadoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_aliado)
        binding = ActivityDetalleAliadoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val buttonClick = binding.DevolverHomeButton2
        buttonClick.setOnClickListener {
            val intent = Intent(this, ActivityHome::class.java)
            startActivity(intent)
        }
        val bundle =intent.extras
        val city = bundle?.getString("city")
        val address = bundle?.getString("address")
        val name = bundle?.getString("name")
        val cellPhone = bundle?.getString("cellPhone")
        val allyId = bundle?.getString("id")
        setup(city?:"", address?:"", name?:"", cellPhone?:"")
        serviceList = emptyList<Service>()

        idAlly = allyId?:""
        lifecycleScope.launch{
            setupUI()
        }

        val buttonComprar = binding.comprar
        val tracker= NetworkTracker.getInstance()
        buttonComprar.setOnClickListener {
            if(!tracker.getInternet(applicationContext)){
                val intent = Intent(this, PopUpNetworkActivity::class.java)
                startActivity(intent)
            }
            else if(idService!= ""){
                showRequestServiceActivity(idService?:"")
                println("Este es el id del servicio: "+idService)

            }
            else{
                showAlert()
            }

        }

    }

    //Muestra alerta por no tener un servicio Seleccionado
    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("No has seleccionado ningún servicio")
        builder.setMessage("Por favor selecciona un servicio antes" )
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }


    fun setup(city: String, address: String, name: String, cellPhone: String)
    {
        val nombreDetalleAliado = binding.nameServiceDetail
        val direccionDetalleAliado = binding.addressServiceDetail

        nombreDetalleAliado.text = name
        direccionDetalleAliado.text = address




    }

    private fun showRequestServiceActivity(idService: String)
    {
        val requestServiceIntent = Intent(this, ActivityRequestService::class.java).apply {
            putExtra("idService", idService)


        }
        startActivity(requestServiceIntent)
    }

    suspend fun setupUI() {

        val serviceFactory = UtilityInjector.provideServiceViewModelFactory(applicationContext)
        val serviceViewModel = ViewModelProviders.of(this, serviceFactory)[ServicesViewModel::class.java]


        serviceViewModel.getServicesByAllyId(" "+idAlly).observe(this, {
                services ->
                 serviceList= services
        })

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val adapter = CustomAdapter(serviceList)
        val listaDeServicios = binding.rvList

        listaDeServicios.setHasFixedSize(true)
        listaDeServicios.adapter = adapter
        listaDeServicios.layoutManager = layoutManager

        adapter.setOnItemClickListener(object: CustomAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                val precio = binding.precioDetail
                val duracion = binding.duracionDetail
                val description = binding.descripcionDetail

                precio.text = "Precio: $"+serviceList[position].price.toString()
                duracion.text = "Duración: "+serviceList[position].duration
                description.text = serviceList[position].description
                idService = serviceList[position].id

            }
        })

        //agregar a favoritos
        val allyFactory = UtilityInjector.provideAllyViewModelFactory(applicationContext)
        val allyViewModel = ViewModelProviders.of(this, allyFactory)[AllyViewModel::class.java]


        val ally: Ally

        ally = allyViewModel.getAlliesById(idAlly)
        val addFav = binding.addFav
        if(ally.favorite == true)
        {
            addFav.setImageResource(R.drawable.estrella)
        }
        else if (ally.favorite == false){
            addFav.setImageResource(R.drawable.favorito_sin_color)
        }

        addFav.setOnClickListener{
            if(ally.favorite)
            {
                addFav.setImageResource(R.drawable.favorito_sin_color)
                //remove
                ally.favorite = false
                lifecycleScope.launch{
                    allyViewModel.removeFromFavorites(ally)
                    allyViewModel.updateAlly(ally)
                }
            }
            else if(ally.favorite == false)
            {
                var seAgrego: Boolean = false

                lifecycleScope.launch{
                    seAgrego= allyViewModel.addToFavorites(ally)
                    if(seAgrego) {
                        ally.favorite = true
                        addFav.setImageResource(R.drawable.estrella)
                        allyViewModel.updateAlly(ally)
                    }

                }


            }


            


        }


    }


}