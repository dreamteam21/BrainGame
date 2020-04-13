package com.example.braingame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.fragment_profile.*
import org.jetbrains.anko.toast

class RegisterActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
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
            val name = regisName.text.toString().trim() {it <= ' ' }
            val email =  regisEmail.text.toString().trim { it <= ' ' }
            val password = regisPassword.text.toString().trim { it <= ' ' }
            val confirmPassword = regisConfirmPassword.text.toString().trim { it <= ' ' }

            if(name.isEmpty()){
                regisProgressBar.visibility = ProgressBar.INVISIBLE
                toast(R.string.register_enter_name)
                Log.d(TAG, "Name was empty")
                return@setOnClickListener
            }

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
                    val score = Score(null)
                    score.uid = mAuth!!.currentUser?.uid.toString()
                    score.name = name
                    score.score = "0"
                    ScoreModel.saveScore(score, OnCompleteListener { task ->
                        if(task.isComplete){
                            Log.d(TAG, "create user entry in db successfully.")
                        }
                    })
                    val updateProfile = UserProfileChangeRequest.Builder().setDisplayName(name).build()
                    mAuth!!.currentUser?.updateProfile(updateProfile)?.addOnCompleteListener {
                        if (task.isSuccessful) {
                            Log.d(TAG, "update uname successfully")
                        } else {
                            Log.d(TAG, "update uname unsuccessfully")
                        }
                    }
                    regisProgressBar.visibility = ProgressBar.INVISIBLE
                    toast(R.string.register_successful)
                    Log.d(TAG, "Create account successfully!")
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    finish()
                }
            }
        }
        regisBackBtn.setOnClickListener {
            onBackPressed()
        }
    }
}