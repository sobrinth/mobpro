package ch.hslu.mobpro.uebung2

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*

class MainFragment : Fragment(R.layout.fragment_main) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRatingBar(view)

    }

    private fun setupRatingBar(view: View) {
        view.ratingBar.setOnRatingBarChangeListener { _, fl, _ ->
            view.ratingBar_textView.text = "Aha , Du wählst ${ratingBar.rating}"

        }
    }
}