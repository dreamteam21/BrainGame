package com.example.braingame

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class ScoreCardAdapter(context: Context, resource: Int, list: ArrayList<Score>): ArrayAdapter<Score>(context, resource, list) {
    private var mResource: Int = 0
    private var mList: ArrayList<Score>
    private var mLayoutInflater: LayoutInflater
    private var mContext: Context = context

    init {
        this.mResource = resource
        this.mList = list
        this.mLayoutInflater =  mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val returnView: View?
        if(convertView == null){
            returnView = try{
                mLayoutInflater.inflate(mResource, null)
            }
            catch(e: Exception){
                e.printStackTrace()
                View(context)
            }
            setUI(returnView, position)
            return returnView
        }
        setUI(convertView, position)
        return convertView
    }

    private fun setUI(view: View, position: Int){
        val score: Score? = if(count > position) getItem(position) else null
        val scoreName: TextView? = view.findViewById((R.id.score_card_name))
        scoreName?.text = score?.name ?: ""

        val scoreScore: TextView? = view.findViewById(R.id.score_card_score)
        scoreScore?.text = score?.score ?: ""
    }
}