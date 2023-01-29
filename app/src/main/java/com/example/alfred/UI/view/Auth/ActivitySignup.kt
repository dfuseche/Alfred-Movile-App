package com.example.alfred

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.example.alfred.UI.view.Home.ActivityHome
import com.example.alfred.UI.view.Home.PopUpNetworkActivity
import com.example.alfred.UI.view.Home.ProviderType
import com.example.alfred.UI.viewmodel.user.UserViewModel
import com.example.alfred.data.model.User
import com.example.alfred.databinding.ActivitySignUpBinding
import com.example.alfred.utilities.UtilityInjector
import com.example.trapp.utilities.NetworkTracker
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class ActivitySignup : AppCompatActivity() {

    private lateinit var binding:ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    val EMAIL_ADDRESS_PATTERN = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_sign_up)
        auth = FirebaseAuth.getInstance()
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integración de Firebase completa")
        analytics.logEvent("InitScreen", bundle)
        setup()
    }
    fun isValidEmail(str: String): Boolean{
        return EMAIL_ADDRESS_PATTERN.matcher(str).matches()
    }

    private fun setup() {
        title = "Autenticación"

        //Variables formulario
        val buttonClick = binding.signUpButton2
        val correo = binding.correoSignUp
        val contrasenia = binding.contraseniaSignUp
        val nombre = binding.nombreSignUp
        val celular = binding.celularSignUp
        val currentUser = FirebaseAuth.getInstance().currentUser
        val tracker= NetworkTracker.getInstance()
        val errorNombre = binding.inputNombre
        val errorCorreo = binding.correoInput
        val errorCelular = binding.celularInput
        val errorContrasenia = binding.contraseniaInput
        //Autenticación
        buttonClick.setOnClickListener {
            if(!tracker.getInternet(applicationContext)){
                val intent = Intent(this, PopUpNetworkActivity::class.java)
                startActivity(intent)
            }

            if(nombre.text.toString().isEmpty()){
                errorNombre.setError("Se requiere el nombre")
            }
            else{
                errorNombre.setErrorEnabled(false)
            }
            if(correo.text.toString().isEmpty()){
                errorCorreo.setError("Se requiere el correo")
            }
            else{
                errorCorreo.setErrorEnabled(false)
            }
            if(!isValidEmail(correo.text.toString())){
                errorCorreo.setError("Inserte un correo valido")
            }
            else{
                errorCorreo.setErrorEnabled(false)
            }
            if(celular.text.toString().isEmpty()){
                errorCelular.setError("Se requiere el número de celular")
            }
            else if (celular.text!!.length != 10){
                errorCelular.setError("El número tiene que contener 10 digitos")
            }else
            {
                errorCelular.setErrorEnabled(false)
            }
            if(contrasenia.text!!.length < 6)
            {
                errorContrasenia.setError("La contraseña tiene que contener más de 6 caracteres")
            }
            else{
                errorContrasenia.setErrorEnabled(false)
            }


            if(correo.text!!.isNotEmpty() && contrasenia.text!!.isNotEmpty() && nombre.text!!.isNotEmpty() && celular.text!!.isNotEmpty() && contrasenia.text!!.length>6 &&celular.text!!.length == 10 && isValidEmail(correo.text.toString()))
            {
                auth.createUserWithEmailAndPassword(correo.text.toString().trim(),
                                            contrasenia.text.toString().trim()).addOnCompleteListener(this) { task->
                                  if(task.isSuccessful) {

                                          val userFactory =
                                              UtilityInjector.provideUserViewModelFactory(applicationContext)
                                          val userViewModel =
                                             ViewModelProviders.of(this, userFactory)[UserViewModel::class.java]

                                          lifecycleScope.launch {
                                              userViewModel.createUser(
                                                  User(
                                                      currentUser?.uid.toString(),
                                                      correo.text.toString(),
                                                      nombre.text.toString(),
                                                      celular.text.toString()
                                                  )
                                              )





                                          showHome(correo.text.toString().trim(), ProviderType.BASIC)
                                      }


                                  } else {
                                      showAlert(task.result.toString())
                                  }
                }
            }


        }

    }

    private fun showAlert(resultado:String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario" + resultado)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String, provider: ProviderType)
    {
        val homeIntent = Intent(this, ActivityHome::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)

        }
        startActivity(homeIntent)
    }
}