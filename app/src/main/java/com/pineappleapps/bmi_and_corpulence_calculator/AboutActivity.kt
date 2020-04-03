package com.pineappleapps.bmi_and_corpulence_calculator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class AboutActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.about_main)
        setTitle("About and Facts");
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
    }
}