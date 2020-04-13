package com.example.braingame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
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
            loginProgressBar.visibility = ProgressBar.VISIBLE
            val email = loginEmail.text.toString().trim { it <= ' ' }
            val password = loginPassword.text.toString().trim { it <= ' ' }

            if(email.isEmpty()){
                toast(R.string.login_enter_email)
                Log.d(TAG, "Email was empty")
                loginProgressBar.visibility = ProgressBar.INVISIBLE
                return@setOnClickListener
            }
            if(password.isEmpty()){
                toast(R.string.login_enter_password)
                Log.d(TAG, "Password was empty")
                loginProgressBar.visibility = ProgressBar.INVISIBLE
                return@setOnClickListener
            }

            mAuth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if(!task.isSuccessful){
                    loginProgressBar.visibility = ProgressBar.INVISIBLE
                    toast(R.string.login_auth_fail)
                    Log.d(TAG, "Authentication Failed: "+task.exception)
                }
                else{
                    loginProgressBar.visibility = ProgressBar.INVISIBLE
                    toast(R.string.login_successful)
                    Log.d(TAG, "Login successfully!")
                    startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                    finish()
                }
            }
        }
        loginBackBtn.setOnClickListener {
            onBackPressed()
        }
    }
}