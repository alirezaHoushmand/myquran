package com.hooshmand.myquran.about

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hooshmand.myquran.R

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        title = "در باره ..."
    }

    fun mailtoClick(view: View) {
        val i = Intent(Intent.ACTION_SEND)
        i.type = "message/rfc822"
        i.putExtra(Intent.EXTRA_EMAIL, arrayOf("alireza.houshmand@gmail.com"))
        i.putExtra(Intent.EXTRA_SUBJECT, "Qurqn  android Ver 1(قران اندروید ورژن 1)")
        i.putExtra(Intent.EXTRA_TEXT, "alireza.houshmand@gmail.com ایمیل به")
        try {
            startActivity(Intent.createChooser(i, "alireza.houshmand@gmail.com ایمیل به"))
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(
                this,
                "There are no email clients installed.",
                Toast.LENGTH_SHORT
            ).show()
        }
        Log.i("tmp", "view $view")
    }

    fun goSource(view: View) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://github.com/alirezaHoushmand/myquran")
            )
        )
        Log.i("tmp", "view $view")
    }
}