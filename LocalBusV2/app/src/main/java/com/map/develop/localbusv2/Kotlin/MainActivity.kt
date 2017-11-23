package com.map.develop.localbusv2.Kotlin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.map.develop.localbusv2.R


class MainActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {


    private var photoImageView: ImageView? = null
    private var nameTextView: TextView? = null
    private var emailTextView: TextView? = null
    private var idTextView: TextView? = null

    private var googleApiClient: GoogleApiClient? = null

    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseAuthListener: FirebaseAuth.AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        photoImageView = findViewById<View>(R.id.photoImageView) as ImageView
        nameTextView = findViewById<View>(R.id.nameTextView) as TextView
        emailTextView = findViewById<View>(R.id.emailTextView) as TextView
        idTextView = findViewById<View>(R.id.idTextView) as TextView

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

        googleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                setUserData(user)
            } else {
                goLoginActivity()
            }
        }
    }

    private fun setUserData(user: FirebaseUser?) {
        nameTextView!!.text = user!!.displayName
        emailTextView!!.text = user.email
        idTextView!!.text = user.uid
        Glide.with(this).load(user.photoUrl).into(photoImageView!!)
    }


    override fun onStart() {
        super.onStart()

        firebaseAuth!!.addAuthStateListener(firebaseAuthListener!!)
        /*OptionalPendingResult<GoogleSignInResult> opr =Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if(opr.isDone())
        {
            GoogleSignInResult result=opr.get();
            handleSignInResult(result);
        }
        else
        {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }*/
    }

    override fun onStop() {
        super.onStop()
        if (firebaseAuthListener != null) {
            firebaseAuth!!.removeAuthStateListener(firebaseAuthListener!!)
        }
    }

    //Llamada silenciosa a firebase solo trae datos
    /*private void handleSignInResult(GoogleSignInResult result) {
        if(result.isSuccess())
        {
            GoogleSignInAccount account=result.getSignInAccount();
            nameTextView.setText(account.getDisplayName());
            emailTextView.setText(account.getEmail());
            idTextView.setText(account.getId());
            Glide.with(this).load(account.getPhotoUrl()).into(photoImageView);
            //Log.d("MIAPP", account.getPhotoUrl().toString());
        }else
        {
            goLoginActivity();
        }
    }*/

    private fun goLoginActivity() {

        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    fun goMap(view: View) {
        val intent = Intent(this, MapsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

    }

    //Seccion de Botones Log Out y Revoke
    fun logOut(view: View) {
        firebaseAuth!!.signOut()
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback { status ->
            if (status.isSuccess) {
                goLoginActivity()
            } else {
                Toast.makeText(applicationContext, "No se pudo cerrar sesión", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun revoke(view: View) {
        firebaseAuth!!.signOut()
        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback { status ->
            if (status.isSuccess) {
                goLoginActivity()
            } else {
                Toast.makeText(applicationContext, "No se pudo eliminar el acceso", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
