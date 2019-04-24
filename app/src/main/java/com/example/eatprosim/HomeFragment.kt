package com.example.eatprosim


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_home, container, false)
        val sortBy = v.findViewById<Spinner>(R.id.sortSpinner)
        val filterBy = v.findViewById<Spinner>(R.id.filterSpinner)

        // val search
        val restaurantList = v.findViewById<RecyclerView>(R.id.restaurantList)
        val model = activity?.run {
            ViewModelProviders.of(this).get(SharedModel::class.java)
        } ?: throw Exception("Invalid Activity")
        val adapter = RestaurantAdapter(model.restaurants.value)

        restaurantList.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        restaurantList.adapter = adapter

        model.restaurants.observe(this, Observer {
            adapter.setData(it)
            adapter.notifyDataSetChanged()
        })

        return v
    }

    inner class RestaurantAdapter(private var myDataset: ArrayList<Restaurant>?) :
        RecyclerView.Adapter<RestaurantAdapter.ViewHolder>() {

        internal fun setData(data : ArrayList<Restaurant>?) {
            myDataset = data
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantAdapter.ViewHolder {

            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.card_view, parent, false)

            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            myDataset?.also {
                val restaurant = it[position]
                val nameView: TextView = holder.view.findViewById(R.id.nameView)
                val ratingView: TextView = holder.view.findViewById(R.id.ratingView)

                nameView.text = restaurant.name
                ratingView.text = "%d/5".format(restaurant.rating)

                holder.itemView.setOnClickListener {
                    findNavController(this@HomeFragment).navigate(
                        R.id.action_homeFragment_to_detailFragment,
                        bundleOf(
                            "name" to restaurant.name, "rating" to restaurant.rating,
                            "phone" to restaurant.phone
                        )
                    )
                }
            }
        }

        inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount() : Int {
            return myDataset?.size ?: 0
        }
    }
}