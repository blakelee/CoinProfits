package net.blakelee.coinprofits.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.SharedPreferences
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.preference.PreferenceManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.yarolegovich.lovelydialog.LovelyProgressDialog
import com.yarolegovich.lovelydialog.LovelyProgressObservable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import net.blakelee.coinprofits.R
import net.blakelee.coinprofits.adapters.HoldingsAdapter
import net.blakelee.coinprofits.base.BaseLifecycleActivity
import net.blakelee.coinprofits.databinding.ActivityMainBinding
import net.blakelee.coinprofits.dialogs.AdvancedHoldingsDialog
import net.blakelee.coinprofits.dialogs.HoldingsDialog
import net.blakelee.coinprofits.models.Holdings
import net.blakelee.coinprofits.viewmodels.MainViewModel

class MainActivity : BaseLifecycleActivity<MainViewModel>() {

    override val viewModelClass = MainViewModel::class.java
    private val adapter = HoldingsAdapter(this::itemLongClick)
    private val settings: SharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        setSupportActionBar(toolbar)

        viewModel = ViewModelProviders.of(this).get(viewModelClass)
        binding.viewmodel = viewModel

        setupObservers()

        holdings_recycler.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkPreferences()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(baseContext, SettingsActivity::class.java))
                true
            }
            R.id.action_add -> {
                val view: View = layoutInflater.inflate(R.layout.dialog_add_coin, null)
                val dialog = HoldingsDialog(this, view, null, viewModel, this::advancedDialog)
                dialog.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun itemLongClick(holdings: Holdings) {
        viewModel.deleteHoldings(holdings)
    }

    fun advancedDialog(old: Holdings, new: Holdings) {
        val view: View = layoutInflater.inflate(R.layout.dialog_merge_replace_coin, null)
        val dialog = AdvancedHoldingsDialog(this, view, old, new, viewModel)
        dialog.show()
    }

    fun getTickers() {
        val completed: LovelyProgressObservable = LovelyProgressObservable()

        val indefinite = LovelyProgressDialog(this)
                .setTopColorRes(R.color.colorPrimary)
                .setTitle("Downloading Images")
                .setIcon(R.drawable.ic_file_download)
                .create()

        indefinite.show()

        viewModel.getTickers({

            val total = it

            val dialog = LovelyProgressDialog(this)
                    .setTopColorRes(R.color.colorPrimary)
                    .setTitle("Downloading Images")
                    .setHorizontal(true)
                    .setMax(total)
                    .setIcon(R.drawable.ic_file_download)
                    .setProgressObservable(completed)
                    .create()

            val job = async(CommonPool) {
                Log.i("PICASSO", "Processing $it images")
                while (completed.progress < it) { }
                Log.i("PICASSO", "Finished getting images")
            }

            async(UI) {
                indefinite.dismiss()
                dialog.show()
                job.await()
            }
        }, {
            completed.progress++
            Log.i("PICASSO", "Completed ${completed.progress}")
        },{
            Toast.makeText(this, "Failed to get tickers: $it", Toast.LENGTH_LONG).show()
        })
    }

    fun setupObservers() {
        viewModel.holdings.observe(this, Observer<List<Holdings>> {
            it?.let { adapter.dataSource = it }
        })

        viewModel.refresh_tickers.observe(this, Observer {
            it?.let { if (it) getTickers() }
        })

        viewModel.first.observe(this, Observer {
            it?.let { if (it) {
                    Toast.makeText(this, "Thank you CoinMarketCap", Toast.LENGTH_LONG).show()
                    getTickers()
                }
            }
        })

    }
}
