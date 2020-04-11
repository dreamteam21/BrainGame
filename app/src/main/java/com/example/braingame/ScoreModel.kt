package com.example.braingame

import android.media.MediaPlayer
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

object ScoreModel: Observable() {
    private val TAG: String = "Score Model"
    private var mValueDataListener: ValueEventListener? = null
    private var mScoreList: ArrayList<Score> = ArrayList()

    private fun getDatabaseRef(): DatabaseReference? {
        return FirebaseDatabase.getInstance().reference.child("score")
    }

    init {
        if(mValueDataListener != null){
            getDatabaseRef()?.removeEventListener(mValueDataListener!!)
        }
        mValueDataListener = null
        Log.d(TAG, "data model init: line 24")

        mValueDataListener = object: ValueEventListener{
            override fun onCancelled(databaseError: DatabaseError) {
                if(databaseError != null){
                    Log.d(TAG, "update cancelled, err msg = ${databaseError.message}")
                }
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    Log.d(TAG, "data update initiated: line 29")
                    val data: ArrayList<Score> = ArrayList()
                    if(dataSnapshot != null){
                        for(snapshot: DataSnapshot in dataSnapshot.children){
                            try{
                                data.add(Score(snapshot))
                            }
                            catch(e: Exception){
                                e.printStackTrace()
                            }
                        }
                        mScoreList = data
                        Log.d(TAG, "data updated, there are "+ mScoreList.size+" entry(ies) in the cache")
                        setChanged()
                        notifyObservers()
                    }
                }
                catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }
        getDatabaseRef()?.addValueEventListener(mValueDataListener!!)
    }

    fun getData(): ArrayList<Score>? {
        return mScoreList
    }

    fun saveScore(score: Score, onCompleteListener: OnCompleteListener<Void>){
        val ref = getDatabaseRef()?.child(score.uid)
        ref?.updateChildren(score.toMap())?.addOnCompleteListener { task ->
            if(task.isComplete){
                onCompleteListener?.onComplete(task)
                setChanged()
                notifyObservers()
            }
        }
    }
}