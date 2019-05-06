package com.example.eatprosim


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import androidx.recyclerview.widget.LinearLayoutManager

class DetailFragment : Fragment() {

    private lateinit var model : SharedModel

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_detail, container, false)
        val nameView = v.findViewById<TextView>(R.id.nameView)
        val ratingView = v.findViewById<TextView>(R.id.ratingView)
        val phoneView = v.findViewById<TextView>(R.id.phoneView)
        val imgView = v.findViewById<ImageView>(R.id.picView)
        val commentList = v.findViewById<RecyclerView>(R.id.commentsRecycler)
        val adapter = CommentAdapter()
        val comments = arrayListOf<Comment>()
        val descView = v.findViewById<TextView>(R.id.descriptionView)
        val linkView = v.findViewById<TextView>(R.id.linkView)
        val commentButton = v.findViewById<Button>(R.id.commentButton)

        model = activity?.run {
            ViewModelProviders.of(this).get(SharedModel::class.java)
        } ?: throw Exception("Invalid Activity")

        nameView.text = arguments?.getString("name")
        descView.text = arguments?.getString("summary")
        ratingView.text = "Rating: %.1f/5".format(arguments?.getDouble("rating"))
        phoneView.text = "Phone: " + arguments?.getString("phone")
        linkView.text = arguments?.getString("url")

        Glide.with(context!!)
            .load(arguments!!.getString("imageURL") ?: "")
            .apply(RequestOptions().override(200, 200))
            .into(imgView)

        // comments
        commentButton.setOnClickListener {
            arguments?.also {args ->
                NavHostFragment.findNavController(this@DetailFragment).navigate(
                    R.id.action_detailFragment_to_commentFragment,
                    bundleOf(
                        "restaurantID" to args.getString("restaurantID")
                    )
                )
            }
        }

        commentList.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        commentList.adapter = adapter

        model.database.child("comments").child(arguments!!.getString("restaurantID")!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(snap: DataSnapshot) {
                comments.clear()
                val downloadedComments : MutableList<Comment> = mutableListOf()
                snap.children.mapNotNullTo(downloadedComments) {
                    it.getValue<Comment>(Comment::class.java)
                }
                for (comment : Comment in downloadedComments) {
                    comments.add(comment)
                }
                adapter.setData(comments)
                adapter.notifyDataSetChanged()
            }
        })

        return v
    }

    inner class CommentAdapter(private var myDataset: ArrayList<Comment>? = null) :
        RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

        internal fun setData(data : ArrayList<Comment>?) {
            myDataset = data
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.ViewHolder {

            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.comment_view, parent, false)

            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            myDataset?.also {
                val comment = it[position]
                val commentView: TextView = holder.view.findViewById(R.id.messageView)

                commentView.text = comment.message
            }
        }

        inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount() : Int {
            return myDataset?.size ?: 0
        }
    }
}
