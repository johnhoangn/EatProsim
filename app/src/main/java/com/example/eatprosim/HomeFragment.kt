package com.example.eatprosim


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_home, container, false)
        val sortBy = v.findViewById<Spinner>(R.id.sortSpinner)
        val filterBy = v.findViewById<Spinner>(R.id.filterSpinner)
        // val search
        val restaurantList = v.findViewById<RecyclerView>(R.id.restaurantList)
        val model = activity?.run {
            ViewModelProviders.of(this).get(SharedModel::class.java)
        } ?: throw Exception("Invalid Activity")
        val viewAdapter = RecyclerViewAdapter(model.restaurants, activity as MainActivity)

        restaurantList.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        restaurantList.adapter = viewAdapter

        model.restaurants.observe(
            this,
            Observer<ArrayList<Restaurant>> { //restaurants ->
                //restaurants?.let{
                    viewAdapter.notifyDataSetChanged()
                //}
            }
        )

        return v
    }
}

class RecyclerViewAdapter(private val myDataset: MutableLiveData<ArrayList<Restaurant>>, private val activity: MainActivity) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): RecyclerViewAdapter.ViewHolder {

        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view, parent, false)

        return ViewHolder(v, activity)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val restaurant = myDataset.value?.get(position)
        if ( restaurant != null) {
            val nameView: TextView = holder.view.findViewById(R.id.nameView)
            val ratingView: TextView = holder.view.findViewById(R.id.ratingView)
            val model = activity.run {
                ViewModelProviders.of(this).get(SharedModel::class.java)
            }

            nameView.text = restaurant.name
            ratingView.text = "%d/5".format(restaurant.rating)
            holder.itemView.setOnClickListener {
                model.selectRestaurant(it, position)
            }
        }
    }

    class ViewHolder(val view: View, private val activity: MainActivity) : RecyclerView.ViewHolder(view)

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() : Int {
        return myDataset.value?.size ?: 0
    }
}