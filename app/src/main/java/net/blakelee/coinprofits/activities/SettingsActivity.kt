package net.blakelee.coinprofits.activities

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.preference.PreferenceFragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasFragmentInjector
import net.blakelee.coinprofits.R
import net.blakelee.coinprofits.repository.PreferencesRepository
import javax.inject.Inject

class SettingsActivity : AppCompatActivity(), HasFragmentInjector {

    @Inject lateinit var fragmentAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        fragmentManager.beginTransaction().replace(R.id.fragment_container, SettingsFragment()).commit()
    }

    override fun fragmentInjector(): AndroidInjector<Fragment> = fragmentAndroidInjector

    class SettingsFragment : PreferenceFragment() {
        @Inject lateinit var prefs: PreferencesRepository

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            addPreferencesFromResource(R.xml.app_prefs)

            val download = findPreference("download")

            download.setOnPreferenceClickListener {
                prefs.download = true
                it.isEnabled = false
                true
            }
        }

        override fun onAttach(context: Context?) {
            AndroidInjection.inject(this)
            super.onAttach(context)
        }
    }
}