package ch.hslu.mobpro.uebung3


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import ch.hslu.mobpro.uebung3.databinding.FragmentMainBinding

class MainFragment : Fragment(R.layout.fragment_main) {

    private val sharedPreferencesViewModel: SharedPreferencesViewModel by activityViewModels()
    private val fileReaderViewModel: FileReaderViewModel by activityViewModels()

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val SHARED_PREFERENCES_OVERVIEW = "sharedPreferencesOverview"
        const val COUNTER_KEY = "resumeCount"

        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonEditPrefs.setOnClickListener { editTeaPreferencesOnClick() }
        binding.buttonResetPrefs.setOnClickListener { resetTeaPreferencesOnClick() }
        binding.buttonSaveText.setOnClickListener { onClickSaveFile() }
        binding.buttonLoadText.setOnClickListener { onClickLoadFile() }

        fileReaderViewModel.getStoredText().observe(viewLifecycleOwner, Observer { text ->
            binding.textFile.setText(text)
        })

        sharedPreferencesViewModel.getPreferenceSummary().observe(viewLifecycleOwner, Observer { text ->
            binding.twPreferredTea.text = text
        })

        fileReaderViewModel.isExternalStorageEnabled().observe(viewLifecycleOwner, Observer { isExternalStorageEnabled ->
            binding.cbUseExternalStorage.isEnabled = isExternalStorageEnabled
        })
    }

    override fun onResume() {
        super.onResume()
        saveResumeCountToPrivatePreferences()
        updateResumeCounterView()
        sharedPreferencesViewModel.buildPreferenceSummaryString()
    }

    private fun saveResumeCountToPrivatePreferences() {
        val prefs = requireActivity().getSharedPreferences(SHARED_PREFERENCES_OVERVIEW, Context.MODE_PRIVATE)
        val newCount = prefs.getInt(COUNTER_KEY, 0) + 1
        val editor = prefs.edit()
        editor.putInt(COUNTER_KEY, newCount)
        editor.apply()
    }

    private fun updateResumeCounterView() {
        val count = requireActivity().getSharedPreferences(SHARED_PREFERENCES_OVERVIEW, Context.MODE_PRIVATE)
            .getInt(COUNTER_KEY, 0)
        val tvCounter = binding.twResumeCount as TextView
        val text = "MainActivity.onResume() wurde seit der Installation dieser App $count mal aufgerufen."
        tvCounter.text = text;
    }


    private fun editTeaPreferencesOnClick() {
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.mainActivityView, TeaPreferenceFragment.newInstance())
            .addToBackStack("preferences")
            .commit()
    }

    private fun resetTeaPreferencesOnClick() {
        sharedPreferencesViewModel.setDefaultPreferences()
    }

    private fun onClickSaveFile() {
        writeFile()
    }

    private fun writeFile() {
        fileReaderViewModel.writeTextToFile(binding.textFile.text.toString(), binding.cbUseExternalStorage.isChecked)
    }

    private fun onClickLoadFile() {
        readFile()
    }

    private fun readFile() {
        fileReaderViewModel.loadTextFromFile(binding.cbUseExternalStorage.isChecked)
    }



}