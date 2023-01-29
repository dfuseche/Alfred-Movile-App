package com.example.alfred.UI.view.Services

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alfred.R
import com.example.alfred.UI.view.Allies.CustomAdapter
import com.example.alfred.UI.viewmodel.CurrentService.CurrentServiceViewModel
import com.example.alfred.UI.viewmodel.service.ServicesViewModel
import com.example.alfred.data.model.Car
import com.example.alfred.data.model.CurrentService
import com.example.alfred.data.model.Service
import com.example.alfred.databinding.ActivityActualServicesBinding
import com.example.alfred.databinding.ActivityRequestServiceBinding
import com.example.alfred.utilities.UtilityInjector
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ActualServices : AppCompatActivity() {

    private lateinit var binding: ActivityActualServicesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actual_services)
        binding = ActivityActualServicesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycleScope.launch {
            setupUI()
        }

    }

    suspend fun setupUI() {
        val currentServiceFactory = UtilityInjector.provideCurrentServiceViewModelFactory(applicationContext)
        val currentServiceViewModel = ViewModelProviders.of(this, currentServiceFactory)[CurrentServiceViewModel::class.java]

        var servicesList: List<CurrentService> = emptyList<CurrentService>()
        val currentUser = FirebaseAuth.getInstance().currentUser
        var userId=""
        if(currentUser!=null)
        {
            userId=currentUser.uid
        }

        val data = currentServiceViewModel.getCurrentServiceAsyncByUserId(userId).observe(this, { services ->
            servicesList = services


        })

        val dineroGastado = binding.dineroGastado
        val serviceFactory = UtilityInjector.provideServiceViewModelFactory(applicationContext)
        val serviceViewModel = ViewModelProviders.of(this, serviceFactory)[ServicesViewModel::class.java]
        var totalGastado = 0
        var servicios = Service()

        //Traer información servicio




        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val adapter = ServiceAdapter(servicesList)

        val listaDeServiciosActuales = binding.actualServicesList

        listaDeServiciosActuales.setHasFixedSize(true)
        listaDeServiciosActuales.adapter = adapter
        listaDeServiciosActuales.layoutManager = layoutManager
        for(service in servicesList){
            serviceViewModel.getServiceById(service.idService).observe(this, {
                    servicio ->
                servicios= servicio
            })

            totalGastado += servicios.price?:0

        }

        dineroGastado.text = "Total Gastado = " + totalGastado
    }


}