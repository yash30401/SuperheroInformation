package com.devyash.superheroinformation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.devyash.superheroinformation.databinding.ActivityMainBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.search.setOnClickListener {
            if(binding.superheroName.text.isEmpty()){
                Toast.makeText(applicationContext, "Empty Field!", Toast.LENGTH_SHORT).show()
            }else{
                GlobalScope.async {
                    getPredictions()
                }

            }
        }



    }


    private suspend fun getPredictions() {

        try {

            val result= GlobalScope.async {
                callAztroAPI("https://superhero-search.p.rapidapi.com/api/?hero=hulk&rapidapi-key=3dff07b0f5msh40d4c6bb5e5815fp1fabfajsn77cdcf2e6982")

            }.await()

            onResponse(result)

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
            // convert the string to JSON object for better reading
            val resultJson = JSONObject(result)
            // Initialize prediction text
            var prediction ="Data \n"
            var powerStats:JSONObject
           
            // Update text with various fields from response
            prediction += resultJson.getString("id")+"\n"

            powerStats=resultJson.getJSONObject("powerstats")

            prediction+=powerStats["strength"]
            //Update the prediction to the view

            setText(binding.resultText,prediction)

        } catch (e: Exception) {
            e.printStackTrace()
           binding.resultText.text = "Oops!! something went wrong, please try again"
        }

    }

    private fun setText(resultView: TextView, value: String) {
        runOnUiThread{binding.resultText.text=value}
        Log.d("Text",value)
    }
}