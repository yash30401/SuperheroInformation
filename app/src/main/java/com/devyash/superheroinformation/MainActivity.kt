package com.devyash.superheroinformation

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ScrollView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
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
                callAztroAPI("https://superhero-search.p.rapidapi.com/api/?hero=${superheroname}&rapidapi-key=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx")

            }.await()

            Log.d("Result.............",result.toString())
            if(result.toString()=="Hero Not Found"){

                runOnUiThread {
                    binding.pBar.visibility = View.INVISIBLE
                    binding.scrollbar.visibility = View.GONE
                    binding.NAME.visibility = View.INVISIBLE
                    binding.heroName.text = "Hero Not Found!!"
                    binding.namelay.visibility = View.VISIBLE
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
                binding.scrollbar.visibility=View.GONE
                runOnUiThread{binding.heroName.text = "Oops!! something went wrong, please try again"}

            }


    }

    private fun setText(value: JSONObject) {

        var powerstats=value.getJSONObject("powerstats")
        var appearance=value.getJSONObject("appearance")
        var biography=value.getJSONObject("biography")
        var work=value.getJSONObject("work")
        var connections=value.getJSONObject("connections")

        runOnUiThread{
            binding.scrollbar.visibility=View.VISIBLE

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
        }

    }




}
