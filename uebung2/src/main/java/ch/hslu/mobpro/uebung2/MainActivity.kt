package ch.hslu.mobpro.uebung2

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainActivityView, MainFragment())
            .commit()
    }

    fun layoutLinearSelected(view: View) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainActivityView, Fragment(R.layout.fragment_linear_layout))
            .addToBackStack("fragmentLinearLayout")
            .commit()
    }

    fun layoutConstraintSelected(view: View) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainActivityView, ConstraintLayoutFragment())
            .addToBackStack("fragmentConstraintLayout")
            .commit()
    }
}