package com.example.braingame

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class LeaderboardFragment : Fragment(), Observer {

    private var mScoreListAdapter: ScoreCardAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_leaderboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ScoreModel.addObserver(this)
        val dataList: ListView = view?.findViewById(R.id.leaderboard_score_list)
        val data: ArrayList<Score> = ArrayList()
        mScoreListAdapter = ScoreCardAdapter(requireContext(), R.layout.score_card_item, data)
        dataList.adapter = mScoreListAdapter
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
        mScoreListAdapter?.addAll(data.sortedByDescending { score -> score.score.toInt() })
        mScoreListAdapter?.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        ScoreModel.addObserver(this)
        ScoreModel.getData()?.let { setData(it) }
    }

    override fun onPause() {
        super.onPause()
        ScoreModel.deleteObserver(this)
    }

    override fun onStop() {
        super.onStop()
        ScoreModel.deleteObserver(this)
    }

}
