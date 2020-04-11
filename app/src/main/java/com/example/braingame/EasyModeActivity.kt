package com.example.braingame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_easy_mode.*

class EasyModeActivity : AppCompatActivity() {

    var mAuth = FirebaseAuth.getInstance()
    var currentUser = mAuth.currentUser
//    var currentScore = currentUser?.uid?.let { Score(it, "test",0) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_easy_mode)

        var tempArr : ArrayList<Score>? = ScoreModel.getData()
        if(tempArr != null){
            for(score in tempArr){
                if(score.uid == currentUser?.uid){
                    Log.d("EASY", "SET TEXT COMPLETED")
                    easy_showScore.text = score.name
                    break
                }
            }
        }
        else{
            easy_showScore.text = "NULL"
        }



//        if (currentScore != null) {
//            easy_showScore.text = currentScore!!.score.toString()
//        }
//
//
//        if (currentScore != null) {
//            easy_scoreUpBtn.setOnClickListener {
//                currentScore!!.score++
//                easy_showScore.text = currentScore!!.score.toString()
//            }
//        }

        easy_backToHomeBtn.setOnClickListener {
            startActivity(Intent(this@EasyModeActivity, HomeActivity::class.java))
        }
    }

//    private fun saveScore(){
//        val ref = FirebaseDatabase.getInstance().getReference("score")
//        if(ref.child("score").equalTo(currentUser!!.uid).once)
//    }
}
