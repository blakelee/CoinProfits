package net.blakelee.coinprofits.fragments

import android.annotation.SuppressLint
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.*
import android.widget.Toast
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.squareup.picasso.Picasso
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import com.yarolegovich.lovelydialog.LovelyChoiceDialog
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_main.*
import net.blakelee.coinprofits.R
import net.blakelee.coinprofits.activities.AboutActivity
import net.blakelee.coinprofits.activities.AddHoldingsActivity
import net.blakelee.coinprofits.activities.SettingsActivity
import net.blakelee.coinprofits.adapters.HoldingsCombinedAdapter
import net.blakelee.coinprofits.base.ItemTouchHelperCallback
import net.blakelee.coinprofits.base.OnStartDragListener
import net.blakelee.coinprofits.databinding.FragmentMainBinding
import net.blakelee.coinprofits.dialogs.DownloadCoinsDialog
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
    private lateinit var adapter: HoldingsCombinedAdapter
    private val downloadCoinsDialog by lazy { DownloadCoinsDialog(context) }
    private val recycler: RecyclerView by lazy { view!!.findViewById<RecyclerView>(R.id.holdings_recycler) }
    private val refresh: SmartRefreshLayout by lazy { view!!.findViewById<SmartRefreshLayout>(R.id.refresh_layout) }

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
        adapter = HoldingsCombinedAdapter(recycler, picasso)
        recycler.adapter = adapter

        val callback = ItemTouchHelperCallback(adapter)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recycler)

        adapter.onStartDragListener = object : OnStartDragListener {
            override fun startDrag(viewHolder: RecyclerView.ViewHolder) {
                itemTouchHelper.startDrag(viewHolder)
            }
        }
    }

    /**
     * Check whether we need to redownload coins or update holdings
     */
    override fun onResume() {
        super.onResume()

        refresh_layout.setOnRefreshListener {
            viewModel.refreshHoldings()
                    .bindUntilEvent(this, Lifecycle.Event.ON_PAUSE)
                    .doOnError { Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show() }
                    .subscribe { _ -> refresh_layout.finishRefresh() }
        }

        //Show refresh dialog
        viewModel.isRefreshing
            .bindUntilEvent(this, Lifecycle.Event.ON_PAUSE)
            .subscribe {
                if (it) {
                    downloadCoinsDialog.show()
                    Toast.makeText(context, "Thank you CoinMarketCap", Toast.LENGTH_SHORT).show()
                }
                else
                    downloadCoinsDialog.dismiss()
        }

        //Show holdings
        viewModel.holdingsCombined
                .bindUntilEvent(this, Lifecycle.Event.ON_PAUSE)
                .subscribe { items -> adapter.dataSource = items }

        //Get count
        viewModel.getCount()
                .bindUntilEvent(this, Lifecycle.Event.ON_PAUSE)
                .subscribe { viewModel.setCount(it) }

        //Get last updated
        viewModel.getLastUpdated()
                .bindUntilEvent(this, Lifecycle.Event.ON_PAUSE)
                .subscribe { viewModel.setLastUpdated(it) }

        viewModel.checkPreferences()

        adapter.longClick
                .bindUntilEvent(this, Lifecycle.Event.ON_PAUSE)
                .subscribe {
                    LovelyChoiceDialog(context)
                            .setTitle("Selection action for ${it.name}")
                            .setTopColorRes(R.color.colorPrimary)
                            .setIcon(R.drawable.ic_info)
                            .setItems(arrayOf("Edit", "Delete"), { position, _ ->
                                when(position) {
                                    0 -> { val intent = Intent(context, AddHoldingsActivity::class.java)
                                        intent.putExtra("id", it.id)
                                        startActivity(intent)
                                    }
                                    1 -> {
                                        val holdings = Holdings()
                                        holdings.order = it.itemOrder
                                        holdings.id = it.id
                                        viewModel.deleteHoldings(holdings).subscribe()
                                    }
                                }
                            })
                            .show()
                }
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
    @SuppressLint("InflateParams")
    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(context, SettingsActivity::class.java))
                true
            }
            R.id.action_add -> {
                startActivity(Intent(context, AddHoldingsActivity::class.java))
                true
            }
            R.id.action_about -> {
                startActivity(Intent(context, AboutActivity::class.java))
                true
            }
            R.id.action_edit -> {
                if (item.isChecked) {
                    item.isChecked = false
                    adapter.editMode = false
                    refresh.isEnableRefresh = true
                    viewModel.updateHoldings(adapter.dataSource)
                } else {
                    item.isChecked = true
                    adapter.editMode = true
                    refresh.isEnableRefresh = false
                }
                    true
            }
            else -> super.onOptionsItemSelected(item)
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