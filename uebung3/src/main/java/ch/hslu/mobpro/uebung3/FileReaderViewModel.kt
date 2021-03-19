package ch.hslu.mobpro.uebung3

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.*
import java.lang.Exception
import java.lang.StringBuilder

class FileReaderViewModel(application: Application): AndroidViewModel(application) {

    private val context = application

    private var storedText: MutableLiveData<String> = MutableLiveData()
    private var storageStateText: MutableLiveData<String> = MutableLiveData()
    private var isExternalStorageEnabled: MutableLiveData<Boolean> = MutableLiveData()

    fun getStoredText(): LiveData<String> {
        return storedText
    }

    fun getStorageStateText(): LiveData<String> {
        return storageStateText
    }

    fun isExternalStorageEnabled(): LiveData<Boolean> {
        return isExternalStorageEnabled
    }

    fun loadTextFromFile(useExtStorage: Boolean) {
        val inFile = getFile(useExtStorage)
        if (!inFile.exists()) {
            storedText.value = "<no file>"
            return
        }
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(FileReader(inFile))
            val content = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                content.append(line)
                content.append("\n")
            }
            storedText.value = content.toString()
        } catch (ex: IOException) {
            Log.e("HSLU-MobPro-Persistenz", "Could not read file", ex)
            storedText.value = "<reading failed>"
        } finally {
            try {
                reader?.close()
            } catch (ex: Exception) {
                Log.e("HSLU-MobPro-Persistenz", "Could not close file", ex)
            }
        }

    }

    fun writeTextToFile(text: String, useExtStorage: Boolean) {
        var writer: Writer? = null
        try {
            writer = BufferedWriter(FileWriter(getFile(useExtStorage)))
            writer.write(text)
            storedText.value = "<ok>"
        } catch (ex: IOException) {
            Log.e("HSLU-MobPro-Persistenz", "Could not write file", ex)
            storedText.value = "<write failed>"
        } finally {
            try {
                writer?.close()
            } catch (ex: Exception) {
                Log.e("HSLU-MobPro-Persistenz", "Could not close file", ex)
            }
        }
    }

    private fun getFile(useExtStorage: Boolean): File {
        val rootDir: File? = if (useExtStorage) {
            context.getExternalFilesDir(null)
        } else {
            context.filesDir
        }
        val parentDir = File(rootDir, "HSLU-MobPro-3")
        parentDir.mkdirs()
        return File(parentDir, "PersistentMessage.txt")
    }
}