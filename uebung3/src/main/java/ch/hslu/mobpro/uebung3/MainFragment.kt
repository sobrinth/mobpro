package ch.hslu.mobpro.uebung3


import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Telephony
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import ch.hslu.mobpro.uebung3.databinding.FragmentMainBinding

class MainFragment : Fragment(R.layout.fragment_main) {

    private val sharedPreferencesViewModel: SharedPreferencesViewModel by activityViewModels()
    private val fileReaderViewModel: FileReaderViewModel by activityViewModels()

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
            object : ActivityResultCallback<Boolean> {
                override fun onActivityResult(result: Boolean) {
                    if (!result) {
                        Toast.makeText(context, "Permission denied!", Toast.LENGTH_SHORT).show()
                        return
                    } else {
                        readSms()
                    }
                }
            }
        )

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
        binding.buttonShowSMS.setOnClickListener { performOperationWithPermission() }

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

    private fun performOperationWithPermission() {
        val result = requireActivity().checkSelfPermission(Manifest.permission.READ_SMS)
        val granted: Boolean = result == PackageManager.PERMISSION_GRANTED
        if (!granted) {
            requestPermissionLauncher.launch(Manifest.permission.READ_SMS)
        } else {
            readSms()
        }
    }


    private fun readSms() {
        val cursor = requireActivity().contentResolver.query(Telephony.Sms.Inbox.CONTENT_URI,
        arrayOf(Telephony.Sms.Inbox._ID, Telephony.Sms.Inbox.BODY),
        null,
        null,
        null
        )
        AlertDialog.Builder(context)
            .setTitle("SMS in Inbox")
            .setCursor(cursor, null, Telephony.TextBasedSmsColumns.BODY)
            .setNeutralButton("Ok", null)
            .create()
            .show()
    }


}