package com.devyash.superheroinformation

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.devyash.superheroinformation.databinding.ActivityMainBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.search.setOnClickListener {

            val imm = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.search.windowToken, 0)


            if(binding.superheroName.text.isEmpty()){
                Toast.makeText(applicationContext, "Empty Field!", Toast.LENGTH_SHORT).show()
            }else{


                binding.pBar.visibility=View.VISIBLE


                GlobalScope.launch {

                    getPredictions()
                }

            }
        }



    }


    private suspend fun getPredictions() {

        try {

            val result= GlobalScope.async {
                var superheroname=binding.superheroName.text.toString()
                callAztroAPI("https://superhero-search.p.rapidapi.com/api/?hero=${superheroname}&rapidapi-key=3dff07b0f5msh40d4c6bb5e5815fp1fabfajsn77cdcf2e6982")

            }.await()

            Log.d("Result.............",result.toString())
            if(result.toString()=="Hero Not Found"){

                runOnUiThread {
                    binding.pBar.visibility = View.INVISIBLE
                    binding.detailslayout.visibility = View.GONE
                    binding.NAME.visibility = View.INVISIBLE
                    binding.heroName.text = "Hero Not Found!!"
                    binding.namelay.visibility = View.VISIBLE
                    binding.heroimage.visibility=View.GONE
                }
            }else {

                onResponse(result)
            }
        }catch (e: Exception){
            e.printStackTrace()
        }

    }


    private fun callAztroAPI(apiUrl: String):String? {

        var result:String?=""
        val url: URL
        var connection: HttpURLConnection?=null

        try{

            url=URL(apiUrl)
            connection=url.openConnection() as HttpURLConnection

            connection.setRequestProperty("X-RapidAPI-Host", "superhero-search.p.rapidapi.com")
            // set the rapid-api key




            connection.requestMethod = "GET"

            val r = connection.inputStream
            val reader = InputStreamReader(r)

            var data=reader.read()
            while(data!=-1){
                val current=data.toChar()
                result+=current
                data=reader.read()
            }

            Log.d("yes","YO")
            return result

        }catch (e:Exception){
            e.printStackTrace()
        }



        return null
    }

    private fun onResponse(result: String?) {



            try {

                val resultJson = JSONObject(result)




                setText(resultJson)




            } catch (e: Exception) {
                e.printStackTrace()
                binding.NAME.visibility = View.GONE
                binding.detailslayout.visibility=View.GONE
                runOnUiThread{binding.heroName.text = "Oops!! something went wrong, please try again"}

            }


    }

    private fun setText(value: JSONObject) {

        var powerstats=value.getJSONObject("powerstats")
        var appearance=value.getJSONObject("appearance")
        var biography=value.getJSONObject("biography")
        var work=value.getJSONObject("work")
        var connections=value.getJSONObject("connections")
        var images=value.getJSONObject("images")
        var media=images["md"]

        runOnUiThread{
            binding.scrollbar.visibility=View.VISIBLE
            binding.detailslayout.visibility=View.VISIBLE

            binding.NAME.visibility=View.VISIBLE
            binding.namelay.visibility=View.VISIBLE
            binding.heroName.text=value.getString("name")

            binding.pBar.visibility=View.GONE
            binding.intelligancevalue.text=powerstats["intelligence"].toString()
            binding.strengthValue.text=powerstats["strength"].toString()
            binding.speedvalue.text=powerstats["speed"].toString()
            binding.durabilityvalue.text=powerstats["durability"].toString()
            binding.powervalue.text=powerstats["power"].toString()
            binding.comatvalue.text=powerstats["combat"].toString()
            binding.gendervalue.text=appearance["gender"].toString()
            binding.racevalue.text=appearance["race"].toString()
            binding.heightvalue.text=appearance["height"].toString()
            binding.weightvalue.text=appearance["weight"].toString()
            binding.eyecolorvalue.text=appearance["eyeColor"].toString()
            binding.haircolorvalue.text=appearance["hairColor"].toString()
            binding.fullnamevalue.text=biography["fullName"].toString()
            binding.aliasesvalue.text=biography["aliases"].toString()
            binding.placeofbirthvalue.text=biography["placeOfBirth"].toString()
            binding.firstappearncevalue.text=biography["firstAppearance"].toString()
            binding.publishervalue.text=biography["publisher"].toString()
            binding.occupationvalue.text=work["occupation"].toString()
            binding.basevalue.text=work["base"].toString()
            binding.groupaffvalue.text=connections["groupAffiliation"].toString()
            binding.relativevalue.text=connections["relatives"].toString()

            if (media !== null) {

                Glide.with(this)
                    .load(media)
                    .into(binding.heroimage)
            } else {
                binding.heroimage.visibility=View.GONE
            }

            binding.heroimage.visibility=View.VISIBLE
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        val isonline=isOnline(applicationContext)


        if(isonline==false){
            binding.nointermetanimation.visibility=View.VISIBLE
            binding.nointernettextview.visibility=View.VISIBLE
        }else{
            binding.nointermetanimation.visibility=View.GONE
            binding.nointernettextview.visibility=View.GONE
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.shareapp -> {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=com.devyash.superheroinformation")
                    )
                )
            }

            R.id.Settings ->{
                    startActivity(Intent(applicationContext,Settings::class.java))
            }
        }

        return super.onOptionsItemSelected(item)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }



}
