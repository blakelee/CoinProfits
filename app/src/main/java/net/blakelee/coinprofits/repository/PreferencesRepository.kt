package net.blakelee.coinprofits.repository

import android.content.SharedPreferences
import com.f2prateek.rx.preferences2.Preference
import com.f2prateek.rx.preferences2.RxSharedPreferences
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class PreferencesRepository @Inject constructor(val prefs: SharedPreferences) {
    val rxPrefs = RxSharedPreferences.create(prefs)

    var download: Boolean
        get() = prefs.getBoolean("download", true)
        set(value) = prefs.edit().putBoolean("download", value).apply()

    var autoRefresh: Boolean = false
        get() = prefs.getBoolean("refresh", false)

    var lastUpdated: Preference<String> = rxPrefs.getString("last_updated", getTime())

    var convert: String = "usd"
        get() = prefs.getString("convert", "usd")

    private fun getTime(): String = SimpleDateFormat("h:mma", Locale.getDefault()).format(Date())
}