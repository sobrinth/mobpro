package ch.hslu.mobpro.uebung2

import androidx.lifecycle.ViewModel

class CounterViewModel : ViewModel() {

    private var counter = 0

    fun incCounter() {counter++}

    fun getCounter(): Int {return counter}
}