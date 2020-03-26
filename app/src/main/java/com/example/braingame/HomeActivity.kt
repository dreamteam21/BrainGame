package com.example.braingame

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.navigation_header.*
import org.jetbrains.anko.toast

class HomeActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private val TAG: String = "Home Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        mAuth = FirebaseAuth.getInstance()

        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val users = firebaseAuth.currentUser
            if(users == null){
                startActivity(Intent(this@HomeActivity, MainActivity::class.java))
                finish()
            }
        }

        val navigationController = Navigation.findNavController(this, R.id.homeFragment)
        NavigationUI.setupWithNavController(navigationView, navigationController)
        NavigationUI.setupActionBarWithNavController(this, navigationController, homeDrawerLayout)

        val user = mAuth!!.currentUser
//        homeEmail.text = user!!.email
//        homeUID.text = user.uid
        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        val headerView = navigationView.getHeaderView(0)
        val homeHeaderUserEmail = headerView.findViewById<TextView>(R.id.homeHeaderUserEmail)
        homeHeaderUserEmail.setText(user!!.email)
    }
    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(Navigation.findNavController(this, R.id.homeFragment), homeDrawerLayout)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.navigationLogout) {
            mAuth!!.signOut()
            toast("Logout!")
            Log.d(TAG, "Logout")
            startActivity(Intent(this@HomeActivity, MainActivity::class.java))
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
