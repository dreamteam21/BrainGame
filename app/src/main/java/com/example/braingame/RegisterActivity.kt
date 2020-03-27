package com.example.braingame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.toast

class RegisterActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    private val TAG: String = "Signup Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()

        if(mAuth!!.currentUser != null){
            startActivity(Intent(this@RegisterActivity, HomeActivity::class.java))
            finish()
        }

        regisBtn.setOnClickListener {
            regisProgressBar.visibility = ProgressBar.VISIBLE
            val email =  regisEmail.text.toString().trim { it <= ' ' }
            val password = regisPassword.text.toString().trim { it <= ' ' }
            val confirmPassword = regisConfirmPassword.text.toString().trim { it <= ' ' }

            if(email.isEmpty()){
                regisProgressBar.visibility = ProgressBar.INVISIBLE
                toast(R.string.register_enter_email)
                Log.d(TAG, "Email was empty")
                return@setOnClickListener
            }
            if(password.isEmpty()){
                regisProgressBar.visibility = ProgressBar.INVISIBLE
                toast(R.string.register_enter_password)
                Log.d(TAG, "Password was empty")
                return@setOnClickListener
            }

            if(confirmPassword.isEmpty()){
                regisProgressBar.visibility = ProgressBar.INVISIBLE
                toast(R.string.register_re_enter_password)
                Log.d(TAG, "Confirm password was empty")
                return@setOnClickListener
            }

            if(password != confirmPassword){
                regisProgressBar.visibility = ProgressBar.INVISIBLE
                toast(R.string.register_password_not_match)
                Log.d(TAG, "Passwords are not match")
                return@setOnClickListener
            }

            mAuth!!.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if(!task.isSuccessful){
                    regisProgressBar.visibility = ProgressBar.INVISIBLE
                    toast(R.string.register_auth_fail)
                    Log.d(TAG, "Authentication Failed: "+task.exception)
                }
                else{
                    regisProgressBar.visibility = ProgressBar.INVISIBLE
                    toast(R.string.register_successful)
                    Log.d(TAG, "Create account successfully!")
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    finish()
                }
            }
        }
    }
}