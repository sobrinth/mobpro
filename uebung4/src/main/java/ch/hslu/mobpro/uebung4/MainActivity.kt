package ch.hslu.mobpro.uebung4

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import ch.hslu.mobpro.uebung4.databinding.ActivityMainBinding
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var binding: ActivityMainBinding
    private val bandsViewModel: BandsViewModel by viewModels()

    //todo Add onClicks() to buttons w/o ID
    companion object {
        const val WAITING_TIME_IN_MILLIS = 7000L
        const val WAITING_TIME_KEY = "waiting_time_key"
    }

    private var demoThread = Thread()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(this.binding.root)
        setupViewModelObservers()
    }

    fun freeze7Seconds(view: View?) {
        try {
            Thread.sleep(WAITING_TIME_IN_MILLIS)
        } catch (e: InterruptedException) {
            // Do nothing
        }
    }

    fun startDemoThread(view: View?) {
        if (!demoThread.isAlive){
            demoThread = createDemoThread()
            demoThread.start()
            binding.mainThreadButton.text = "[Demo-Thread läuft...]"
        } else {
            Toast.makeText(this, "DemoThread läuft schon!", Toast.LENGTH_SHORT).show()
        }
    }

    fun requestDataFromServer(view: View?) {
        bandsViewModel.requestBandCodesFromServer()
    }

    fun resetViewModel(view: View) {
        bandsViewModel.resetBandsData()
    }

    fun showBandsDialog(view: View?) {
        val bands = bandsViewModel.getBands().value!!
        AlertDialog.Builder(this)
            .setTitle("Welche Band?")
            .setItems(bands.map { bandCode -> bandCode.name }.toTypedArray()) { _: DialogInterface, itemPos: Int ->
                bandsViewModel.requestBandInfoFromServer(bands[itemPos].code)
            }
            .setNegativeButton("Abbrechen") { _, _ ->
                // ignore
            }
            .create()
            .show()
    }

    private fun createDemoThread(): Thread {
        return object : Thread("hsluDemoThread") {
            override fun run() {
                try {
                    sleep(WAITING_TIME_IN_MILLIS)
                    runOnUiThread {
                        binding.mainThreadButton.text = "Demo-Thread starten"
                        Toast.makeText(this@MainActivity, "Demo-Thread beendet", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: InterruptedException) {
                    // Do nothing
                }
            }
        }
    }

    fun startDemoWorker(view: View?) {
        val workManager = WorkManager.getInstance(application)
        val demoWorkerRequest = OneTimeWorkRequestBuilder<DemoWorker>()
            .setInputData(Data.Builder()
                .putLong(WAITING_TIME_KEY, WAITING_TIME_IN_MILLIS)
                .build())
            .build()
        workManager.enqueue(demoWorkerRequest)
    }

    private fun setupViewModelObservers() {
        bandsViewModel.getBands().observe(this) { names ->
            binding.mainCurrentBandName.text = ""
            binding.mainCurrentBandInfo.text = ""
            binding.mainCurrentBandImage.visibility = View.GONE
            binding.mainNumberOfBands.text = "#Bands = " + if (names.isEmpty()) 0 else names.size
            if (names.isNotEmpty()) {
                showBandsDialog(null)
            }
        }
        bandsViewModel.getCurrentBand().observe(this) {bandInfo ->
            binding.mainCurrentBandName.text = bandInfo?.name ?: ""
            if (bandInfo != null) {
                binding.mainCurrentBandInfo.text = bandInfo.homeCountry + ", Gründung: " + bandInfo.foundingYear
            } else {
                binding.mainCurrentBandInfo.text = ""
            }
            if (bandInfo?.bestOfCdCoverImageUrl != null) {
                Picasso.get()
                    .load(bandInfo.bestOfCdCoverImageUrl)
                    .into(binding.mainCurrentBandImage)
                binding.mainCurrentBandImage.visibility = View.VISIBLE
            } else {
                binding.mainCurrentBandImage.visibility = View.GONE
            }
        }
    }



    class DemoWorker(ctx: Context, private val params: WorkerParameters): Worker(ctx, params) {
        override fun doWork(): Result {
            return try {
                val waitingTime = params.inputData.getLong(WAITING_TIME_KEY, 0)
                Thread.sleep(waitingTime)
                Result.success()
            } catch (throwable: Throwable) {
                Result.failure()
            }
        }
    }

}