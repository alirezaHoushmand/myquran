package com.hooshmand.myquran.soreh

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
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
import java.io.BufferedReader
import java.io.File

var SorehNo = 0  //0..113
var sorehName = ""
var AyehNo = 0  //0..

class Soreh : AppCompatActivity(), CustomAdapterSoreh.onItemClickListener {
    val myDownloadList = arrayListOf<String>()
    val myarrList = arrayListOf<String>()
    val myarrListfa = arrayListOf<String>()
    lateinit var message: String
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var my_Recler_View: RecyclerView
    private lateinit var dm: DownloadManager

    private lateinit var br: BroadcastReceiver

    val users = ArrayList<data_Soreh>()
    private val adapter = CustomAdapterSoreh(users, this)

    var myDownloadId: Long = -2
    var mywaitePlay = false

    lateinit var myDirect: File

    var urlPath =
        "http://www.everyayah.com//data//AbdulSamad_64kbps_QuranExplorer.Com//" // your URL here
    lateinit var urlEsme: String
    lateinit var url: String
    private lateinit var spinner: ProgressBar
    private lateinit var spinnerText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_soreh)
        title = sorehName
        myDirect = File(getExternalFilesDir(null).toString() + "/myQuran")
        //myDirect = File(Environment.getExternalStorageDirectory().toString() + "/myQuran")

        urlEsme = String.format(null, "%03d%03d.mp3", SorehNo + 1, AyehNo + 1)
        url = urlPath + urlEsme

        readSoreh()
        readTarjomeh()
        mywaitePlay = false
        users.clear() //?
        for (i in myarrList.indices)
            users.add(data_Soreh(myarrList[i], myarrListfa[i]))

        my_Recler_View = findViewById<RecyclerView>(R.id.recycler_view_soreh)
        my_Recler_View.adapter = adapter
        my_Recler_View.layoutManager = LinearLayoutManager(this)
        my_Recler_View.setHasFixedSize(true)
        my_Recler_View.scrollToPosition(AyehNo)
        myDownloadList.clear()

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
        }
    }
    //*********************************

    fun downloadFile(urlToDownload: String) {
        /////////////////////

        if (!myDirect.exists()) {
            myDirect.mkdirs()
        }
        Log.d("myQuran", "url to down $url")
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
            .setDestinationInExternalFilesDir(applicationContext, "/myQuran/", urlEsme)
        Log.d("myQuran", " set  folder")
        dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        Log.d("myQuran", "dm to $dm")

        val tmpId = dm.enqueue(request)
        if (mywaitePlay) {
            spinner.visibility = View.VISIBLE
            spinnerText.visibility = View.VISIBLE
            myDownloadId = tmpId
        } else
            myDownloadId = -2

        mywaitePlay = false

        br = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {

                val id: Long = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)!!

                Log.d("myQuran", "dll link==>" + dm.getUriForDownloadedFile(id).toString())
                myDownloadList.remove(dm.getUriForDownloadedFile(id).toString())
                if (id == myDownloadId) {
                    spinner.visibility = View.GONE
                    spinnerText.visibility = View.GONE
                    Log.d("myQuran", "brodacstRecive  in id=$id (download completed)")
                    mywaitePlay = true
                    myDownloadId = -3
                    // Toast.makeText(this@Soreh, "download comlete!", Toast.LENGTH_SHORT).show()
                    playAndDownloadControl()
                }
            }
        }
        registerReceiver(br, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    //*********************************
    override fun onItemClick(postion: Int) {
        myDownloadId = -3
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.stop()
            mediaPlayer.reset()
        }

        AyehNo = postion
        adapter.notifyDataSetChanged()
        mywaitePlay = true
        Log.d("myQuran", "call playAndDownload  from onClick")
        playAndDownloadControl()
    }

    //*********************************
    fun play(localFile: String) {
        Log.d("myQuran", "play $localFile")
        mywaitePlay = false
        try {
            mediaPlayer = MediaPlayer().apply {
                //setAudioStreamType(AudioManager.STREAM_MUSIC)
                reset()
                setDataSource(localFile)
                prepareAsync() // might take long! (for buffering, etc)
                Log.d("myQuran", "prepare")
            }
        } catch (e: Exception) {
            Log.d("myQuran", "media exception :$e")
        }
        mediaPlayer.setOnPreparedListener {
            Log.d("myQuran", "start")
            mediaPlayer.start()
            spinnerText.visibility = View.GONE
            spinner.visibility = View.GONE
        }

        mediaPlayer.setOnCompletionListener(MediaPlayer.OnCompletionListener {

            Log.d("myQuran", "media complete")
            if (AyehNo < myarrList.size - 1) {
                AyehNo++
                my_Recler_View.adapter?.notifyDataSetChanged()
                my_Recler_View.post(Runnable {
                    my_Recler_View.smoothScrollToPosition(
                        AyehNo
                    )
                })

                mywaitePlay = true
                Log.d("myQuran", "call play and download  from media complete")
                playAndDownloadControl()
            } else {
                mywaitePlay = false
            }
        })
    }

    //*********************************
    fun playAndDownloadControl() {
        Log.d("myQuran", "play and download")
        urlEsme = String.format(null, "%03d%03d.mp3", SorehNo + 1, AyehNo + 1)
        val playFile = myDirect.toString() + "//" + urlEsme
        url = urlPath + urlEsme

        val myfile = File(myDirect, urlEsme)

        if (!(myfile.exists())) {
            if (!myDownloadList.contains(url)) {
                myDownloadList.add(url)
                downloadFile(url)
                mywaitePlay = false
            }
        } else {
            Log.d("myQuran", "file exists,play it ....")
            if (mywaitePlay)
                play(playFile)
        }

    }


    override fun onStop() {
        super.onStop()
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.stop()
            mediaPlayer.reset()
        }
        myDownloadId = -3
        if (::br.isInitialized)
            unregisterReceiver(br)

//        if (::dm.isInitialized) {
//            val query = DownloadManager.Query()
//            query.setFilterByStatus(DownloadManager.STATUS_FAILED or DownloadManager.STATUS_PENDING or DownloadManager.STATUS_RUNNING)
//            val c = dm.query(query)
//            while (c.moveToNext()) {
//                dm.remove(c.getLong(c.getColumnIndex(DownloadManager.COLUMN_ID)))
//            }
//        }
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
    }
}
