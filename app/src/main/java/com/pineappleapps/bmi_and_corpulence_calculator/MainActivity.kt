package com.pineappleapps.bmi_and_corpulence_calculator

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.math.RoundingMode
import java.text.DecimalFormat


class MainActivity : AppCompatActivity() {

    private lateinit var mInterstitialAd: InterstitialAd

    lateinit var mAdView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTitle("BMI and CI Calculator");
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView_main)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        requestReadPermissions()

        val file = File(Environment.getExternalStorageDirectory().toString() + "/BMI_Share");

        file.mkdirs();


        MobileAds.initialize(this) {}

        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = "ca-app-pub-4833985159522271/1046297985"
        mInterstitialAd.loadAd(AdRequest.Builder().build())


        val preferences = PreferenceManager.getDefaultSharedPreferences(this)

        bmi_weight.minValue = 25
        bmi_weight.maxValue = 205
        bmi_weight.value = 50
        bmi_weight.wrapSelectorWheel = false

        bmi_height.minValue = 100
        bmi_height.maxValue = 210
        bmi_height.value = 170
        bmi_height.wrapSelectorWheel = false




        bmi_calculate.setOnClickListener {

            val calcweight:Double = bmi_weight.value.toDouble()
            val calcheight:Double = (bmi_height.value.toDouble())/100


            val df = DecimalFormat("#.##")
            val bmi_raw:Double = calcweight / (calcheight * calcheight)

            val ponderal_index_raw = calcweight / (calcheight * calcheight * calcheight)

            df.roundingMode = RoundingMode.CEILING
            val bmi_value: String = df.format(bmi_raw)
            val ponderal_value: String = df.format(ponderal_index_raw)

            preferences.edit()
                .putString("BMI_VALUE", bmi_value)
                .putString("PONDERAL", ponderal_value)
                .apply()


            val intent = Intent(this, ResultActivity::class.java)
            startActivity(intent)

            mInterstitialAd.show()


            Log.i("CONDITION", "Weight: ${calcweight}, Height: ${calcheight}, BMI: ${bmi_value}, PONDERAL INDEX: ${ponderal_value}")


        }


    }
    @SuppressLint("WrongConstant")
    fun setTitle(title: String?) {
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        val textView = TextView(this)
        textView.text = title
        textView.textSize = 20f
        textView.setTypeface(null, Typeface.BOLD)
        textView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        textView.gravity = Gravity.CENTER_HORIZONTAL
        textView.setTextColor(resources.getColor(R.color.bmi_white))
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.customView = textView
    }

    private fun requestReadPermissions() {

        Dexter.withActivity(this)
            .withPermissions( Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        Log.d("Success:", "App Has Been Granted Permissions")

                    }
                    if (report.isAnyPermissionPermanentlyDenied) {
                        Toast.makeText(applicationContext, "Kindly grant BMI permissions in the Settings.", Toast.LENGTH_LONG)
                            .show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                    token.continuePermissionRequest()
                }
            }).withErrorListener { Log.d("Error:","Permissions Unsuccessful") }
            .onSameThread()
            .check()
    }

    private var sayBackPress: Long = 0


    override fun onBackPressed() {
        if (sayBackPress + 2000 > System.currentTimeMillis()) {
            val a = Intent(Intent.ACTION_MAIN)
            a.addCategory(Intent.CATEGORY_HOME)
            startActivity(a)
            finishAffinity()
            finish()
            finishAndRemoveTask()
            System.exit(0)
            onDestroy()
            super.onBackPressed()
        } else {
            Toast.makeText(this, "Press back again to exit.", Toast.LENGTH_SHORT)
                .show()
            sayBackPress = System.currentTimeMillis()
            mInterstitialAd.show()
        }
    }


}

