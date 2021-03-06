package com.hooshmand.myquran


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hooshmand.myquran.about.AboutActivity
import com.hooshmand.myquran.setting.*
import com.hooshmand.myquran.soreh.*
import java.io.BufferedReader
import java.io.File

class MainActivityEsmeSoreh : AppCompatActivity(), CustomAdapterEsmeSoreh.onItemClickListener {
    val myarrListEsme = arrayListOf<data_esme_soreh>()

    private val adapter = CustomAdapterEsmeSoreh(myarrListEsme, this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_esme_soreh)
        title = "  قرآن من  "

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//            if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(
//                    this,
//                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                    0
//                );
//            }
        readData()
        selectGraat()
        readEsmeSoreh()
        val my_Recler_View = findViewById<RecyclerView>(R.id.recycler_view_esme_soreh)
        my_Recler_View.adapter = adapter
        my_Recler_View.layoutManager = LinearLayoutManager(this)
        my_Recler_View.setHasFixedSize(true)
        my_Recler_View.scrollToPosition(SorehNo)

    }

    private fun readEsmeSoreh() {
        try {
            val ff = BufferedReader(resources.openRawResource(R.raw.qlist).reader())
            var thisLine = ff.readLine()
            while (thisLine != null) {
                myarrListEsme.add(data_esme_soreh(thisLine))
                thisLine = ff.readLine()
            }
        } catch (e: java.lang.Exception) {
        }
    }

    override fun onItemClick(postion: Int) {
        if (SorehNo != postion) {
            AyehNo = 0
            SorehNo = postion
            adapter.notifyDataSetChanged()
        }
        sorehName = myarrListEsme[SorehNo].esme
        val myintent = Intent(this, Soreh::class.java)
        startActivity(myintent)
    }


    override fun onDestroy() {
        super.onDestroy()
        saveData()
        val myfile = File(getExternalFilesDir(null).toString() + localPath)
        deleteDir(myfile)
    }

    fun saveData() {
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putInt("ayeh", AyehNo)
        editor.putInt("soreh", SorehNo)
        editor.putFloat("fontEsmesoreh", fontEsmesoreh)
        editor.putFloat("fontAyeh", fontAyeh)
        editor.putFloat("fontTarjomeh", fontTarjomeh)
        editor.putInt("intSelectButton", intSelectButton)
        editor.apply()
    }

    fun readData() {
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        AyehNo = sharedPreference.getInt("ayeh", 0)
        SorehNo = sharedPreference.getInt("soreh", 0)

        fontEsmesoreh = sharedPreference.getFloat("fontEsmesoreh", 24f)
        fontAyeh = sharedPreference.getFloat("fontAyeh", 25f)
        fontTarjomeh = sharedPreference.getFloat("fontTarjomeh", 20f)
        intSelectButton = sharedPreference.getInt("intSelectButton", 1)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflat = menuInflater
        inflat.inflate(R.menu.my_options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.about)
            startActivity(Intent(this, AboutActivity::class.java))
        else
            if (item.itemId == R.id.setting)
                startActivityForResult(Intent(this, SettingsActivity::class.java), 0)
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        adapter.notifyDataSetChanged()
    }

}