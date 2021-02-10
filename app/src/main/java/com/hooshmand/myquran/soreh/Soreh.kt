package com.hooshmand.myquran.soreh

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hooshmand.myquran.R
import com.hooshmand.myquran.about.AboutActivity
import com.hooshmand.myquran.setting.SettingsActivity
import com.hooshmand.myquran.setting.gharee
import com.hooshmand.myquran.setting.intSelectButton
import java.io.BufferedReader
import java.io.File

private const val TAG = "myQuran"
var SorehNo = 0  //0..113
var sorehName = ""
var AyehNo = 0  //0..
var urlPath = "" // your URL here

var localPath = ""
private var mediaPlayer: MediaPlayer? = null

class Soreh : AppCompatActivity(), CustomAdapterSoreh.onItemClickListener {
    val myDownloadListUrl = arrayListOf<String>()
    val myDownloadListId = arrayListOf<Long>()

    val myarrList = arrayListOf<String>()
    val myarrListfa = arrayListOf<String>()
    lateinit var message: String

    private lateinit var my_Recler_View: RecyclerView
    private lateinit var dm: DownloadManager

    private lateinit var br: BroadcastReceiver

    val users = ArrayList<data_Soreh>()
    private val adapter = CustomAdapterSoreh(users, this)

    var onPlay = false
    lateinit var myDirect: File

    lateinit var urlEsme: String

