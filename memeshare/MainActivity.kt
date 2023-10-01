// A simple android app that displays and shares random memes from reddit
// using the https://meme-api.herokuapp.com/gimme API

package com.example.memeshare

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

class MainActivity : AppCompatActivity() {

    // Declare the UI elements as variables
    private lateinit var memeImageView: ImageView
    private lateinit var shareButton: Button
    private lateinit var nextButton: Button
    private lateinit var progressBar: ProgressBar

    // Declare a variable to store the current meme url
    private var currentMemeUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the UI elements by finding them by their ids
        memeImageView = findViewById(R.id.memeImageView)
        shareButton = findViewById(R.id.shareButton)
        nextButton = findViewById(R.id.nextButton)
        progressBar = findViewById(R.id.progressBar)

        // Set the onClickListeners for the buttons
        shareButton.setOnClickListener {
            shareMeme()
        }

        nextButton.setOnClickListener {
            loadMeme()
        }

        // Load the first meme when the app starts
        loadMeme()
    }

    // A function to load a random meme from the API and display it on the image view
    private fun loadMeme() {
        // Show the progress bar and hide the buttons while loading the meme
        progressBar.visibility = View.VISIBLE
        shareButton.visibility = View.GONE
        nextButton.visibility = View.GONE

        // Create a request queue using Volley library
        val queue = Volley.newRequestQueue(this)

        // Create a JSON object request using the API url
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, "https://meme-api.herokuapp.com/gimme",
            null,
            Response.Listener { response ->
                // Get the meme url from the response JSON object
                currentMemeUrl = response.getString("url")

                // Use Glide library to load the image from the url into the image view
                Glide.with(this).load(currentMemeUrl).into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        // Hide the progress bar and show the buttons when the image is ready
                        progressBar.visibility = View.GONE
                        shareButton.visibility = View.VISIBLE
                        nextButton.visibility = View.VISIBLE

                        // Set the image view drawable to the resource drawable
                        memeImageView.setImageDrawable(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // Do nothing
                    }
                })
            },
            Response.ErrorListener {
                // Handle the error by showing a toast message and hiding the progress bar
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            })

        // Add the request to the queue
        queue.add(jsonObjectRequest)
    }

    // A function to share the current meme url using an implicit intent
    private fun shareMeme() {
        // Create an intent with action SEND and type text/plain
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"

        // Put the current meme url as an extra with key Intent.EXTRA_TEXT
        intent.putExtra(Intent.EXTRA_TEXT, "Hey, check out this cool meme I found on reddit: $currentMemeUrl")

        // Start the intent chooser with a title "Share this meme using..."
        val chooser = Intent.createChooser(intent, "Share this meme using...")
        startActivity(chooser)
    }
}
