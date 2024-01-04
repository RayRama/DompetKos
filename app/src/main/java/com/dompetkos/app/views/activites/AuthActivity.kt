package com.dompetkos.app.views.activites

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dompetkos.app.R
import com.dompetkos.app.utils.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.drive.Drive


class AuthActivity : AppCompatActivity() {
    private lateinit var client: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestServerAuthCode(Constants.CLIENT_ID)
            .requestIdToken(Constants.CLIENT_ID)
            .requestScopes(Drive.SCOPE_APPFOLDER, Drive.SCOPE_FILE)
            .build()

        client = GoogleSignIn.getClient(this, gso)

        val btnSignIn = findViewById<SignInButton>(R.id.btn_sign_in)

        btnSignIn.setOnClickListener {
            signIn()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100) {
            try {
                val account = GoogleSignIn.getSignedInAccountFromIntent(data)
                    .getResult(ApiException::class.java)
                handleSignInResult(account)
            } catch (e: ApiException) {
                Log.d("AuthActivity", "onActivityResult: " + e.statusCode)
                Log.d("AuthActivity", "onActivityResult: " + e.message)
                e.printStackTrace()
            }
        }
    }

    private fun handleSignInResult(account: GoogleSignInAccount?) {

        val sharedPreferences = getSharedPreferences("com.dompetkos.app", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isSignedIn", true)
        editor.commit()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
        Toast.makeText(this, "Signed in successfully", Toast.LENGTH_SHORT).show()


    }

    private fun signIn() {
        val signInIntent = client.signInIntent
        startActivityForResult(signInIntent, 100)
    }


}