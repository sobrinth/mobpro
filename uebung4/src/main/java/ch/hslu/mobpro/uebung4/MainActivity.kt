package ch.hslu.mobpro.uebung4

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import ch.hslu.mobpro.uebung4.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var binding: ActivityMainBinding

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