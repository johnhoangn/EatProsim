package com.example.eatprosim


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment

class CommentFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_comment, container, false)
        val model = ViewModelProviders.of(this).get(SharedModel::class.java)

        v.findViewById<Button>(R.id.submit).setOnClickListener {
            arguments?.also {args ->
                model.uploadComment(Comment(v.findViewById<TextView>(R.id.commentField).text.toString()),
                    args.getString("restaurantID")!!)
            }
            NavHostFragment.findNavController(this).popBackStack()
        }

        return v
    }
}
