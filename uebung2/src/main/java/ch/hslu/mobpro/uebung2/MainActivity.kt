package ch.hslu.mobpro.uebung2

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainActivityView, MainFragment())
            .commit()
    }

    fun layoutLinearSelected(view: View) {

    }

    fun layoutConstraintSelected(view: View) {

    }
}