package com.map.develop.localbusv2.Java;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.map.develop.localbusv2.Kotlin.LoginActivity;
import com.map.develop.localbusv2.Kotlin.MapsActivity;
import com.map.develop.localbusv2.R;

/**
 * Created by Mario on 15/11/2017.
 */

public class main extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private ImageView photoImageView;
    private TextView nameTextView;
    private TextView emailTextView;
    private TextView idTextView;

    private GoogleApiClient googleApiClient;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        photoImageView=(ImageView) findViewById(R.id.photoImageView);
        nameTextView=(TextView) findViewById(R.id.nameTextView);
        emailTextView=(TextView)findViewById(R.id.emailTextView);
        idTextView=(TextView)findViewById(R.id.idTextView);

        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user =firebaseAuth.getCurrentUser();
                if(user !=null)
                {
                    setUserData(user);
                }
                else
                {
                    goLoginActivity();
                }
            }
        };
    }

    private void setUserData(FirebaseUser user) {
        nameTextView.setText(user.getDisplayName());
        emailTextView.setText(user.getEmail());
        idTextView.setText(user.getUid());
        Glide.with(this).load(user.getPhotoUrl()).into(photoImageView);
    }


    @Override
    protected void onStart() {
        super.onStart();

        firebaseAuth.addAuthStateListener(firebaseAuthListener);
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

    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseAuthListener!=null)
        {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
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

    private void goLoginActivity() {

        Intent intent=new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    public void goMap(View view)
    {
        Intent intent=new Intent(this, MapsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //Seccion de Botones Log Out y Revoke
    public void logOut(View view)
    {
        firebaseAuth.signOut();
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if(status.isSuccess())
                {
                    goLoginActivity();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"No se pudo cerrar sesión",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void revoke(View view)
    {
        firebaseAuth.signOut();
        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if(status.isSuccess())
                {
                    goLoginActivity();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"No se pudo eliminar el acceso",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
