package net.blakelee.coinprofits.activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceFragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import android.support.v7.widget.Toolbar
import net.blakelee.coinprofits.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        fragmentManager.beginTransaction().replace(R.id.fragment_container, SettingsFragment()).commit()
    }

    class SettingsFragment : PreferenceFragment() {
        private val prefs: SharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(this.activity) }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.app_prefs)

            val first = findPreference("first")
            val refresh_tickers = findPreference("refresh_tickers")

            first.setOnPreferenceClickListener {
                prefs.edit().putBoolean("first", true).apply()
                prefs.edit().putBoolean("refresh_tickers", false).apply()
                it.isEnabled = false
                refresh_tickers.isEnabled = false
                true
            }

            refresh_tickers.setOnPreferenceClickListener {
                prefs.edit().putBoolean("refresh_tickers", true).apply()
                it.isEnabled = false
                true
            }
        }

        override fun onAttach(context: Context?) {
            super.onAttach(context)
        }

    }
}