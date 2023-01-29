package com.example.alfred.UI.view.Services

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.example.alfred.R
import com.example.alfred.UI.view.Home.PopUpNetworkActivity
import com.example.alfred.UI.viewmodel.CurrentService.CurrentServiceViewModel
import com.example.alfred.UI.viewmodel.car.CarViewModel
import com.example.alfred.UI.viewmodel.service.ServicesViewModel
import com.example.alfred.data.model.Car
import com.example.alfred.data.model.CurrentService
import com.example.alfred.data.model.Service
import com.example.alfred.databinding.ActivityDetalleAliadoBinding
import com.example.alfred.databinding.ActivityRequestServiceBinding
import com.example.alfred.utilities.UtilityInjector
import com.example.trapp.utilities.NetworkTracker
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class ActivityRequestService : AppCompatActivity() {

    private var service = Service("null")
    private lateinit var binding: ActivityRequestServiceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_service)
        binding = ActivityRequestServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bundle =intent.extras
        val idService = bundle?.getString("idService")
        lifecycleScope.launch {
            setupUI(idService?:"")
        }
    }

    suspend fun setupUI(idService: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val carFactory = UtilityInjector.provideCarViewModelFactory(applicationContext)
        val userFactory = UtilityInjector.provideUserViewModelFactory(applicationContext)
        val carViewModel = ViewModelProviders.of(this, carFactory)[CarViewModel::class.java]
        val serviceFactory = UtilityInjector.provideServiceViewModelFactory(applicationContext)
        val serviceViewModel = ViewModelProviders.of(this, serviceFactory)[ServicesViewModel::class.java]
        val currentServiceFactory = UtilityInjector.provideCurrentServiceViewModelFactory(applicationContext)
        val currentServiceViewModel = ViewModelProviders.of(this, currentServiceFactory)[CurrentServiceViewModel::class.java]

        var carList: List<Car> = emptyList<Car>()
        var userId = ""
        if (currentUser != null)
            userId = currentUser.uid

        //Agregar carros al spinne
        val userCars = carViewModel.getCarsByUserId(userId,applicationContext).observe(this, { cars ->
            carList = cars

        })
        val data: MutableList<String> = ArrayList()
        for(i in carList){
            data.add(i.plate)
        }

        val spinner = binding.spinner
        if (spinner != null) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, data
            )
            spinner.adapter = adapter


        }
        // Ingresar información del servicio
        println("Este es el id del servicio: "+idService)
        val servicio = serviceViewModel.getServiceById(idService).observe(this, { serviceFound ->
            service = serviceFound

        })

        val nombreServicio = binding.nombreServicioASolicitar

        nombreServicio.text = service.name

        val totalServicio = binding.totalSolicitarServicio

        totalServicio.text = "Total: "+ service.price


        //Creación servicio

        val direccionRecogida = binding.direccionDeRecogida

        val direccionEntrega = binding.direccionDeEntrega

        val driver = binding.needDriver

        val creacionServicioButton = binding.finalizarCreacionServicio

        val tracker= NetworkTracker.getInstance()
        creacionServicioButton.setOnClickListener{
            if(!tracker.getInternet(applicationContext)){
                val intent = Intent(this, PopUpNetworkActivity::class.java)
                startActivity(intent)
            }
            else
            {
                lifecycleScope.launch {
                    if(direccionEntrega.text.isNotEmpty() && direccionRecogida.text.isNotEmpty())
                    {
                        createService(currentUser?.uid+spinner.selectedItem.toString(), service.id?:"",currentUser?.uid?:"",spinner.selectedItem.toString(),
                            direccionRecogida.text.toString(), direccionEntrega.text.toString(), driver.isChecked, "Servicio iniciado" )

                    }
                    else{
                        showEmptyFildAlert()
                    }

                } 
            }



        }


    }

    private fun sendMessageWhatsapp(placas: String, dirRecogida: String)
    {
        val phoneNumberWithCountryCode = "+573228225845"
        val message = "Hola soy el usuario que solicito el servicio de "+service.name+ " para el carro de placas "+ placas + " en la direccion "+ dirRecogida

        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(
                    String.format(
                        "https://api.whatsapp.com/send?phone=%s&text=%s",
                        phoneNumberWithCountryCode,
                        message
                    )
                )
            )
        )
    }

     suspend fun createService(id: String, serviceId: String, userId: String, carId: String, dirRecogida: String, dirEntrega: String, driver: Boolean, state: String){
        val currentServiceFactory = UtilityInjector.provideCurrentServiceViewModelFactory(applicationContext)
        val currentServiceViewModel = ViewModelProviders.of(this, currentServiceFactory)[CurrentServiceViewModel::class.java]
         val time = Calendar.getInstance().time
         val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
         val current = formatter.format(time)
        currentServiceViewModel.createCurrentService(CurrentService(id, serviceId,userId, carId, dirRecogida, dirEntrega, driver, state, current ))
        if(driver)
        {
            showCreationServiceAlertWithDriver(carId, dirRecogida)
        }
         else if(!driver){
            showCreationServiceAlertWithoutDriver(carId)
        }

    }

    //Mensaje para usuario que solicita conductor
    private fun showCreationServiceAlertWithDriver(carro:String, dirRecogida: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Servicio creado")
        builder.setMessage("Su servicio para el carro con las placas "+ carro + " ha sido creado con exito. Has click en aceptar para hablar con tu conductor de Alfred")
        builder.setPositiveButton("Aceptar"){ dialog, which ->
            sendMessageWhatsapp(carro, dirRecogida)
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    //Mensaje para usuario que no solicito conductor
    private fun showCreationServiceAlertWithoutDriver(carro:String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Servicio creado")
        builder.setMessage("Su servicio para el carro con las placas "+ carro + " ha sido creado con exito")
        builder.setPositiveButton("Aceptar"){ dialog, which ->
            val intent = Intent(this, ActualServices::class.java)
            startActivity(intent)
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showEmptyFildAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("No has llenado todos los campos")
        builder.setMessage("Por favor llena todos los campos para pedir el servicio")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }



}




