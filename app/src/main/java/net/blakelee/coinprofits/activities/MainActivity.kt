package net.blakelee.coinprofits.activities

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.yarolegovich.lovelydialog.LovelyProgressDialog
import com.yarolegovich.lovelydialog.LovelyProgressObservable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import net.blakelee.coinprofits.R
import net.blakelee.coinprofits.adapters.HoldingsAdapter
import net.blakelee.coinprofits.base.BaseLifecycleActivity
import net.blakelee.coinprofits.dialogs.AdvancedHoldingsDialog
import net.blakelee.coinprofits.dialogs.HoldingsDialog
import net.blakelee.coinprofits.models.Holdings
import net.blakelee.coinprofits.viewmodels.MainViewModel

class MainActivity : BaseLifecycleActivity<MainViewModel>() {

    override val viewModelClass = MainViewModel::class.java
    private val PREFS_NAME = "CoinPrefs"
    private val adapter = HoldingsAdapter(this::itemLongClick)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        checkPreferences()
        addListeners()
        subscribeHoldings()

        holdings_recycler.adapter = adapter
        holdings_recycler.layoutManager = LinearLayoutManager(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun addListeners() {
        fab.setOnClickListener { _ ->
            val view: View = layoutInflater.inflate(R.layout.dialog_add_coin, null)
            val dialog = HoldingsDialog(this, view, null, viewModel, this::advancedDialog)
            dialog.show()
        }

        refresh_layout.setOnRefreshListener {
            subscribeHoldings()
            viewModel.refreshHoldings({
                Toast.makeText(this, "Couldn't refresh data. Perhaps website or network connection is down", Toast.LENGTH_SHORT).show()
                refresh_layout.isRefreshing = false
            }, {
                refresh_layout.isRefreshing = false
            })
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

    fun checkPreferences() {
        val refresh: Boolean
        val settings = getSharedPreferences(PREFS_NAME, 0)
        refresh = settings.getBoolean("refresh", true)

        if (refresh) {

            fab.isEnabled = false
            val completed: LovelyProgressObservable = LovelyProgressObservable()

            viewModel.refreshTickers({

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
                    dialog.show()
                    job.await()
                }
            }, {
                completed.progress++
                Log.i("PICASSO", "Completed ${completed.progress}")
            },{
                Toast.makeText(this, "Failed to get tickers: $it", Toast.LENGTH_LONG).show()
            })

            fab.isEnabled = true
            settings.edit().putBoolean("refresh", false).apply()
        }
    }


    fun subscribeHoldings() {
        viewModel.holdings.observe(this, Observer<List<Holdings>> {
            it?.let { adapter.dataSource = it }
        })
    }
}
