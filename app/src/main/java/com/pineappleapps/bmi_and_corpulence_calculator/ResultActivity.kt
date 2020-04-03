package com.pineappleapps.bmi_and_corpulence_calculator

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Typeface
import android.os.Bundle
import android.os.Environment
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.preference.PreferenceManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.tarek360.instacapture.Instacapture
import com.tarek360.instacapture.listener.SimpleScreenCapturingListener
import kotlinx.android.synthetic.main.result_main.*
import java.io.File
import java.io.FileOutputStream
import java.util.*


class ResultActivity : AppCompatActivity() {


    lateinit var mAdView: AdView

    lateinit var bmi_condition: String


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.result_main)
        setTitle("BMI Details")
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)


        val bmi_value =
            PreferenceManager.getDefaultSharedPreferences(this).getString("BMI_VALUE", "0")
        val ponderal_value =
            PreferenceManager.getDefaultSharedPreferences(this).getString("PONDERAL", "0")

        if (bmi_value!! <= "0") {
            bmi_condition = "SEVERELY UNDERWEIGHT";
        } else if (bmi_value > "15" && bmi_value <= "16") {
            bmi_condition = "VERY UNDERWEIGHT";
        } else if (bmi_value > "16" && bmi_value <= "18.5") {
            bmi_condition = "UNDERWEIGHT";
        } else if (bmi_value > "18.5" && bmi_value <= "25") {
            bmi_condition = "NORMAL";
        } else if (bmi_value > "25" && bmi_value <= "30") {
            bmi_condition = "OVERWEIGHT";
        } else if (bmi_value > "30" && bmi_value <= "35") {
            bmi_condition = "VERY OVERWEIGHT";
        } else if (bmi_value > "35" && bmi_value <= "40") {
            bmi_condition = "OBESE";
        } else if (bmi_value > "40" && bmi_value <= "45") {
            bmi_condition = "VERY OBESE";
        } else {
            bmi_condition = "SEVERELY OBESE";
        }

        hello_name.text = "YOU ARE $bmi_condition"

        val ss = SpannableString(bmi_value)
        ss.setSpan(RelativeSizeSpan(2.5f), 0, ss.indexOf("."), 0)


        bmi_value_view.setText(ss)

        ponderal_index.text = "Corpulence/Ponderal Index: $ponderal_value kg/m3"

        Log.i("METRICS", "Condition: $bmi_condition kg/m2, Ponderal/Corpulence: $ponderal_value kg/m3")



        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)


        bmi_share.setOnClickListener {
            screenShotAndShareIt()

            Log.i("Success:", "Screenshot captured successfully.")

        }

        bmi_rate.setOnClickListener {

            val intent = Intent(this, RatingActivity::class.java)
            startActivity(intent)
        }


    }

    @SuppressLint("WrongConstant")
    fun setTitle(title: String?) {
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        val textView = TextView(this)
        textView.text = title
        textView.textSize = 20f
        textView.setTypeface(null, Typeface.BOLD)
        textView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT + 640,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        textView.gravity = Gravity.CENTER_HORIZONTAL
        textView.setTextColor(resources.getColor(R.color.bmi_icon_background))
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.customView = textView
    }


    fun screenShotAndShareIt() {
        val now = Date()
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
        Instacapture.capture(this, object : SimpleScreenCapturingListener() {
            override fun onCaptureComplete(bitmap: Bitmap) {
                val state = Environment.getExternalStorageState()
                if (Environment.MEDIA_MOUNTED == state) {
                    val path: String = Environment.getExternalStorageDirectory().toString()
                    val picDir = File(path.plus("/BMI_Share"))
                    if (!picDir.exists()) {
                        picDir.mkdir()
                    }
                    var bitmapScreenShot = bitmap
                    val fileName = "My BMI and Corpulence Index" + ".jpeg"
                    val picFile = File(picDir.path.plus("/" + fileName))
                    try {
                        picFile.createNewFile()
                        val picOut = FileOutputStream(picFile)
                        bitmapScreenShot =
                            Bitmap.createBitmap(
                                bitmapScreenShot,
                                0,
                                0,
                                bitmapScreenShot.width,
                                bitmapScreenShot.height
                            )
                        val saved: Boolean =
                            bitmapScreenShot.compress(Bitmap.CompressFormat.JPEG, 100, picOut)
                        if (saved) {
                            Log.i(
                                "Success:",
                                "Image saved to your device Pictures " + ", Directory: ${picFile.absolutePath}"
                            )
                        } else {
                            Log.i("Error: ", "Error on saving! + ${picFile.absolutePath}")
                        }
                        picOut.close()


                            val urii = FileProvider.getUriForFile(applicationContext, "com.pineappleapps.bmi_and_corpulence_calculator.provider", picFile)

                            val intent = Intent(android.content.Intent.ACTION_SEND)
                            intent.type = "image/*"
                            intent.putExtra(Intent.EXTRA_STREAM, urii)
                            intent.setDataAndType(urii, getContentResolver().getType(urii));
                            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
                            intent.putExtra(android.content.Intent.EXTRA_TEXT, "Hey! Kindly Check out my BMI and Corpulence/Ponderal Index.");
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(Intent.createChooser(intent, "Share your BMI details..."));

                    } catch (e: Exception) {
                        Log.i("Catch", ": " + e.printStackTrace())
                    }
                } else {
                    //Error
                    Log.i("Error:", "Environment.MEDIA_MOUNTED == state : ")
                }

            }
        })
    }
}