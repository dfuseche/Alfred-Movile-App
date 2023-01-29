package com.example.alfred.UI.view.Home

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.example.alfred.ActivityAliados
import com.example.alfred.ActivityCar
import com.example.alfred.ActivityLogin
import com.example.alfred.R
import com.example.alfred.UI.view.Car.RegisterCarActivity
import com.example.alfred.UI.view.Services.ActualServices
import com.example.alfred.UI.viewmodel.ServiceClient.ServiceClientViewModel
import com.example.alfred.UI.viewmodel.car.CarViewModel
import com.example.alfred.UI.viewmodel.user.UserViewModel
import com.example.alfred.data.cache.CarMemCache
import com.example.alfred.data.model.Car
import com.example.alfred.data.model.CarNotif
import com.example.alfred.data.model.ServiceClient
import com.example.alfred.data.model.User
import com.example.alfred.databinding.ActivityHomeBinding
import com.example.alfred.utilities.NetworkStatus
import com.example.alfred.utilities.NetworkStatusHelper
import com.example.alfred.utilities.UtilityInjector
import com.example.trapp.utilities.NetworkTracker
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

enum class ProviderType {
    BASIC,
    GOOGLE
}
class ActivityHome : AppCompatActivity() {

    private lateinit var binding:ActivityHomeBinding
    var startTime: Long = 0
    val CHANNEL_ID = "Alfred_Car_Notification_Channel"
    var endTime: Long = 0
    var carNum=0
    var preciseLocationGranted = false
    var locationGranted = false
    var carListSaved: List<Car> = emptyList<Car>()
    var carNotifications: List<CarNotif> = emptyList<CarNotif>()
        lateinit var currentUser: FirebaseUser
    var connectionState = true
    private lateinit var alertDialogBuilder: AlertDialog.Builder
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mContext: Context = this@ActivityHome
        startTime = System.currentTimeMillis();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext)
        alertDialogBuilder = AlertDialog.Builder(mContext)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_home)
        lifecycleScope.launch{
            setupUI()}

        NetworkStatusHelper(mContext).observe(this) {
            when (it) {
                NetworkStatus.Available -> changeConnectivityState()
                NetworkStatus.Unavailable -> changeConnectivityState()
            }
        }


    }

    private fun setup(email:String)
    {

        title = "Inicio"

        val saludo = binding.greeting
        saludo.text = email

        val logOutButton = binding.logOut
        val tracker= NetworkTracker.getInstance()
        logOutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val prefs = getSharedPreferences("com.example.alfred.PREFERENCE_FILE_KEY", MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            val intent = Intent(this, ActivityLogin::class.java)
            startActivity(intent)
        }
        val buttonClick = binding.include.buttonService

        buttonClick.setOnClickListener {
            if(!tracker.getInternet(applicationContext)){
                val intent = Intent(this,PopUpNetworkActivity::class.java)
                startActivity(intent)
            }
            else{
                val intent = Intent(this, ActivityAliados::class.java)
                startActivity(intent)
            }

        }
        val buttonClick2 = binding.carButton
        buttonClick2.setOnClickListener {
            val intent = Intent(this, ActivityCar::class.java)
            startActivity(intent)

        }
        val buttonClick3 = binding.imageView4
        buttonClick3.setOnClickListener {
            val intent = Intent(this, ActivityCar::class.java)
            startActivity(intent)

        }
        val buttonClick4 = binding.include.buttonCarClock
        buttonClick4.setOnClickListener {
            val intent = Intent(this, ActualServices::class.java)
            startActivity(intent)

        }
        val buttonClickPlus = binding.constraintLayoutPlus
        buttonClickPlus.setOnClickListener {
            val intent = Intent(this, RegisterCarActivity::class.java)
            startActivity(intent)

        }
        if(carNum<=1){
        val buttonClickPlus2 = binding.constraintLayoutPlus2
        buttonClickPlus2.setOnClickListener {
            val intent = Intent(this, RegisterCarActivity::class.java)
            startActivity(intent)

        }}
    }
    @RequiresApi(Build.VERSION_CODES.N)
    suspend fun setupUI() {
        currentUser = FirebaseAuth.getInstance().currentUser!!
        val userFactory= UtilityInjector.provideUserViewModelFactory(applicationContext)
        val serviceClientFactory = UtilityInjector.provideServiceClientViewModelFactory(applicationContext)
        val userViewModel = ViewModelProviders.of(this, userFactory)[UserViewModel::class.java]
        val serviceClienViewModel = ViewModelProviders.of(this, serviceClientFactory)[ServiceClientViewModel::class.java]
        var userInfo = User()

        /**
         *  type 2
         */

        var serviceClientList: List<ServiceClient> = emptyList<ServiceClient>()
        val clientService = serviceClienViewModel.getServiceClient().observe(this, {
                serviceClients ->
            serviceClientList=serviceClients
        })



        var userId = ""

        if (currentUser != null)
            userId = currentUser!!.uid
        getUserCars()
        val user = userViewModel.getUserById(userId).observe(this, {
                found ->
            userInfo = found
        })
        var userName ="No se encontró el nombre"
        if(userInfo.name!=null)
            userName= userInfo.name!!
        if(carNum>=2){
            binding.constraintLayoutPlus2.visibility = View.INVISIBLE
            binding.constraintLayoutSecondCar.visibility = View.VISIBLE
        }
        changeConnectivityState()
        val bundle = intent.extras
        val email = bundle?.getString("email")
        setup(userName)

        val prefs = getSharedPreferences("com.example.alfred.PREFERENCE_FILE_KEY", MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.apply()
        changeCarList(carListSaved)
        getMostUsedService(serviceClientList)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun changeCarList(carList:List<Car>) {
        val plate = binding.textPlaca
        val carName = binding.textTitleCarro
        val plate2 = binding.textPlaca2
        val carName2 = binding.textTitleCarro2
        val firstCarBackground = binding.constraintLayoutFirstCar
        val secondCarBackground = binding.constraintLayoutSecondCar
        val picoYPlaca = binding.picoyplaca
        val currentDay: Int = Calendar.getInstance().getTime().day
        plate.text= "Aquí estaría tu carro!"
        carName.text= "Agregalo a tu garaje!"
        if(carList.size>=1){
            plate.text= carList[0].plate
            carName.text= carList[0].name

        }
        if(carList.size>=2){
            plate2.text= carList[1].plate
            carName2.text= carList[1].name

        }
        when {
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                preciseLocationGranted = true
            }
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                locationGranted = true
            }

        }
        alertDialogBuilder.setTitle("Permisos de GPS")
        alertDialogBuilder.setMessage(
            "Hola! Para usar algunas de nuestras herramientas, como el aviso de pico y placa y el uso de un mapa para pedir servicios, necesitamos de la información de gps. Oprima " +
                    "Si para poder darnos acceso a esta información, de lo contrario oprima No"
        )
        //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))
        var ask = false
        alertDialogBuilder.setPositiveButton(
            "Si"
        ) { dialog, which ->
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
        alertDialogBuilder.setNegativeButton(
            "No"
        ) { dialog, which ->

        }

        if (!preciseLocationGranted) {
            var alert = alertDialogBuilder.create()
            alert.show()

        }

        val tracker = NetworkTracker.getInstance()

        picoYPlaca.text="La función de pico y placa no está disponible por falta de ubicación gps"
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                // Got last known location. In some rare situations this can be null.
                if(location!=null){
                    if(location.longitude>=-74.162090&&location.longitude<=-74.036000&&location.latitude>=4.485000&&location.latitude<=4.825989){
                        if(currentDay%2==0)
                            picoYPlaca.text="Los carros con placas terminadas en 0-2-4-6-8 tienen pico y placa"

                        else
                            picoYPlaca.text="Los carros con placas terminadas en 1-3-5-7-9 tienen pico y placa"

                        if(carList.size>=1&&currentDay%2!=plate.text.get(plate.text.length-1).toString().toInt()%2){
                            firstCarBackground.setBackground(ContextCompat.getDrawable(this,R.drawable.green_gradient_square))
                            plate.setTextColor(ContextCompat.getColor(this, R.color.black))
                            carName.setTextColor(ContextCompat.getColor(this, R.color.black))}
                        if(carList.size>=2&&currentDay%2!=plate2.text.get(plate2.text.length-1).toString().toInt()%2){
                            secondCarBackground.setBackground(ContextCompat.getDrawable(this,R.drawable.green_gradient_square))
                            plate2.setTextColor(ContextCompat.getColor(this, R.color.black))
                            carName2.setTextColor(ContextCompat.getColor(this, R.color.black))}}
                    else{
                        picoYPlaca.text="La función de pico y placa no está disponible ya que no se encuentra actualmente en Bogota"
                    }}
                else {
                    fusedLocationClient.requestLocationUpdates(
                        locationRequest, locationCallback, Looper.getMainLooper()
                    )
                }
            }


    }
    suspend fun getMostUsedService(serviceClientList: List<ServiceClient>){
        var recomend = binding.recommended
        var resultArray : MutableList<ServiceClient> = ArrayList<ServiceClient>()
        var set : HashSet<ServiceClient> = HashSet<ServiceClient>()
        var range =serviceClientList.indices
        for(i in range){
            if(set.contains(serviceClientList.get(i))){
                resultArray.add(serviceClientList.get(i))
            }else{
                set.add(serviceClientList.get(i))
            }
        }
        resultArray.sort()
        if(resultArray.size>0)
            recomend.text = resultArray.get(0).servicetype
    }
    @RequiresApi(Build.VERSION_CODES.N)
    val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
                if(preciseLocationGranted==false){
                    preciseLocationGranted=true
                    changeCarList(carListSaved)
                }
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
                if(locationGranted==false){
                    locationGranted=true
                    changeCarList(carListSaved)
                }
            } else -> {
            // No location access granted.
        }
        }
    }
    private val locationRequest =
        LocationRequest().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = TimeUnit.SECONDS.toMillis(10)
            fastestInterval = TimeUnit.SECONDS.toMillis(1)
        }


    private val locationCallback =
        object : LocationCallback() {

            override  fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)

                if (locationResult != null) {
                    changeCarList(carListSaved)
                }
            }
        }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch{

            getUserCars()
            if(carNum>0)
                changeCarList(carListSaved)}

    }

    override fun onResume(){
        super.onResume()
        changeConnectivityState()
    }

    suspend fun getUserCars(){
        val carFactory = UtilityInjector.provideCarViewModelFactory(applicationContext)
        val carViewModel = ViewModelProviders.of(this, carFactory)[CarViewModel::class.java]
        var userId = ""


        if (currentUser != null)
            userId = currentUser!!.uid
        carViewModel.getCarsByUserId(userId, applicationContext).observe(this) { cars ->
            carListSaved = cars
            carNum = carListSaved.size
        }
        CarMemCache.carsList.addAll(carListSaved)
        val carSize = CarMemCache.carsList.size
        for(i in 0..carSize-1){
            println(CarMemCache.carsList[i].model)
        }
        sendCarNotifications(carViewModel)

    }

    fun changeConnectivityState(){
        val tracker=NetworkTracker.getInstance()
        if(connectionState&&!tracker.getInternet(applicationContext)){
           connectionState=false
            binding.constraintLayoutCross.visibility= View.VISIBLE
            binding.constraintLayoutPlus.visibility= View.INVISIBLE
            if(binding.constraintLayoutPlus2.visibility== View.VISIBLE){
                binding.constraintLayoutCross2.visibility= View.VISIBLE
                binding.constraintLayoutPlus2.visibility= View.INVISIBLE}
        }else if(!connectionState&&tracker.getInternet(applicationContext)){
            connectionState=true
            binding.constraintLayoutCross.visibility= View.INVISIBLE
            binding.constraintLayoutPlus.visibility= View.VISIBLE
            if(binding.constraintLayoutCross2.visibility== View.VISIBLE){
                binding.constraintLayoutCross2.visibility= View.INVISIBLE
                binding.constraintLayoutPlus2.visibility= View.VISIBLE}
        }
    }
    override fun onDestroy() {
        endTime = System.currentTimeMillis()
        val timeSpend = endTime - startTime
        // Insert timeSpend in databse.
        Toast.makeText(baseContext, "Tiempo tomado en actividad Home$timeSpend" , Toast.LENGTH_SHORT).show()
//        Crashlytics.logException(new RuntimeException("Tiempo tomado en actividad$timeSpend"));
        super.onDestroy()
    }
    suspend fun sendCarNotifications(carViewModel: CarViewModel) {
        when {
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                carViewModel.getCarsNotifByUserId(currentUser!!.uid).observe(this){ carNotifs ->
                    carNotifications= carNotifs
                }
                createNotificationChannel()
                var builder = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_outline_directions_car_24)
                    .setContentTitle("Pico y placa Alfred")
                    .setChannelId(CHANNEL_ID)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                val currentDay: Int = Calendar.getInstance().getTime().day
                var carNotifPicoYPlaca= carNotifications.filter { it.plate.get(it.plate.length-1).toString().toInt()%2==currentDay%2 }
                val carNotifPicoYPlacaNum= carNotifPicoYPlaca.size
                if(carNotifPicoYPlacaNum>0)
                    for(i in 0..carNotifPicoYPlacaNum-1){
                        builder.setContentText("El carro "+ carNotifPicoYPlaca[i].name +" de placa " +carNotifPicoYPlaca[i].plate+ " tiene pico y placa")
                        with(NotificationManagerCompat.from(this)) {
                            notify(i+1, builder.build())
                        }
                    }
            }


        }

    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT).apply {
                lightColor = R.color.l_dark_blue
                enableLights(true)
            }
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
// ...

// Before you perform the actual permission request, check whether your app
// already has the permissions, and whether your app needs to show a permission
// rationale dialog. For more details, see Request permissions.


}