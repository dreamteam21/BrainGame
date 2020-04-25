package com.example.braingame

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeEasy.setOnClickListener {
            val alertDialog: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            alertDialog.setTitle(resources.getString(R.string.home_dialog_title))
            alertDialog.setMessage(resources.getString(R.string.home_dialog_message))
            alertDialog.setPositiveButton(resources.getString(R.string.home_dialog_yes)){_, _ ->
                startActivity(Intent(activity, EasyModeActivity::class.java))
            }
            alertDialog.setNegativeButton(resources.getString(R.string.home_dialog_no)){_, _ -> }
            alertDialog.show()
        }
        homeNormal.setOnClickListener {
            val alertDialog: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            alertDialog.setTitle(resources.getString(R.string.home_dialog_title))
            alertDialog.setMessage(resources.getString(R.string.home_dialog_message))
            alertDialog.setPositiveButton(resources.getString(R.string.home_dialog_yes)){_, _ ->
                startActivity(Intent(activity, NormalModeActivity::class.java))
            }
            alertDialog.setNegativeButton(resources.getString(R.string.home_dialog_no)){_, _ -> }
            alertDialog.show()
        }
        homeHard.setOnClickListener {
            val alertDialog: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            alertDialog.setTitle(resources.getString(R.string.home_dialog_title))
            alertDialog.setMessage(resources.getString(R.string.home_dialog_message))
            alertDialog.setPositiveButton(resources.getString(R.string.home_dialog_yes)){_, _ ->
                startActivity(Intent(activity, HardModeActivity::class.java))
            }
            alertDialog.setNegativeButton(resources.getString(R.string.home_dialog_no)){_, _ -> }
            alertDialog.show()
        }
    }

}
