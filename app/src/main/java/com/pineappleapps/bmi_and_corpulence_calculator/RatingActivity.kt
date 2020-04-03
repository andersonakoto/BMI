package com.pineappleapps.bmi_and_corpulence_calculator

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.rating_main.*


class RatingActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.rating_main)


        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)

        val width = dm.widthPixels
        val height = dm.heightPixels

        window.setLayout((width * 0.75).toInt(), (height * 0.55).toInt())
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        val params = window.attributes

        params.gravity = Gravity.CENTER
        params.x = 0
        params.y = 0
        val lp = window.attributes
        lp.dimAmount = 0.7f
        window.attributes = lp
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        bmi_rating.setOnClickListener {

            val rating: String = java.lang.String.valueOf(ratingBar_bmi.getRating())
            Toast.makeText(applicationContext, "Rating: $rating", Toast.LENGTH_LONG).show()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, R.anim.slide_up)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_OUTSIDE) {
            overridePendingTransition(0, R.anim.slide_up)
        }
        return true
    }

}