package com.example.braingame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_easy_mode.*

class EasyModeActivity : AppCompatActivity() {

    private val TAG: String = "Easy Mode"
    private var mAuth = FirebaseAuth.getInstance()
    private var currentUser = mAuth.currentUser
    private var inGameScore: Int = 0
    private var numClick: Int = 0
    private var gamePlayed: Int = 1
    private var scoreGiven: Int = 1
    private var questionToPlay: Int = 5
    private val timeInit: Long = 3000
    private val timeOut: Long = 5000
    private val interval: Long = 1000
    private lateinit var score: Score
    private lateinit var correctAnswer: String
    private lateinit var initialTimer : CountDownTimer
    private lateinit var gameTimer : CountDownTimer
    private val questionArray: ArrayList<ArrayList<String>> = ArrayList<ArrayList<String>>()
    private val questionData: Array<Array<String>> = arrayOf(
        //   "image_name", "correct answer"
        arrayOf("easy_q1", "4"),
        arrayOf("easy_q2", "3"),
        arrayOf("easy_q3", "6"),
        arrayOf("easy_q4", "5"),
        arrayOf("easy_q5", "8"),
        arrayOf("easy_q6", "8"),
        arrayOf("easy_q7", "2"),
        arrayOf("easy_q8", "5"),
        arrayOf("easy_q9", "6"),
        arrayOf("easy_q10", "7"),
        arrayOf("easy_q11", "9")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_easy_mode)
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
                gameEasyStartShowTime.text = ((millisUntilFinished/interval)+1).toString()
            }

            override fun onFinish() {
                gameEasyLayout.visibility = LinearLayout.VISIBLE
                for(question in questionData){
                    val tempArrayList: ArrayList<String> = ArrayList()
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
        gameEasyProgressBar.progress = ((gamePlayed.toDouble() / questionToPlay.toDouble()) * 100).toInt()
        numClick = 0
        gameEasyImageView.setImageResource(
            resources.getIdentifier(thisQuestion[0], "drawable", packageName)
        )
        correctAnswer = thisQuestion[1]
        questionArray.removeAt(randomNumber)
        gameTimer = object: CountDownTimer(timeOut, interval){
            override fun onTick(millisUntilFinished: Long) {
                gameEasyShowTime.text = ((millisUntilFinished/interval)+1).toString()
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
            startActivity(Intent(this@EasyModeActivity, HomeActivity::class.java))
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

    override fun onBackPressed() {
    }
}
