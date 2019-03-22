package com.example.eatprosim


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class DetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_detail, container, false)
        val nameView = v.findViewById<TextView>(R.id.nameView)
        // val statusView = v.findViewById<TextView>(R.id.statusView)
        val ratingView = v.findViewById<TextView>(R.id.ratingView)
        val phoneView = v.findViewById<TextView>(R.id.phoneView)

        nameView.text = arguments?.getString("name")
        ratingView.text = "%d/5".format(arguments?.getString("rating"))
        phoneView.text = arguments?.getString("phone")

        return v
    }
}
