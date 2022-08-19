package com.devyash.superheroinformation

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.devyash.superheroinformation.databinding.ActivitySettingsBinding

class Settings : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.policybtn.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://pages.flycricket.io/superhero-informatio/privacy.html")
                )
            )
        }

        binding.rateappbtn.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=com.devyash.superheroinformation")
                )
            )
        }

        binding.conatctbtn.setOnClickListener {
            val intent=Intent(Intent.ACTION_SENDTO,Uri.fromParts("mailto","yashquery30401@gmail.com",null))
            startActivity(Intent.createChooser(intent,"Send Mail..."))
        }

        binding.howtobtn.setOnClickListener {
            val dialog= Dialog(this)
            dialog.setContentView(R.layout.howtodialoglayout)

            dialog.show()


            val conslayout=dialog.findViewById<LinearLayout>(R.id.conslayout)

            conslayout.setBackgroundColor(ContextCompat.getColor(applicationContext, android.R.color.transparent))

            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
                conslayout.alpha= 0F
            } else {
                conslayout.setBackgroundColor(ContextCompat.getColor(applicationContext, android.R.color.transparent))
            }


            val okbtn=dialog.findViewById<Button>(R.id.okbtn)
            okbtn.setOnClickListener {
                dialog.dismiss()
            }
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


        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}