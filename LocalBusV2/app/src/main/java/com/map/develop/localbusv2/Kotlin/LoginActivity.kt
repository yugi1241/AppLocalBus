package com.map.develop.localbusv2.Kotlin

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.map.develop.localbusv2.R

class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    private var googleApiClient: GoogleApiClient? = null
    private var signInButton: SignInButton? = null
    val SIGN_IN_CODE = 777
    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseAuthListener: FirebaseAuth.AuthStateListener? = null
    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Se inicializa opciones de entrada
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //Obtener un Token de firebase
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        //Aqui se Inicializa el cliente de Google
        googleApiClient = GoogleApiClient.Builder(this)
                //AutManage se gestion el cicl ode vida del google api client con el activity
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()

        //Darle una accion al boton signInButton
        signInButton = findViewById<View>(R.id.signInButton) as SignInButton
        signInButton!!.setSize(SignInButton.SIZE_WIDE)
        signInButton!!.setOnClickListener {
            val intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
            startActivityForResult(intent, SIGN_IN_CODE)
        }

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            //oyente de firebase
            val user = firebaseAuth.currentUser
            if (user != null) {
                goMainActivity()
            }
        }
        progressBar = findViewById<View>(R.id.progressBar) as ProgressBar

    }

    override fun onStart() {
        super.onStart()

        firebaseAuth!!.addAuthStateListener(firebaseAuthListener!!)
    }

    override fun onStop() {
        super.onStop()
        if (firebaseAuthListener != null) {
            firebaseAuth!!.removeAuthStateListener(firebaseAuthListener!!)
        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        // se puede mostrar un mensaje cuando algo sale mal en la conexion
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_CODE) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            handleSignInResult(result)
        }
    }

    //Este metodo comprueba si la operacion fue exitosa
    private fun handleSignInResult(result: GoogleSignInResult) {
        if (result.isSuccess) {
            //Si es exitoso manda a llamar a MainActivity
            //goMainActivity();

            firebaseAuthWithGoogle(result.signInAccount)
        } else {
            Toast.makeText(this, "No se pudo iniciar sesión", Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(signInAccount: GoogleSignInAccount?) {
        //Muestra un progress bar para autenticacion en firebase y oculta el boton de google
        progressBar!!.visibility = View.VISIBLE
        signInButton!!.visibility = View.GONE

        val credential = GoogleAuthProvider.getCredential(signInAccount!!.idToken, null)
        firebaseAuth!!.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            progressBar!!.visibility = View.GONE
            signInButton!!.visibility = View.VISIBLE
            if (!task.isSuccessful) {
                Toast.makeText(applicationContext, "No se puede autenticar con Firebase", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun goMainActivity() {
        //que nunca se quede una atras de la otra
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}


