package ch.hslu.mobpro.uebung2

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*

class MainFragment : Fragment(R.layout.fragment_main), AdapterView.OnItemSelectedListener {

    private var firstSpinnerSelection = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRatingBar(view)
        view.spinner.onItemSelectedListener = this
        this.dialogSelection.setOnClickListener { createChoiceDialog().show() }

    }

    private fun setupRatingBar(view: View) {
        view.ratingBar.setOnRatingBarChangeListener { _, fl, _ ->
            view.ratingBar_textView.text = "Aha , Du wählst ${ratingBar.rating}"

        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        if (!firstSpinnerSelection) {
            val msg = ("Du hast '" + parent.getItemAtPosition(position) + "' gewählt")
            Toast.makeText(activity, msg, Toast.LENGTH_LONG).show()
        }
        firstSpinnerSelection = false

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Do nothing
    }

    private fun createChoiceDialog(): AlertDialog {
        val items = arrayOf("Alles", "Ein bisschen was", "Nichts")
        val dialogBuilder = AlertDialog.Builder(activity)
            .setTitle("Was willst du?")
            .setItems(items) { _, itemPos ->
                val msg = ("Du hast '" + items[itemPos] + "' gewählt")
                Toast.makeText(activity, msg, Toast.LENGTH_LONG).show()
            }.setNegativeButton("WEISS NICHT", null)
        return dialogBuilder.create()
    }
}