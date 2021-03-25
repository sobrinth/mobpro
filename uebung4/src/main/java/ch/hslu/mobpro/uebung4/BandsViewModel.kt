package ch.hslu.mobpro.uebung4

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.HttpURLConnection

class BandsViewModel : ViewModel() {

    private val bands: MutableLiveData<List<BandCode>> = MutableLiveData()
    private val currentBand: MutableLiveData<BandInfo?> = MutableLiveData()

    private val retrofit = Retrofit.Builder()
        .client(OkHttpClient().newBuilder().build())
        .addConverterFactory(MoshiConverterFactory.create())
        .baseUrl("https://wherever.ch/hslu/rock-bands/")
        .build()

    private val bandsService = retrofit.create(BandsApiService::class.java)

    fun getCurrentBand(): LiveData<BandInfo?> {
        return currentBand
    }

    fun getBands(): LiveData<List<BandCode>> {
        return bands
    }

    fun requestBandCodesFromServer() {
        val call = bandsService.getBandNames()
        call.enqueue(object : Callback<List<BandCode>> {

            override fun onResponse(call: Call<List<BandCode>>, response: Response<List<BandCode>>) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    bands.value = response.body().orEmpty()
                }
            }

            override fun onFailure(call: Call<List<BandCode>>, t: Throwable) {
                // ignore
            }
        })
    }

    fun resetBandsData() {
        bands.value = emptyList()
        currentBand.value = null

    }

    fun requestBandInfoFromServer(code: String) {
        val call = bandsService.getBandInfo(code)
        call.enqueue(object : Callback<BandInfo> {

            override fun onResponse(call: Call<BandInfo>, response: Response<BandInfo>) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    currentBand.value = response.body()
                }
            }

            override fun onFailure(call: Call<BandInfo>, t: Throwable) {
                // ignore
            }
        })
    }

}

