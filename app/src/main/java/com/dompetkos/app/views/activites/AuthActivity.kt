package com.dompetkos.app.views.activites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dompetkos.app.R
import com.dompetkos.app.utils.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import io.realm.mongodb.Credentials
import io.realm.mongodb.Credentials.google

class AuthActivity : AppCompatActivity() {
    private lateinit var client: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestServerAuthCode(Constants.CLIENT_ID)
            .build()

        client = GoogleSignIn.getClient(this, gso)

        val btnSignIn = findViewById<SignInButton>(R.id.btn_sign_in)

        btnSignIn.setOnClickListener {

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)

        }
    }

    private fun handleSignInResult(account: GoogleSignInAccount?) {
        try {
            val code = account?.serverAuthCode
            val googleCredential = google(code!!)

        } catch (e: ApiException) {
            e.printStackTrace()
        }
    }

    private fun signIn() {
        val signInIntent = client.signInIntent
        startActivityForResult(signInIntent, 100)
    }


}