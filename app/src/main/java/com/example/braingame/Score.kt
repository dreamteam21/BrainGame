package com.example.braingame

import android.util.Log
import com.google.firebase.database.DataSnapshot

class Score (snapshot: DataSnapshot?){

    lateinit var uid: String
    lateinit var name: String
    lateinit var score: String

    init {
        if(snapshot != null){
            createScoreFromSnapshot(snapshot)
        }
    }

    private fun createScoreFromSnapshot(snapshot: DataSnapshot){
        try {
            val data: HashMap<String, Any> = snapshot.value as HashMap<String, Any>
            Log.d("ScoreTest", data.toString())
            uid = if (data["uid"] is String) data["uid"] as String else (data["uid"] as Long).toString()
            name = if (data["name"] is String) data["name"] as String else (data["name"] as Long).toString()
            score = if (data["score"] is String) data["score"] as String else (data["score"] as Long).toString()
        }
        catch (e : Exception){
            e.printStackTrace()
        }
    }

    fun toMap(): HashMap<String, Any> {
        val map: HashMap<String, Any> = HashMap()
        map["uid"] = uid
        map["name"] = name
        map["score"] = score
        return map
    }
}