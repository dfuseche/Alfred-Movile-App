package com.example.alfred

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.example.alfred.UI.view.Home.ActivityHome
import com.example.alfred.UI.view.Home.PopUpNetworkActivity
import com.example.alfred.UI.view.Home.ProviderType
import com.example.alfred.databinding.ActivityAliadosBinding
import com.example.alfred.databinding.ActivityLoginBinding
import com.example.trapp.utilities.NetworkTracker
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class ActivityLogin : AppCompatActivity() {

    private val GOOGLE_SIGN_IN = 100
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setup()
        session()

        val buttonClick = binding.signUpButton

        buttonClick.setOnClickListener {
            val intent = Intent(this, ActivitySignup::class.java)
            startActivity(intent)
        }

}
    private fun session() {
        val prefs = getSharedPreferences("com.example.alfred.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)

        if(email != null)
        {

            showHome(email, ProviderType.BASIC )
        }
    }
    private fun setup() {
        title = "Inicio Sesión"

        //Variables formulario
        val buttonClick = binding.crearSesiNButton
        val correo = binding.editTextTextEmailAddressLogin
        val contrasenia = binding.editTextPasswordlogin
        val tracker= NetworkTracker.getInstance()
        //Autenticación
        buttonClick.setOnClickListener {
            if(!tracker.getInternet(applicationContext)){
                val intent = Intent(this,PopUpNetworkActivity::class.java)
                startActivity(intent)
            }
            if(correo.text.isNotEmpty() && contrasenia.text.isNotEmpty())
            {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(correo.text.toString(),
                    contrasenia.text.toString()).addOnCompleteListener {
                    if(it.isSuccessful) {
                        showHome(it.result?.user?.email ?:"", ProviderType.BASIC)
                    } else {
                        showAlert(it.exception.toString())
                    }
                }
            }

        }
        val googleButton = binding.googleButton

        googleButton.setOnClickListener {
            //Configuración
            val googleConfig = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val googleClient = GoogleSignIn.getClient(this, googleConfig)
            googleClient.signOut()

            resultLauncher.launch(googleClient.signInIntent)
        }



    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

        if(result.resultCode == Activity.RESULT_OK) {

            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                showHome(account.email ?: "", ProviderType.GOOGLE)
                            } else {
                                showAlert(it.exception.toString())
                            }
                        }

                }

            } catch (e: ApiException) {
                showAlert(e.toString())
            }
        }

    }

    private fun showAlert(error: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        val errorDescription = showFireBaseError(error)
        builder.setMessage("Se ha producido un error autenticando al usuario: " + errorDescription)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showFireBaseError(error: String): String
    {
        if(error.contains("There is no user record corresponding to this identifier"))
        {
            return "El usuario ingresado no existe"
        }
        else if(error.contains("The email address is badly formatted"))
        {
            return "El correo ingresado no es válido  "
        }
        else if(error.contains("The password is invalid or the user"))
        {
            return "contraseña incorrecta"
        }
        else if(error.contains("A network error"))
        {
            return "No hay conexión a internet"
        }
        else{
            return "Usuario no válido"
        }


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