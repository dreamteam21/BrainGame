package com.example.braingame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*
import org.jetbrains.anko.toast

class HomeActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private val TAG: String = "Home Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        mAuth = FirebaseAuth.getInstance()

        val user = mAuth!!.currentUser

        homeEmail.text = user!!.email
        homeUID.text = user.uid

        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val users = firebaseAuth.currentUser
            if(users == null){
                startActivity(Intent(this@HomeActivity, MainActivity::class.java))
                finish()
            }
        }

        homeSignoutBtn.setOnClickListener {
            mAuth!!.signOut()
            toast("Logout!")
            Log.d(TAG, "Logout")
            startActivity(Intent(this@HomeActivity, MainActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        mAuth!!.addAuthStateListener { mAuthListener }
    }

    override fun onStop() {
        super.onStop()
        if(mAuthListener != null){
            mAuth!!.removeAuthStateListener { mAuthListener }
        }
    }

    //all user to press back to go back to previous task
//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        if(keyCode == KeyEvent.KEYCODE_BACK){
//            moveTaskToBack(true)
//        }
//        return super.onKeyDown(keyCode, event)
//    }
}
