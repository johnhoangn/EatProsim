package com.example.eatprosim


import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.SearchView
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class HomeFragment : Fragment(), AdapterView.OnItemSelectedListener {

    // hehe
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var sortBy : Spinner
    private lateinit var filterBy : Spinner
    private lateinit var search : SearchView
    private lateinit var model : SharedModel
    private lateinit var adapter : RestaurantAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_home, container, false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity as Activity)

        model = activity?.run {
            ViewModelProviders.of(this).get(SharedModel::class.java)
        } ?: throw Exception("Invalid Activity")

        val restaurantList = v.findViewById<RecyclerView>(R.id.restaurantList)
        adapter = RestaurantAdapter(model.restaurants.value)

        sortBy = v.findViewById(R.id.sortSpinner)
        filterBy = v.findViewById(R.id.filterSpinner)
        search = v.findViewById(R.id.searchbar)

        // search bar formatting
        search.queryHint = "Search for a restaurant!"
        search.setBackgroundColor(Color.argb(45, 200, 200, 200))

        // recycler view set up
        restaurantList.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        restaurantList.adapter = adapter

        // viewmodel observer
        model.restaurants.observe(this, Observer {
            adapter.setData(it)
            adapter.notifyDataSetChanged()
            Log.wtf("WTF", "DATACHANGED")
        })

        // listeners
        sortBy.onItemSelectedListener = this
        filterBy.onItemSelectedListener = this

        // change restaurant list as letters are being pressed that match names
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(text: String): Boolean {
                model.filterString = text
                model.restaurants.postValue(model.filterByContains(text) as ArrayList<Restaurant>)
                return true
            }
        })

        return v
    }

    /**
     * Adapter class for recycler view of restaurants
     */
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

        @SuppressLint("MissingPermission")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            myDataset?.also {
                val restaurant = it[position]
                val nameView: TextView = holder.view.findViewById(R.id.nameView)
                val ratingView: TextView = holder.view.findViewById(R.id.ratingView)
                val imgView: ImageView = holder.view.findViewById(R.id.imageView)
                val distView: TextView = holder.view.findViewById(R.id.distanceView)

                Glide.with(context!!)
                    .load(restaurant.imageURL)
                    .apply(RequestOptions().override(200, 200))
                    .into(imgView)

                nameView.text = restaurant.name
                ratingView.text = "%.1f/5".format(restaurant.rating)
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        model.setLocation(location!!)
                        distView.text = "%.2fmi".format(model.findDistance(restaurant) * 0.000621371)
                    }

                holder.itemView.setOnClickListener {
                    findNavController(this@HomeFragment).navigate(
                        R.id.action_homeFragment_to_detailFragment,
                        bundleOf(
                            "name" to restaurant.name, "rating" to restaurant.rating,
                            "phone" to restaurant.phone, "url" to restaurant.site,
                            "summary" to restaurant.summary,
                            "imageURL" to restaurant.imageURL, "restaurantID" to restaurant.placeid
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

    /**
     * Set spinner choices when selected
     */
    override fun onNothingSelected(parent: AdapterView<*>?) {}
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent) {
            sortBy -> {
                model.setSort(position)
            }
            filterBy -> {
                model.setFilter(position)
            }
        }
        model.filterByContains(model.filterString)
        model.filter()
        model.sort()
        adapter.notifyDataSetChanged()
    }
}