    private lateinit var spinner: ProgressBar
    private lateinit var spinnerText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_soreh)
        title = sorehName+ "*"+gharee+"*"
        myDirect = File(getExternalFilesDir(null).toString())

        readSoreh()
        readTarjomeh()
        onPlay = false
        users.clear() //?
        for (i in myarrList.indices)
            users.add(data_Soreh(myarrList[i], myarrListfa[i]))

        my_Recler_View = findViewById<RecyclerView>(R.id.recycler_view_soreh)
        my_Recler_View.adapter = adapter
        my_Recler_View.layoutManager = LinearLayoutManager(this)
        my_Recler_View.setHasFixedSize(true)
        my_Recler_View.scrollToPosition(AyehNo)
        myDownloadListUrl.clear()
        myDownloadListId.clear()

        spinner = findViewById(R.id.progressBar1)
        spinnerText = findViewById(R.id.ProgressBarText)
        spinnerText.visibility = View.GONE
        spinner.visibility = View.GONE

    }
    //*********************************

    fun readSoreh() {
        try {
            message = (SorehNo + 1).toString()
            val fname = resources.openRawResource(
                resources.getIdentifier(
                    "s" + message,
                    "raw", packageName
                )
            )
            val ff = BufferedReader(fname.reader())
            var thisLine = ff.readLine()
            while (thisLine != null) {
                myarrList.add(thisLine)
                thisLine = ff.readLine()
            }
            ff.close()
        } catch (e: java.lang.Exception) {
            Toast.makeText(this, "*خطا در خواندن سوره*" + e, Toast.LENGTH_SHORT).show()
        }
    }

    //*********************************
    private fun readTarjomeh() {
        try {
            message = (SorehNo + 1).toString()
            val fname = resources.openRawResource(
                resources.getIdentifier(
                    "f$message",
                    "raw", packageName
                )
            )
            val ff = BufferedReader(fname.reader())
            var thisLinefa = ff.readLine()
            while (thisLinefa != null) {
                myarrListfa.add(thisLinefa)
                thisLinefa = ff.readLine()
            }
            ff.close()
        } catch (e: java.lang.Exception) {
            Toast.makeText(this, "*خطا در خواندن ترجمه*" + e, Toast.LENGTH_SHORT).show()
        }
    }
    //*********************************

    fun downloadFile(urlToDownload: String, SorehNo: Int, AyehNo: Int, urlEsme: String) {
        try {
            Log.d(TAG  , "url to down $urlToDownload")
            Log.d(TAG, "urlEsme $urlEsme")
            Log.d(TAG, "localpath $localPath")
            val request = DownloadManager.Request(
                Uri.parse(urlToDownload)
            )
                .setTitle("Download soreh:${SorehNo + 1} Ayeh:${AyehNo + 1}")
                .setDescription("from: $urlToDownload")
                //  .setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
                .setAllowedOverMetered(true)
                .setAllowedNetworkTypes(
                    DownloadManager.Request.NETWORK_WIFI
                            or DownloadManager.Request.NETWORK_MOBILE
                )
                //   .setDestinationInExternalPublicDir("/myQuran/", urlEsme)
                .setDestinationInExternalFilesDir(applicationContext, localPath, urlEsme)
            dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            Log.d(TAG, "dm to $dm")
            val tmpId = dm.enqueue(request)
            myDownloadListUrl.add(urlToDownload)
            myDownloadListId.add(tmpId)
            if (!onPlay) {
                spinner.visibility = View.VISIBLE
                spinnerText.visibility = View.VISIBLE
            }

            br = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    val id: Long = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)!!
                    Log.d(TAG, "dll link==>" + dm.getUriForDownloadedFile(id).toString())
                    val tmpidRm =
                        myDownloadListUrl.indexOf(dm.getUriForDownloadedFile(id).toString())
                    if (tmpidRm > -1) {
                        myDownloadListUrl.removeAt(tmpidRm)
                        myDownloadListId.removeAt(tmpidRm)
                    }
                    playAndDownloadControl()
                }
            }
            registerReceiver(br, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        } catch (e: Exception) {
            Toast.makeText(this, "*خطا در دانلود فایل*" + e, Toast.LENGTH_SHORT).show()
        }
    }

    //*********************************
    override fun onItemClick(postion: Int) {
        mediaPlayer?.stop()
        mediaPlayer?.reset()
        AyehNo = postion
        adapter.notifyDataSetChanged()
        onPlay = false
        Log.d(TAG, "call playAndDownload  from onClick")
        if ((intSelectButton == 2) or (intSelectButton == 4))  //for add besm to first of  Menshawi or Alafasy
            if (AyehNo == 0) //ayeh 1
                if ((SorehNo != 0) && (SorehNo != 8)) //sore not(hamed or tubeh)
                    AyehNo--

        playAndDownloadControl()
    }

    //*********************************
    fun play(localFile: String) {
      //  mediaPlayer?.stop()
       // mediaPlayer?.reset()
        Log.d(TAG, "play $localFile")
        onPlay = true
        try {
            mediaPlayer = MediaPlayer().apply {
                //setAudioStreamType(AudioManager.STREAM_MUSIC)
                setDataSource(localFile)
                prepareAsync()
                Log.d(TAG, "prepare")
            }
        } catch (e: Exception) {
            Toast.makeText(this, "*خطا در مدیا پلیر*" + e, Toast.LENGTH_SHORT).show()
            Log.d(TAG, "media exception :$e")
        }
        try {
            mediaPlayer?.setOnPreparedListener {
                Log.d(TAG, "start")
                mediaPlayer?.start()
                spinnerText.visibility = View.GONE
                spinner.visibility = View.GONE
                if (AyehNo < myarrList.size - 1)
                    preDownload(SorehNo, AyehNo + 1)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "*خطا در آماده سازی مدیا*" + e, Toast.LENGTH_SHORT).show()
        }

        mediaPlayer?.setOnCompletionListener {
            onPlay = false
            Log.d(TAG, "media complete")
            if (AyehNo < myarrList.size - 1) {
                AyehNo++
                my_Recler_View.adapter?.notifyDataSetChanged()
                my_Recler_View.smoothScrollToPosition(AyehNo)

                Log.d(TAG, "call play and download  from media complete")
                playAndDownloadControl()
            } else {
                Toast.makeText(this, "پایان سوره", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun makeUrlToPlay(soreh: Int, ayeh: Int): String {
        if (ayeh == -1)
            return "001001.mp3"
        return String.format(null, "%03d%03d.mp3", soreh + 1, ayeh + 1)
    }

    //*********************************
    fun playAndDownloadControl() {
        try {
            Log.d(TAG, "play and download")
            urlEsme = makeUrlToPlay(SorehNo, AyehNo)

            // url = urlPath + urlEsme
            val myfile = File(myDirect.toString() + localPath, urlEsme)
            if (!(myfile.exists())) {
                if (!myDownloadListUrl.contains(urlPath + urlEsme)) {
                    downloadFile(urlPath + urlEsme, SorehNo, AyehNo, urlEsme)
                } else {
                    spinner.visibility = View.VISIBLE
                    spinnerText.visibility = View.VISIBLE
                }
            } else {
                Log.d(TAG, "file exists,play it ....${myfile.path}")
                if (!onPlay)
                    play(myfile.path)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "*خطا در کنترل دانلود و پخش*" + e, Toast.LENGTH_SHORT).show()
        }
    }

    //**********************************
    fun preDownload(prSorehNo: Int, prAyehNo: Int) {
        try {
            Log.d(TAG, "pre download:${prSorehNo + 1},${prAyehNo + 1}")
            val preUrlEsme = makeUrlToPlay(prSorehNo, prAyehNo)
            val preUrl = urlPath + preUrlEsme
            val preMyfile = File(myDirect.toString() + localPath, preUrlEsme)
            if (!(preMyfile.exists())) {
                if (!myDownloadListUrl.contains(preUrl)) {
                    downloadFile(preUrl, prSorehNo, prAyehNo, preUrlEsme)
                }
            }

        } catch (e: Exception) {
            Toast.makeText(this, "*خطا در آماده سازی دانلود سوره بعد *" + e, Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onStop() {
        try {
            super.onStop()
            mediaPlayer?.stop()
            mediaPlayer?.reset()
            if (::br.isInitialized)
                unregisterReceiver(br)
        } catch (e: Exception) {
            Toast.makeText(this, "*خطا در توقف اکتیویتی*" + e, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflat = menuInflater
        inflat.inflate(R.menu.my_options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.about) {
            startActivity(Intent(this, AboutActivity::class.java))
        } else
            if (item.itemId == R.id.setting) {
                startActivityForResult(Intent(this, SettingsActivity::class.java), 0)
            }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        adapter.notifyDataSetChanged()
        title = sorehName+ "*"+gharee+"*"
    }
}
