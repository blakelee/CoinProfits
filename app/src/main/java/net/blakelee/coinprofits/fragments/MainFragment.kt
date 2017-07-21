package net.blakelee.coinprofits.fragments

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.Toast
import com.squareup.picasso.Picasso
import com.yarolegovich.lovelydialog.LovelyChoiceDialog
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_main.*
import net.blakelee.coinprofits.R
import net.blakelee.coinprofits.activities.SettingsActivity
import net.blakelee.coinprofits.adapters.HoldingsAdapter
import net.blakelee.coinprofits.databinding.FragmentMainBinding
import net.blakelee.coinprofits.dialogs.AdvancedHoldingsDialog
import net.blakelee.coinprofits.dialogs.DownloadCoinsDialog
import net.blakelee.coinprofits.dialogs.HoldingsDialog
import net.blakelee.coinprofits.models.Holdings
import net.blakelee.coinprofits.viewmodels.MainViewModel
import javax.inject.Inject

/**
 * Contains the main screen with the list of holdings
 */
class MainFragment : Fragment(), LifecycleRegistryOwner {

    @Inject lateinit var viewModel: MainViewModel
    @Inject lateinit var picasso: Picasso

    private val registry = LifecycleRegistry(this)
    private lateinit var adapter: HoldingsAdapter

    /**
     * Setup data binding
     */
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentMainBinding>(inflater, R.layout.fragment_main, container, false)
        binding.viewmodel = viewModel
        return binding.root
    }

    /**
     * Setup observers after onCreateView
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setHasOptionsMenu(true)
        adapter = HoldingsAdapter(holdings_recycler, picasso, this::itemLongClick)
        holdings_recycler.adapter = adapter
        setupObservers()
    }

    /**
     * Check whether we need to redownload coins or update holdings
     */
    override fun onResume() {
        super.onResume()

        viewModel.checkPreferences()
    }


    private fun getTickers() {
        val indefinite = DownloadCoinsDialog(context)
        indefinite.show()

        viewModel.insertCoins({
            it?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
            indefinite.dismiss()
        })
    }

    /**
     * Set menu to create one where you can add holdings
     */
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Open new activity or dialog
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(context, SettingsActivity::class.java))
                true
            }
            R.id.action_add -> {
                //TODO: Fix this
                val view: View = layoutInflater.inflate(R.layout.dialog_add_coin, null)
                HoldingsDialog(context, view, null, viewModel, picasso, this::advancedDialog).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun advancedDialog(old: Holdings, new: Holdings) {
        val view: View = layoutInflater.inflate(R.layout.dialog_merge_replace_coin, null)
        val dialog = AdvancedHoldingsDialog(context, view, old, new, viewModel)
        dialog.show()
    }

    fun itemLongClick(holdings: Holdings) {
        LovelyChoiceDialog(context)
                .setTitle("Selection action for ${holdings.name}")
                .setTopColorRes(R.color.colorPrimary)
                .setIcon(R.drawable.ic_info)
                .setItems(arrayOf("Edit", "Delete"), { position, _ ->
                    when(position) {
                        0 -> {
                            val view: View = layoutInflater.inflate(R.layout.dialog_add_coin, null)
                            HoldingsDialog(context, view, holdings, viewModel, picasso, this::advancedDialog).show()
                        }
                        1 -> viewModel.deleteHoldings(holdings)
                    }
                })
                .show()
    }

    private fun setupObservers() {
        viewModel.holdings.observe(this, Observer<List<Holdings>> {
            it?.let { adapter.dataSource = it }
        })

        viewModel.refresh_tickers.observe(this, Observer {
            it?.let { if (it) getTickers() }
        })

        viewModel.first.observe(this, Observer {
            it?.let { if (it) {
                Toast.makeText(context, "Thank you CoinMarketCap", Toast.LENGTH_LONG).show()
                getTickers()
            }
            }
        })
    }

    override fun getLifecycle(): LifecycleRegistry = registry

    /**
     * Inject ViewModel
     */
    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
}