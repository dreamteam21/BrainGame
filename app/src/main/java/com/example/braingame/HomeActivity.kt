package com.example.braingame

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
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

        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val users = firebaseAuth.currentUser
            if(users == null){
                startActivity(Intent(this@HomeActivity, MainActivity::class.java))
                finish()
            }
        }

        val navigationController = Navigation.findNavController(this, R.id.homeFragment)
        NavigationUI.setupWithNavController(view_navigation, navigationController)
        NavigationUI.setupActionBarWithNavController(this, navigationController, homeDrawerLayout)

        val user = mAuth!!.currentUser
        val headerView = view_navigation.getHeaderView(0)
        val homeHeaderUserEmail = headerView.findViewById<TextView>(R.id.homeHeaderUserEmail)
        val homeDisplay = headerView.findViewById<ImageView>(R.id.homeProfileDisplay)
        if(user?.photoUrl != null) {
            Glide.with(this).load(user?.photoUrl).into(homeDisplay)
        }
        if(!user?.displayName.isNullOrEmpty()){
            homeHeaderUserEmail.text = user?.displayName
        }else{
            homeHeaderUserEmail.text = user?.email
        }
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
            toast(R.string.home_logout_message)
            Log.d(TAG, "Logout")
            startActivity(Intent(this@HomeActivity, MainActivity::class.java))
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
