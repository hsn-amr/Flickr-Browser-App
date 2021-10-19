package com.example.flickrbrowserapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: RVPhoto
    private lateinit var rvMain: RecyclerView

    private lateinit var tagInput: EditText
    private lateinit var searchButton: Button

    private var photos = ArrayList<Photo>()

    private var tag = ""
    private val API_KEY = "0c944ebbc472f758230f3711aea24676"
    private lateinit var cm: ConnectivityManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvMain = findViewById(R.id.rvMain)
        adapter = RVPhoto(photos,this)
        rvMain.adapter = adapter
        rvMain.layoutManager = LinearLayoutManager(this)

        tagInput = findViewById(R.id.etTagInput)
        searchButton = findViewById(R.id.btnSearch)

        searchButton.setOnClickListener {
            if(tagInput.text.isNotEmpty()){
                tag = tagInput.text.toString()
                requestApi()
                tagInput.text.clear()
            }else{
                Toast.makeText(this, "Please, Enter a tag to search", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun requestApi(){
        cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        if(activeNetwork?.isConnectedOrConnecting == true){
            CoroutineScope(Dispatchers.IO).launch {
                val data = async {
                    fetchData()
                }.await()

                if(data.isNotEmpty()){
                    if(photos.isNotEmpty()){
                        photos.clear()
                    }
                    updateRV(data)
                }else{
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity, "invalid tag", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }else{
            Toast.makeText(this, "Please Connect To Internet First", Toast.LENGTH_LONG).show()
        }
    }

    private fun fetchData(): String {
        var response = ""
        try {
            response = URL("https://www.flickr.com/services/rest/?method=flickr.photos.search&api_key=$API_KEY&tags=$tag&page=1&format=json&nojsoncallback=1").readText(Charsets.UTF_8)
        }catch (e: Exception){
            Log.e("TAG","ISSUE -> $e")
        }
        return response
    }

    suspend fun updateRV(data: String){

        val photosJsonObject = JSONObject(data).getJSONObject("photos")
        val photosJsonArray = photosJsonObject.getJSONArray("photo")
        var photoNumbers = photosJsonArray.length()

        for(i in 0 until photoNumbers-1){
            val photo = Photo()
            val photoJsonObject = photosJsonArray.getJSONObject(i)
            photo.title = photoJsonObject.getString("title")
            photo.id = photoJsonObject.getString("id")
            photo.secret = photoJsonObject.getString("secret")
            photo.server = photoJsonObject.getString("server")

            withContext(Dispatchers.Main){
                photos.add(photo)
            }
        }

        withContext(Dispatchers.Main){
            rvMain.adapter!!.notifyDataSetChanged()
            rvMain.smoothScrollToPosition(0)
        }
    }
}