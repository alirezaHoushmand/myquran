@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.hooshmand.myquran.setting

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.hooshmand.myquran.R
import com.hooshmand.myquran.soreh.localPath
import com.hooshmand.myquran.soreh.urlPath
import java.io.File

var fontEsmesoreh = 24f
var fontAyeh = 25f
var fontTarjomeh = 20f
lateinit var spEsmesoreh: SeekBar
lateinit var spAyeh: SeekBar
lateinit var spTarjomeh: SeekBar
lateinit var EtEsmesoreh: TextView
lateinit var EtAyeh: TextView
lateinit var EtTarjomeh: TextView
lateinit var radioGroup: RadioGroup
lateinit var radioButton: RadioButton
var intSelectButton: Int = 1
var gharee = ""
fun selectGraat() {
    if (intSelectButton == 1) {
        urlPath =
            "https://everyayah.com//data//AbdulSamad_64kbps_QuranExplorer.Com//" // your URL here
        localPath = "/myQuran/AbdulSamad/"
        gharee = "عبدالباسط"
    } else if (intSelectButton == 2) {
        urlPath =
            "https://everyayah.com//data//Menshawi_16kbps//" // your URL here
        localPath = "/myQuran/Menshawi/"
        gharee = "منشاوی"
    } else if (intSelectButton == 3) {
        urlPath =
            "https://everyayah.com//data//Parhizgar_48kbps//" // your URL here
        localPath = "/myQuran/Parhizgar/"
        gharee = "پرهیزگار"
    } else if (intSelectButton == 4) {
        urlPath = "https://everyayah.com/data/Alafasy_128kbps/" // your URL here
        // urlPath = "https://everyayah.com//data//data//Alafasy_64kbps//" // your URL here
        localPath = "/myQuran/Alafasy/"
        gharee = "عفاسی"
    }
    //Toast.makeText(baseContext, radioButton.text, Toast.LENGTH_SHORT).show()
}

fun deleteDir(file: File) {

    if (file.isDirectory)
        for (child: File in file.listFiles())
            if ("-" in child.name)
                child.delete()
}

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

        radioGroup = findViewById(R.id.radioGroup)
        if (intSelectButton == 1)
            radioGroup.check(R.id.RadioAbdulSamad)
        else if (intSelectButton == 2)
            radioGroup.check(R.id.RadioMenshawi)
        else if (intSelectButton == 3)
            radioGroup.check(R.id.RadioParhizgar)
        else if (intSelectButton == 4)
            radioGroup.check(R.id.RadioAlafasy)

        val myfile = File(getExternalFilesDir(null).toString() + localPath)
        deleteDir( myfile )
    }

    fun okClick(view: View) {
        finish()
        Log.i("tmp", "view $view")
    }

    override fun onPause() {
        super.onPause()
        fontEsmesoreh = spEsmesoreh.progress.toFloat()
        fontAyeh = spAyeh.progress.toFloat()
        fontTarjomeh = spTarjomeh.progress.toFloat()

        val SelectButton = radioGroup.checkedRadioButtonId
        if (SelectButton == R.id.RadioAbdulSamad)
            intSelectButton = 1
        else if (SelectButton == R.id.RadioMenshawi)
            intSelectButton = 2
        else if (SelectButton == R.id.RadioParhizgar)
            intSelectButton = 3
        else if (SelectButton == R.id.RadioAlafasy)
            intSelectButton = 4

        selectGraat()

    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        EtEsmesoreh.text = spEsmesoreh.progress.toString()
        EtAyeh.text = spAyeh.progress.toString()
        EtTarjomeh.text = spTarjomeh.progress.toString()
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
    override fun onStopTrackingTouch(seekBar: SeekBar?) {}


}