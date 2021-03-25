package ch.hslu.mobpro.uebung3

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import java.lang.StringBuilder

class SharedPreferencesViewModel(application: Application): AndroidViewModel(application) {

    private val context = application

    private val PREF_TEA_PREFERRED = "teaPreferred"
    private val PREF_TEA_WITH_SUGAR = "teaWithSugar"
    private val PREF_TEA_SWEETENER = "teaSweetener"

    private var preferencesSummary: MutableLiveData<String> = MutableLiveData()

    fun getPreferenceSummary(): LiveData<String> {
        return preferencesSummary
    }

    fun setDefaultPreferences() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = prefs.edit()
        editor.putString(PREF_TEA_PREFERRED, "Lipton/Pfefferminztee")
        editor.putBoolean(PREF_TEA_WITH_SUGAR, true)
        editor.putString(PREF_TEA_SWEETENER, "natural")
        editor.apply()
        buildPreferenceSummaryString()
    }

    fun buildPreferenceSummaryString() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val sb = StringBuilder()
        sb.append("Ich trinke am liebsten ")
        sb.append(prefs.getString(PREF_TEA_PREFERRED, "Lipton/Pfefferminztee"))
        sb.append(", ")
        if (prefs.getBoolean(PREF_TEA_WITH_SUGAR, true)) {
            sb.append("gesüsst mit ")
            sb.append(getSweetenerDisplayValue(prefs.getString(PREF_TEA_SWEETENER, "natural")!!))
        } else {
            sb.append("ungesüsst")
        }
        preferencesSummary.value = sb.toString()
    }


    private fun getSweetenerDisplayValue(string: String): String {

        val entryValues = context.resources.getStringArray(R.array.teaSweetenerValues)
        val entries = context.resources.getStringArray(R.array.teaSweetener)
        val index = entryValues.indexOf(string)
        return entries[index]
    }
}