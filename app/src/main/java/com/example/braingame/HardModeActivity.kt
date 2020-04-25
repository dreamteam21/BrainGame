package com.example.braingame

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_hard_mode.*

class HardModeActivity : AppCompatActivity() {

    private val TAG: String = "Hard Mode"
    private var mAuth = FirebaseAuth.getInstance()
    private var currentUser = mAuth.currentUser
    private var inGameScore: Int = 0
    private var numClick: Int = 0
    private var gamePlayed: Int = 1
    private var scoreGiven: Int = 3
    private var questionToPlay: Int = 5
    private val timeInit: Long = 3000
    private val timeOut: Long = 7000
    private val interval: Long = 1000
    private lateinit var score: Score
    private lateinit var correctAnswer: String
    private lateinit var initialTimer : CountDownTimer
    private lateinit var gameTimer : CountDownTimer
    private val questionArray: ArrayList<ArrayList<String>> = ArrayList<ArrayList<String>>()
    private val questionData: Array<Array<String>> = arrayOf(
        //   "image_name", "correct answer"
        arrayOf("hard_q1", "6"),
        arrayOf("hard_q2", "5"),
        arrayOf("hard_q3", "7"),
        arrayOf("hard_q4", "8"),
        arrayOf("hard_q5", "4"),
        arrayOf("hard_q6", "7"),
        arrayOf("hard_q7", "5"),
        arrayOf("hard_q8", "6"),
        arrayOf("hard_q9", "4"),
        arrayOf("hard_q10", "6")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hard_mode)
        val tempArr : ArrayList<Score>? = ScoreModel.getData()
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
        initialTimer = object: CountDownTimer(timeInit, interval){
            override fun onTick(millisUntilFinished: Long) {
                gameHardStartShowTime.text = ((millisUntilFinished/interval)+1).toString()
            }

            override fun onFinish() {
                gameHardLayout.visibility = LinearLayout.VISIBLE
                for(question in questionData){
                    val tempArrayList: ArrayList<String> = ArrayList()
                    tempArrayList.add(question[0])
                    tempArrayList.add(question[1])
                    questionArray.add(tempArrayList)
                }
                gameHardInitText.visibility = TextView.INVISIBLE
                gameHardStartShowTime.visibility = TextView.INVISIBLE
                showQuestion()
            }
        }
        initialTimer.start()
    }

    private fun showQuestion(){
        val random: java.util.Random = java.util.Random()
        val randomNumber: Int = random.nextInt(questionArray.size)
        val thisQuestion: ArrayList<String> = questionArray[randomNumber]
        gameHardProgressBar.progress = ((gamePlayed.toDouble() / questionToPlay.toDouble()) * 100).toInt()
        numClick = 0
        gameHardImageView.setImageResource(
            resources.getIdentifier(thisQuestion[0], "drawable", packageName)
        )
        handleAnimation()
        correctAnswer = thisQuestion[1]
        questionArray.removeAt(randomNumber)
        gameTimer = object: CountDownTimer(timeOut, interval){
            override fun onTick(millisUntilFinished: Long) {
                gameHardShowTime.text = ((millisUntilFinished/interval)+1).toString()
                gameHardNumBlock.text = numClick.toString()
                gameHardButton.setOnClickListener {
                    numClick++
                    gameHardNumBlock.text = numClick.toString()
                }
            }
            override fun onFinish() {
                gameHardShowTime.text = "0"
                checkAnswer()
            }
        }
        gameTimer.start()
    }

    private fun checkAnswer(){
        val alertDialogTitle : String
        val alertDialogMessage : String
        val positiveButton : String
        if(correctAnswer == numClick.toString()){
            alertDialogTitle = resources.getString(R.string.game_correct)
            alertDialogMessage = resources.getString(R.string.game_dialog_correct_message)+" "+scoreGiven+" "+ resources.getString(R.string.game_dialog_correct_message_unit)
            inGameScore += scoreGiven
        }
        else{
            alertDialogTitle = resources.getString(R.string.game_wrong)
            alertDialogMessage = resources.getString(R.string.game_dialog_score_message)+" "+correctAnswer
        }
        if(gamePlayed >= questionToPlay){
            positiveButton = resources.getString(R.string.game_finish)
        }
        else{
            positiveButton = resources.getString(R.string.game_continue)
        }
        val alertDialog : AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialog.setTitle(alertDialogTitle)
        alertDialog.setMessage(alertDialogMessage)
        alertDialog.setPositiveButton(positiveButton) { _, _ ->
            if(gamePlayed >= questionToPlay) {
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

        val alertDialog : AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialog.setTitle(resources.getString(R.string.game_over))
        alertDialog.setMessage(resources.getString(R.string.game_dialog_result)+" "+inGameScore)
        alertDialog.setPositiveButton(resources.getString(R.string.game_retry)) { _, _ ->
            recreate()
        }
        alertDialog.setNegativeButton(resources.getString(R.string.game_terminate)) { _, _ ->
            startActivity(Intent(this@HardModeActivity, HomeActivity::class.java))
            finish()
        }
        alertDialog.show()
        val updateScore : Int = score.score.toInt() + inGameScore
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
    private fun handleAnimation(){
        val animation: AlphaAnimation = AlphaAnimation(1f, 0f)
        animation.duration = 200
        animation.interpolator = LinearInterpolator()
        animation.repeatCount = 30
        animation.repeatMode = Animation.REVERSE
        gameHardImageView.startAnimation(animation)
    }
    override fun onBackPressed() {
    }
}
