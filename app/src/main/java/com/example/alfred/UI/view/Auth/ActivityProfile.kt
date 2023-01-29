package com.example.alfred.UI.view.Auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.example.alfred.R
import com.example.alfred.UI.viewmodel.user.UserViewModel
import com.example.alfred.data.model.User
import com.example.alfred.utilities.UtilityInjector
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class ActivityProfile : AppCompatActivity() {


    lateinit var currentUser: FirebaseUser
    lateinit var usuario : User
    private val PICK_IMAGE_REQUEST = 71
    private var userId:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        currentUser = FirebaseAuth.getInstance().currentUser!!
        val bundle =intent.extras
        val idService = bundle?.getString("idService")
        findViewById<Button>(R.id.buttonFoto).setOnClickListener {

                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)

            }

        }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch{
            getUser()
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null) {
                return
            }
            var fotoPrefil = findViewById<ImageView>(R.id.fotoPerfil)
            fotoPrefil.setImageURI(data.data)
        }
    }


    suspend fun getUser(){
        val UserFactory = UtilityInjector.provideUserViewModelFactory(applicationContext)
        val UserViewModel = ViewModelProviders.of(this, UserFactory)[UserViewModel::class.java]
        var userId = ""

        if (currentUser != null)
            userId = currentUser!!.uid
        UserViewModel.getUserById(userId).observe(this) { user ->
            usuario = user
        }
        if(usuario != null){
            val name= findViewById<MaterialTextView>(R.id.name)
            val cellphone= findViewById<MaterialTextView>(R.id.cellphone)
            val email= findViewById<MaterialTextView>(R.id.email)


            name.text = usuario.name
            cellphone.text = usuario.cellphone
            email.text = usuario.email

        }

    }



}