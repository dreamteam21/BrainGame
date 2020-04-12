package com.example.braingame

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Layout
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_easy_mode.*
import org.jetbrains.anko.toast
import kotlin.random.Random
import org.threeten.bp.LocalDateTime
import kotlin.time.milliseconds

class EasyModeActivity : AppCompatActivity() {

    private var mAuth = FirebaseAuth.getInstance()
    private var currentUser = mAuth.currentUser
    private val TAG: String = "Easy Mode"
    private var inGameScore: Int = 0
    private var numClick: Int = 0
    private var gamePlayed: Int = 1
    private lateinit var score: Score
    private lateinit var correctAnswer: String



    private val questionArray: ArrayList<ArrayList<String>> = ArrayList<ArrayList<String>>()
    private val questionData: Array<Array<String>> = arrayOf(
        //   "image_name", "correct answer"
        arrayOf("easy_q1", "4"),
        arrayOf("easy_q2", "5")
//        arrayOf("easy_q3", "3"),
//        arrayOf("easy_q4", "4"),
//        arrayOf("easy_q5", "5"),
//        arrayOf("easy_q6", "6"),
//        arrayOf("easy_q7", "7"),
//        arrayOf("easy_q8", "8"),
//        arrayOf("easy_q9", "9"),
//        arrayOf("easy_q10", "10")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_easy_mode)

        var tempArr : ArrayList<Score>? = ScoreModel.getData()
        score = Score(null)
        if(tempArr != null){
            for(user in tempArr){
                if(user.uid == currentUser?.uid){
                    Log.d(TAG, "RETRIEVED USER INFORMATION COMPLETED")
                    score.score = user.score
                    score.uid = user.uid
                    score.name = user.name
                    break
                }
            }
        }
        gameInitialize()
    }

    private fun gameInitialize(){
        val initialTimer = object: CountDownTimer(3000, 1000){
            override fun onTick(millisUntilFinished: Long) {
                gameEasyStartShowTime.text = ((millisUntilFinished/1000)+1).toString()
            }

            override fun onFinish() {
                gameEasyLayout.visibility = LinearLayout.VISIBLE
                for(question in questionData){
                    val tempArrayList: ArrayList<String> = ArrayList<String>()
                    tempArrayList.add(question[0])
                    tempArrayList.add(question[1])
                    questionArray.add(tempArrayList)
                }
                showQuestion()
            }
        }
        initialTimer.start()
    }

    private fun showQuestion(){
        val random: java.util.Random = java.util.Random()
        val randomNumber: Int = random.nextInt(questionArray.size)
        val thisQuestion: ArrayList<String> = questionArray[randomNumber]
        numClick = 0
        gameEasyImageView.setImageResource(
            resources.getIdentifier(thisQuestion[0], "drawable", packageName)
        )
        correctAnswer = thisQuestion[1]
        questionArray.removeAt(randomNumber)
        val gameTimer = object: CountDownTimer(5000, 1000){
            override fun onTick(millisUntilFinished: Long) {
                gameEasyShowTime.text = ((millisUntilFinished/1000)+1).toString()
                gameEasyNumBlock.text = numClick.toString()
                gameEasyButton.setOnClickListener {
                    numClick++
                    gameEasyNumBlock.text = numClick.toString()
                }
            }
            override fun onFinish() {
                gameEasyShowTime.text = "0"
                checkAnswer()
            }
        }
        gameTimer.start()
    }

    private fun checkAnswer(){
        var alertDialogTitle : String
        val positiveButton : String
        if(correctAnswer == numClick.toString()){
            alertDialogTitle = resources.getString(R.string.game_correct)
            inGameScore += 1
        }
        else{
            alertDialogTitle = resources.getString(R.string.game_wrong)
        }
        if(gamePlayed >= 2){
            positiveButton = resources.getString(R.string.game_finish)
        }
        else{
            positiveButton = resources.getString(R.string.game_continue)
        }
        var alertDialog : AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialog.setTitle(alertDialogTitle)
        alertDialog.setMessage(resources.getString(R.string.game_dialog_score_message)+" "+correctAnswer)
        alertDialog.setPositiveButton(positiveButton) {dialog, which ->
            if(gamePlayed >= 2) {
                showResult()
            }
            else{
                gamePlayed++
                showQuestion()
            }
        }
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun showResult(){

        var alertDialog : AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialog.setTitle(resources.getString(R.string.game_over))
        alertDialog.setMessage(resources.getString(R.string.game_dialog_result)+" "+inGameScore)
        alertDialog.setPositiveButton(resources.getString(R.string.game_retry)) {dialog, which ->
            recreate()
        }
        alertDialog.setNegativeButton(resources.getString(R.string.game_terminate)) {dialog, which ->
            finish()
        }
        alertDialog.show()
        var updateScore : Int = score.score.toInt() + inGameScore
        score.score = updateScore.toString()
        ScoreModel.saveScore(score, OnCompleteListener { task ->
            if(task.isComplete){
                Log.d(TAG, "USER SCORE HAS BEEN UPDATED SUCCESSFULLY")
            }
            else{
                Log.d(TAG, "USER SCORE HAS NOT BEEN UPDATED")
            }
        })
    }
}
