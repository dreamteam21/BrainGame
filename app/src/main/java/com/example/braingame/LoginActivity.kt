package com.example.braingame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.toast

class LoginActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    private val TAG: String = "Login Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        if(mAuth!!.currentUser != null){
            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
            finish()
        }

        loginBtn.setOnClickListener {
            val email = loginEmail.text.toString().trim { it <= ' ' }
            val password = loginPassword.text.toString().trim { it <= ' ' }

            if(email.isEmpty()){
                toast("Please Enter Email Address")
                Log.d(TAG, "Email was empty")
                return@setOnClickListener
            }
            if(password.isEmpty()){
                toast("Please Enter Password")
                Log.d(TAG, "Password was empty")
                return@setOnClickListener
            }

            mAuth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if(!task.isSuccessful){
                    toast("Authentication Failed: "+task.exception)
                    Log.d(TAG, "Authentication Failed: "+task.exception)
                }
                else{
                    toast("Login successfully!")
                    Log.d(TAG, "Login successfully!")
                    startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                    finish()
                }
            }
        }
    }
}