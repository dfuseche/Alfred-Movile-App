package com.example.alfred

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.example.alfred.UI.view.Auth.ActivityProfile
import com.example.alfred.UI.view.Car.RegisterCarActivity
import com.example.alfred.UI.viewmodel.car.CarViewModel
import com.example.alfred.data.model.Car
import com.example.alfred.databinding.ActivityCarBinding
import com.example.alfred.databinding.ActivityLoginBinding
import com.example.alfred.utilities.UtilityInjector
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ActivityCar : AppCompatActivity() {

    lateinit var imageView: ImageView
    lateinit var button: Button
    lateinit var buttonPerfil: Button
    val REQUEST_IMAGE_CAPTURE = 100
    val CAMERA_PERM_CODE = 101;
    lateinit var currentPhotoPath: String
    lateinit var storageReference: StorageReference
    lateinit var currentUser: FirebaseUser
    var carListSaved: List<Car> = emptyList<Car>()
    var carNum=0
    var startTime: Long = 0
    var endTime: Long = 0
    private lateinit var binding: ActivityCarBinding
    /***
     *  Metodos
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        startTime = System.currentTimeMillis();
        currentUser = FirebaseAuth.getInstance().currentUser!!

        imageView = binding.carButton
        button = binding.btnTakePicture
        buttonPerfil = binding.butonHaciaPerfil

        storageReference = FirebaseStorage.getInstance().reference;

        button.setOnClickListener{
            askCameraPermissions()

            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try{
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)

            }catch (e: ActivityNotFoundException){
                Toast.makeText(this, "Error:" + e.localizedMessage,Toast.LENGTH_SHORT).show()
            }
        }
        buttonPerfil.setOnClickListener{
            val intent = Intent(this, ActivityProfile::class.java)
            startActivity(intent)
        }

    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch{
            getUserCar()
        }

    }
    suspend fun getUserCar(){
        val carFactory = UtilityInjector.provideCarViewModelFactory(applicationContext)
        val carViewModel = ViewModelProviders.of(this, carFactory)[CarViewModel::class.java]
        var userId = ""

        if (currentUser != null)
            userId = currentUser!!.uid
        carViewModel.getCarsByUserId(userId, applicationContext).observe(this) { cars ->
            carListSaved = cars
            carNum = carListSaved.size
        }
        if(carListSaved.isNotEmpty()){
            var car = carListSaved.get(0);
            val descriptionCar= binding.descripcionDetail
            val claseVehiculo= binding.claseVehiculo
            val lineaVehiculo= binding.lineaVehiculo
            val fechaSoat= binding.fechaSoat
            val lugarMatricula = binding.lugarMatricula
            val ciudad3= binding.Ciudad3

            descriptionCar.text = car.description
            claseVehiculo.text = car.model
            lineaVehiculo.text = car.plate
            fechaSoat.text = car.soat_date.toString()
            lugarMatricula.text = car.country
            ciudad3.text = car.city

        }

    }


    private fun askCameraPermissions() {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA ), CAMERA_PERM_CODE)
        }else{
            dispatchTakePictureIntent();
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            if(requestCode == Activity.RESULT_OK){
                val f: File? = File(currentPhotoPath)
                imageView.setImageURI(Uri.fromFile(f))
                Log.d("tag", "Absolute Url of image" + Uri.fromFile(f))

                /**
                 *  agregar a firebase
                 */
                val contentURi: Uri = Uri.fromFile(f)
                if (f != null) {
                    uploadImageToFirebase(f.name, contentURi)
                };
            }
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(imageBitmap)


        }
        else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun uploadImageToFirebase(name: String, contentURi: Uri) {
        val image: StorageReference = storageReference.child("cars/" + name)
        image.putFile(contentURi).addOnSuccessListener {
            Toast.makeText(this, "Upload succesful", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == CAMERA_PERM_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                dispatchTakePictureIntent();
            }
            else{
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT)
            }
        }
        else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }


    }



    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    override fun onDestroy() {
        endTime = System.currentTimeMillis()
        val timeSpend = endTime - startTime
        // Insert timeSpend in databse.
        Toast.makeText(baseContext, "Tiempo tomado en actividad$timeSpend" , Toast.LENGTH_SHORT).show()
//        Crashlytics.logException(new RuntimeException("Tiempo tomado en actividad$timeSpend"));
        super.onDestroy()
    }
    private fun dispatchTakePictureIntent() {
//        val takePictureIntent =
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    Toast.makeText(this, "ocurrio error", Toast.LENGTH_SHORT).show()
                    // Error occurred while creating the File
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }

//        startActivity(takePictureIntent)


    }

}
