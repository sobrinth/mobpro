package ch.hslu.mobpro.uebung2

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.fragment_constraint_layout.view.*

class ConstraintLayoutFragment : Fragment(R.layout.fragment_constraint_layout) {

    private var counter = 0
    private val counterViewModel: CounterViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.buttonIncViewModel.text = "VIEWMODEL: ${counterViewModel.getCounter()}++"
        view.buttonIncFragment.text = "FRAGMENT: ${counter}++"

        view.buttonIncFragment.setOnClickListener {
            counter++
            view.buttonIncFragment.text = "FRAGMENT: ${counter}++"
        }
        view.buttonIncViewModel.setOnClickListener {
            counterViewModel.incCounter()
            view.buttonIncViewModel.text = "VIEWMODEL: ${counterViewModel.getCounter()}++"
        }
    }
}