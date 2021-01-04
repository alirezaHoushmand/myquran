package com.hooshmand.myquran.setting

import android.graphics.fonts.Font
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.hooshmand.myquran.R

var fontEsmesoreh = 24f
var fontAyeh = 25f
var fontTarjomeh = 20f
lateinit var spEsmesoreh: SeekBar
lateinit var spAyeh: SeekBar
lateinit var spTarjomeh: SeekBar
lateinit var EtEsmesoreh: TextView
lateinit var EtAyeh: TextView
lateinit var EtTarjomeh: TextView

class SettingsActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        spEsmesoreh = findViewById(R.id.SeekBarESmeSoreh)
        spEsmesoreh.progress = fontEsmesoreh.toInt()
        spEsmesoreh.setOnSeekBarChangeListener(this)
        EtEsmesoreh = findViewById(R.id.textViewESmeSoreh)
        EtEsmesoreh.text = fontEsmesoreh.toString()


        spAyeh = findViewById(R.id.SeekBarAyeh)
        spAyeh.progress = fontAyeh.toInt()
        spAyeh.setOnSeekBarChangeListener(this)
        EtAyeh = findViewById(R.id.textViewAyeh)
        EtAyeh.text = fontAyeh.toString()

        spTarjomeh = findViewById(R.id.SeekBarTarjomeh)
        spTarjomeh.progress = fontTarjomeh.toInt()
        spTarjomeh.setOnSeekBarChangeListener(this)
        EtTarjomeh = findViewById(R.id.textViewTarjomeh)
        EtTarjomeh.text = fontTarjomeh.toString()

    }

    fun okClick(view: View) {
        finish()
        Log.i("tmp","view $view")
    }

    override fun onPause() {
        super.onPause()
        fontEsmesoreh = spEsmesoreh.progress.toFloat()
        fontAyeh = spAyeh.progress.toFloat()
        fontTarjomeh = spTarjomeh.progress.toFloat()

    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        EtEsmesoreh.text = spEsmesoreh.progress.toString()
        EtAyeh.text = spAyeh.progress.toString()
        EtTarjomeh.text = spTarjomeh.progress.toString()
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
}