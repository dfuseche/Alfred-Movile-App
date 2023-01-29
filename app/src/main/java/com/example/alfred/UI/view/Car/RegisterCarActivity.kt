package com.example.alfred.UI.view.Car

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isNotEmpty
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.example.alfred.R
import com.example.alfred.UI.view.Home.ActivityHome
import com.example.alfred.UI.viewmodel.car.CarViewModel
import com.example.alfred.data.model.Car
import com.example.alfred.data.model.CarNotif
import com.example.alfred.databinding.ActivityRegisterCarBinding
import com.example.alfred.utilities.NetworkStatus
import com.example.alfred.utilities.NetworkStatusHelper
import com.example.alfred.utilities.UtilityInjector
import com.example.trapp.utilities.NetworkTracker
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class RegisterCarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterCarBinding
    private val blockCharacterSet =  "!#$%&()*+,-./:;<=>?@[]^_{|}"+'"'
    private var notifGranted = false
    private val filter =
        InputFilter { source, start, end, dest, dstart, dend ->
            if (source != null && blockCharacterSet.contains("" + source)) {
                ""
            } else null
        }
    private lateinit var alertDialogBuilder: AlertDialog.Builder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mContext: Context = this@RegisterCarActivity
        binding = DataBindingUtil.setContentView(this,R.layout.activity_register_car)
        val buttonClick2 = binding.goBackCarRegister
        buttonClick2.setOnClickListener {
            val intent = Intent(this, ActivityHome::class.java)
            startActivity(intent)
        }
        alertDialogBuilder = AlertDialog.Builder(mContext)
        lifecycleScope.launch{
        setup()}
        NetworkStatusHelper(mContext).observe(this) {
            when (it) {
                NetworkStatus.Available -> changeNetworkState()
                NetworkStatus.Unavailable -> changeNetworkState()
            }
        }

    }
    private suspend fun setup() {
        val tracker = NetworkTracker.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val carFactory = UtilityInjector.provideCarViewModelFactory(applicationContext)
        val carViewModel = ViewModelProviders.of(this, carFactory)[CarViewModel::class.java]
        val buttonClick = binding.CarRegisterButton
        if(!tracker.getInternet(applicationContext)){
            onDisconnect()
        }else{
            onConnect()
        }
        val name = binding.carNameInput
        val plate = binding.carPlateInput
        val city = binding.carCityInput
        val country = binding.carCountryInput
        val model = binding.carModelInput
        val description =binding.carDescriptionInput
        val SOATDate =binding.SOATDate
        name.filters = arrayOf(filter, LengthFilter(30))
        name.doAfterTextChanged {
            if(name.text.isNullOrEmpty()){
                name.backgroundTintList = ColorStateList.valueOf(Color.RED)
                name.error="Digite un nombre válido(No puede estar vació ni usar caracteres especiales)"
            }else {
                name.backgroundTintList = ColorStateList.valueOf(R.color.background.toInt())
            }
        }
        plate.filters = arrayOf(filter, LengthFilter(6))
        plate.doAfterTextChanged {
            if(plate.text.isNullOrEmpty()||plate.text!!.length<5){
                plate.backgroundTintList = ColorStateList.valueOf(Color.RED)
                plate.error="Digite una placa válida(Debe tener almenos 5 digitos y letras sin usar caracteres especiales)"
            }else {
                plate.backgroundTintList = ColorStateList.valueOf(R.color.background.toInt())
            }
        }
        city.filters = arrayOf(filter, LengthFilter(30))
        city.doAfterTextChanged {
           if(city.text.isNullOrEmpty()){
               city.backgroundTintList = ColorStateList.valueOf(Color.RED)
               city.error="Digite una ciudad válida(No puede estar vacia ni usar caracteres especiales)"
           }else {
                city.backgroundTintList = ColorStateList.valueOf(R.color.background.toInt())
            }
        }
        country.filters = arrayOf(filter, LengthFilter(45))
        country.doAfterTextChanged {
            if(country.text.isNullOrEmpty()){
                country.backgroundTintList = ColorStateList.valueOf(Color.RED)
                country.error="Digite un país válido(No puede estar vació ni usar caracteres especiales)"
            }else {
                country.backgroundTintList = ColorStateList.valueOf(R.color.background.toInt())
            }
        }
        model.filters = arrayOf(filter, LengthFilter(30))
        model.doAfterTextChanged {
            if(model.text.isNullOrEmpty()){
                model.backgroundTintList = ColorStateList.valueOf(Color.RED)
                model.error="Digite un modelo válido(No puede estar vació ni usar caracteres especiales)"
            }else {
                model.backgroundTintList = ColorStateList.valueOf(R.color.background.toInt())
            }
        }
        description.filters = arrayOf(filter, LengthFilter(100))
        description.doAfterTextChanged {
            if(description.text.isNullOrEmpty()){
                description.backgroundTintList = ColorStateList.valueOf(Color.RED)
                description.error="Digite un descripción válida(No puede estar vacia ni usar caracteres especiales)"
            }else {
                description.backgroundTintList = ColorStateList.valueOf(R.color.background.toInt())
            }
        }
        var carList: List<Car> = emptyList<Car>()
        val userCars=carViewModel.getCarsByUserId(currentUser!!.uid, applicationContext).observe(this, {
                cars ->
            carList=cars

        })

        SOATDate.setMaxDate(Calendar.getInstance().time.time)
        when {
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                notifGranted = true
            }


        }


        buttonClick.setOnClickListener {
            if(!name.text.isNullOrEmpty() && !plate.text.isNullOrEmpty()&& !city.text.isNullOrEmpty()
                &&!country.text.isNullOrEmpty()&& !model.text.isNullOrEmpty()&& plate.text!!.length>=5
                &&SOATDate.isNotEmpty()&&tracker.getInternet(applicationContext))
            {
                val calendar = Calendar.getInstance()
                calendar.set(SOATDate.year, SOATDate.month, SOATDate.dayOfMonth)
                var carId: Long =0
                if(carList.isNotEmpty()){
                    carId = carList.sortedBy { car -> car.idCar }.get(carList.size-1).idCar+1
                }else{
                    carId=1
                }

                lifecycleScope.launch{
                    withContext(Dispatchers.IO){
                    carViewModel.createCar(Car(
                        idCar =carId,
                        name = name.text.toString(), plate = plate.text.toString(), city = city.text.toString()
                        , country = country.text.toString(), model = model.text.toString(),
                        soat_date = Timestamp(calendar.time), description = description.text.toString(),
                        user = currentUser!!.uid), applicationContext)
                    }}
                alertDialogBuilder.setTitle("Permisos de Notificación")
                alertDialogBuilder.setMessage(
                    "¿Le gustaría recibir notificaciones relacionadas al carro recién registado?" +
                            "Le mostraría cuando tiene pico y placa y si el SOAT está cerca a vencerse"
                )
                alertDialogBuilder.setPositiveButton(
                    "Si"
                ) { dialog, which ->
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            carViewModel.createCarNotif(
                                CarNotif(
                                    idCar = carId,
                                    name = name.text.toString(),
                                    plate = plate.text.toString(),
                                    soat_date = Timestamp(calendar.time),
                                    user = currentUser!!.uid
                                )
                            )
                        }

                    }
                    if(!notifGranted) {
                        notifPermissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }else{
                        val intent = Intent(this, ActivityHome::class.java)
                        startActivity(intent)
                    }


                        }
                alertDialogBuilder.setNegativeButton(
                    "No"
                ) { dialog, which ->
                    val intent = Intent(this, ActivityHome::class.java)
                    startActivity(intent)
                }
                var alert = alertDialogBuilder.create()
                alert.show()




            }else{
                if(!tracker.getInternet(applicationContext)) {
                }
                if(model.text.isNullOrEmpty()){
                    model.backgroundTintList = ColorStateList.valueOf(Color.RED)
                    model.error="Digite un modelo válido(No puede estar vació ni usar caracteres especiales)"
                }
                if(country.text.isNullOrEmpty()){
                    country.backgroundTintList = ColorStateList.valueOf(Color.RED)
                    country.error="Digite un país válido(No puede estar vació ni usar caracteres especiales)"
                }
                if(city.text.isNullOrEmpty()){
                    city.backgroundTintList = ColorStateList.valueOf(Color.RED)
                    city.error="Digite una ciudad válida(No puede estar vacia ni usar caracteres especiales)"
                }
                if(plate.text.isNullOrEmpty()||plate.text!!.length<5){
                    plate.backgroundTintList = ColorStateList.valueOf(Color.RED)
                    plate.error="Digite una placa válida(Debe tener almenos 5 digitos y letras sin usar caracteres especiales)"
                }
                if(name.text.isNullOrEmpty()){
                    name.backgroundTintList = ColorStateList.valueOf(Color.RED)
                    name.error="Digite un nombre válido(No puede estar vació ni usar caracteres especiales)"
                }
                if(description.text.isNullOrEmpty()){
                    description.backgroundTintList = ColorStateList.valueOf(Color.RED)
                    description.error="Digite un descripción válida(No puede estar vacia ni usar caracteres especiales)"
                }
            }







        }
    }

    override fun onResume() {
        super.onResume()
        changeNetworkState()
    }
    fun changeNetworkState(){
        val tracker = NetworkTracker.getInstance()
        if(tracker.getInternet(applicationContext)){
            onConnect()
        }else{
            onDisconnect()
        }
    }
    fun onDisconnect(){
        val buttonClick = binding.CarRegisterButton
        buttonClick.background= ContextCompat.getDrawable(this,R.drawable.gray_button)
        binding.noConnectionRCar.visibility= View.VISIBLE
    }
    fun onConnect(){
        val buttonClick = binding.CarRegisterButton
        buttonClick.background= ContextCompat.getDrawable(this,R.drawable.green_gradient)
        binding.noConnectionRCar.visibility= View.INVISIBLE
    }
    @RequiresApi(Build.VERSION_CODES.N)
    val notifPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
            if(isGranted){
                if(!notifGranted){
                    notifGranted=true
                }}
        val intent = Intent(this, ActivityHome::class.java)
        startActivity(intent)

        }

    }

