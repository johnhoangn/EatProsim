package com.example.eatprosim


import android.graphics.Color
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

class HomeFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var sortBy : Spinner
    private lateinit var filterBy : Spinner
    private lateinit var search : SearchView
    private lateinit var model : SharedModel
    private lateinit var adapter : RestaurantAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_home, container, false)

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
        })

        // listeners
        sortBy.onItemSelectedListener = this
        filterBy.onItemSelectedListener = this

        // change restaurant list as letters are being pressed that match names
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(text: String?): Boolean {
                model.reDownload() // reload complete restaurants list for filtering
                model.restaurants.postValue(model.filterByContains(text!!) as ArrayList<Restaurant>)
                adapter.notifyDataSetChanged()
                return true
            }
        }

        )

        return v
    }

    /**
     * Adapter class for recycler view of restaurants
     */
    inner class RestaurantAdapter(private var myDataset: ArrayList<Restaurant>?) :
        RecyclerView.Adapter<RestaurantAdapter.ViewHolder>() {

        internal fun setData(data : ArrayList<Restaurant>?) {
            myDataset = data
            model.sort()
            adapter.notifyDataSetChanged()
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
                val imgView: ImageView = holder.view.findViewById(R.id.imageView)

                Glide.with(context!!)
                    .load(restaurant.imageURL)
                    .apply(RequestOptions().override(200, 200))
                    .into(imgView)

                nameView.text = restaurant.name
                ratingView.text = "%.1f/5".format(restaurant.rating)

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
                model.sort()
            }
            filterBy -> {
                model.setFilter(position)
                model.reDownload()
                model.filter()
                model.restaurants.postValue(model.filter() as ArrayList<Restaurant>)
            }
        }
        adapter.notifyDataSetChanged()
    }
}