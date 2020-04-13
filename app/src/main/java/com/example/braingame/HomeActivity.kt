package com.example.braingame

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.navigation_header.*
import org.jetbrains.anko.toast
import java.util.*

class HomeActivity : AppCompatActivity(), Observer {

    var mAuth: FirebaseAuth? = null
    var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private val TAG: String = "Home Activity"
    private var mScoreListAdapter: ScoreCardAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        ScoreModel.addObserver(this)
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
        val progressBar = headerView.findViewById<ProgressBar>(R.id.homeHeaderProgressBar)
        progressBar.visibility = ProgressBar.VISIBLE
        val homeHeaderUserName = headerView.findViewById<TextView>(R.id.homeHeaderUserName)
        val homeDisplay = headerView.findViewById<ImageView>(R.id.homeProfileDisplay)
        if(user?.photoUrl != null) {
            Glide.with(this).load(user?.photoUrl).into(homeDisplay)
        }
        if(user?.displayName != null){
            homeHeaderUserName.text = user?.displayName
        }
        else{
            homeHeaderUserName.text = user?.email
        }
        progressBar.visibility = ProgressBar.INVISIBLE
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

    override fun onResume() {
        super.onResume()
    }

    override fun update(o: Observable?, arg: Any?) {
        mScoreListAdapter?.clear()

        val data = ScoreModel.getData()
        if(data != null){
            setData(data)
        }
    }

    private fun setData(data: List<Score>) {
        mScoreListAdapter?.clear()
        mScoreListAdapter?.addAll(data.sortedByDescending { score -> score.score })
        mScoreListAdapter?.notifyDataSetChanged()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.navigationLogout) {
            val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
            alertDialog.setTitle(resources.getString(R.string.home_dialog_logout_title))
            alertDialog.setMessage(resources.getString(R.string.home_dialog_logout_message))
            alertDialog.setPositiveButton(resources.getString(R.string.home_dialog_logout_yes)){_, _ ->
                mAuth!!.signOut()
                toast(R.string.home_logout_message)
                Log.d(TAG, "Logout")
                startActivity(Intent(this@HomeActivity, MainActivity::class.java))
                finish()
            }
            alertDialog.setNegativeButton(resources.getString(R.string.home_dialog_logout_no)){_, _ -> }
            alertDialog.show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
