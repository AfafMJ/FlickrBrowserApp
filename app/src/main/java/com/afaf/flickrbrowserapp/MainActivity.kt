package com.afaf.flickrbrowserapp


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.custome.view.*
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var images: ArrayList<Image>
    private lateinit var ivImg: ImageView
    private lateinit var rvImg: RecyclerView
    private lateinit var rvAdapter: rvImgAdapter

    private lateinit var linH: LinearLayout
    private var imagePerPage = 10
    private lateinit var edtSearch: EditText
    private lateinit var btnSearch: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //init UI
        ivImg=findViewById(R.id.imgV)
        linH=findViewById(R.id.linh)
        edtSearch=findViewById(R.id.edtSearch)
        btnSearch=findViewById(R.id.searchBtn)

        //init arrayList & RV & adapter
        images = arrayListOf()
        rvImg=findViewById(R.id.rvImge)
        rvAdapter= rvImgAdapter(this,images)
        rvImg.adapter = rvAdapter
        rvImg.layoutManager= GridLayoutManager(this,2)

        btnSearch.setOnClickListener {
            if(edtSearch.text.isNotEmpty()){
                requestAPI()
            }else{

                Toast.makeText(this, "Search field is empty", Toast.LENGTH_LONG).show()
            }
        }

        ivImg.setOnClickListener {
            closeImg()
        }

    }

    private fun requestAPI(){
        CoroutineScope(Dispatchers.IO).launch {
            val data = async { getPhotos() }.await()
            if(data.isNotEmpty()){
                println(data)
                showPhotos(data)
            }else{
                Toast.makeText(this@MainActivity, "No Images Found", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // 54c06333154efb06c9f5673aef734ec6 key
//a7a57f126eada3e8 scard
//https://www.flickr.com/services/rest/?method=flickr.test.echo&name=value
//methud  flickr.photos.search

    private fun getPhotos(): String{
        var response = ""
        try{
            response = URL("https://www.flickr.com/services/rest/?method=flickr.photos.search&api_key=54c06333154efb06c9f5673aef734ec6&tags=${edtSearch.text}&per_page=${imagePerPage}&format=json&nojsoncallback=1")
                .readText(Charsets.UTF_8)
        }catch(e: Exception){
            println("ISSUE: $e")
        }
        return response
    }


    private suspend fun showPhotos(data: String){
        withContext(Dispatchers.Main){
            val jsonObj = JSONObject(data)
            val photos = jsonObj.getJSONObject("photos").getJSONArray("photo")
            println("photos")
            println(photos.getJSONObject(0))
            println(photos.getJSONObject(0).getString("farm"))
            for(i in 0 until photos.length()){
                val title = photos.getJSONObject(i).getString("title")
                val farmID = photos.getJSONObject(i).getString("farm")
                val serverID = photos.getJSONObject(i).getString("server")
                val id = photos.getJSONObject(i).getString("id")
                val secret = photos.getJSONObject(i).getString("secret")
                val photoLink = "https://farm$farmID.staticflickr.com/$serverID/${id}_$secret.jpg"
                images.add(Image(title, photoLink))
            }
            rvAdapter.notifyDataSetChanged()
        }
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Number of image per page")

        val view = this.layoutInflater.inflate(R.layout.custome, null)
        view.image_per_page.text = imagePerPage.toString()

        view.decrease_btn.setOnClickListener {
            if (imagePerPage > 0) {
                imagePerPage--
                view.image_per_page.text = imagePerPage.toString()
            }
        }

        view.increase_btn.setOnClickListener {
            imagePerPage++
            view.image_per_page.text = imagePerPage.toString()
        }

        builder.setView(view)

        builder.setPositiveButton("Save") { _, _ ->
            view.image_per_page.text = imagePerPage.toString()
            item.title = imagePerPage.toString()
        }
        builder.setNegativeButton("Cancel") { _, _ -> }
        builder.create().show()

        return super.onOptionsItemSelected(item)
    }


    fun viewImg(link: String){
        Glide.with(this).load(link).into(ivImg)
        ivImg.isVisible = true
        rvImg.isVisible = false
        linH.isVisible = false
    }

    private fun closeImg(){
        ivImg.isVisible = false
        rvImg.isVisible = true
        linH.isVisible = true
    }
}