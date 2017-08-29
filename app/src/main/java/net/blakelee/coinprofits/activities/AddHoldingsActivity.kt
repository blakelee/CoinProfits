package net.blakelee.coinprofits.activities

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v4.app.NavUtils
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.*
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import dagger.android.AndroidInjection
import net.blakelee.coinprofits.R
import kotlinx.android.synthetic.main.activity_add.*
import net.blakelee.coinprofits.adapters.AutoCompleteCurrencyAdapter
import net.blakelee.coinprofits.viewmodels.AddHoldingsViewModel
import javax.inject.Inject
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import com.robertlevonyan.views.chip.Chip
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.item_add_transaction.view.*
import net.blakelee.coinprofits.adapters.TransactionAdapter
import net.blakelee.coinprofits.base.InstantAutoComplete
import net.blakelee.coinprofits.di.AppModule
import net.blakelee.coinprofits.models.Coin
import net.blakelee.coinprofits.models.Holdings
import net.blakelee.coinprofits.models.Transaction

class AddHoldingsActivity : AppCompatActivity(), LifecycleRegistryOwner {

    private val registry = LifecycleRegistry(this)
    private var id: String? = null
    private val autocompleteTV by lazy { findViewById<InstantAutoComplete>(R.id.holdings_autocomplete) }
    private val constaintLayout by lazy { findViewById<ConstraintLayout>(R.id.holdings_constraint) }
    private val transactionAdd by lazy { findViewById<Button>(R.id.holdings_add_button) }
    private val transactionRecycler by lazy { findViewById<RecyclerView>(R.id.holdings_recycler) }
    private val transactionAdapter by lazy { TransactionAdapter(this, transactionRecycler) }
    private lateinit var autoCompleteHint: String
    @Inject lateinit var adapter: AutoCompleteCurrencyAdapter
    @Inject lateinit var viewModel: AddHoldingsViewModel
    private var holdings: Holdings? = null
    private var chip: Chip? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add)
        setSupportActionBar(toolbar)

        val arrow: Drawable = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)
        supportActionBar?.setHomeAsUpIndicator(arrow)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        id = savedInstanceState?.getString("id", null)
        autoCompleteHint = autocompleteTV.hint.toString()
        transactionRecycler.adapter = transactionAdapter

        if (id == null)
            supportActionBar?.setTitle(R.string.dialog_add)
        else {
            supportActionBar?.setTitle(R.string.dialog_edit)
            viewModel.getHoldingsById(id!!)
                    .subscribe {
                        holdings = it
                        transactionAdapter.dataSource = holdings!!.transaction.toMutableList()
                        makeChip(it as Coin, false)
                    }
        }

        setupInstantTextView()
        setupTransactionAdd()
    }

    override fun onBackPressed() {
        if (autocompleteTV.isPopupShowing)
            autocompleteTV.dismissDropDown()
        else
            super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()

        viewModel.getCoins()
                .bindUntilEvent(this, Lifecycle.Event.ON_PAUSE)
                .subscribe {
                    adapter.originalList = it
                }

        transactionAdapter.viewClick
                .bindUntilEvent(this, Lifecycle.Event.ON_PAUSE)
                .subscribe { view ->
                    val text = view.publicKey.text.toString()
                    if (text.isNotEmpty())
                        viewModel.transactionRepo
                                .getAddressInfo(text)
                                .subscribe {
                                    val item = it[holdings!!.symbol]
                                    if (item != null) {
                                        view.transaction_amount.setText(it.toString())
                                        view.transaction_amount.isEnabled = false
                                    } else {
                                        view.transaction_amount.error = "This public key doesn't seem to contain any balance"
                                        view.transaction_price.setText("0.0")
                                    }
                                }
                }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean =
         when(item?.itemId) {
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                true
            }
             R.id.action_add -> {
                 if (transactionAdapter.validate()) {
                     holdings?.let {
                         it.transaction = transactionAdapter.dataSource
                         viewModel.insertHoldings(it)
                     }
                 }
                 true
             }
            else -> super.onOptionsItemSelected(item)
        }

    override fun getLifecycle(): LifecycleRegistry = registry

    private fun hideKeyboard(view: View) {
        val inputManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    private fun setupTransactionAdd() {
        transactionAdd.setOnClickListener {
            chip?.let {
                transactionAdapter.addItem(Transaction())
            }
        }
    }

    private fun setupInstantTextView() {
        autocompleteTV.setAdapter(adapter)

        //Do chip editing here
        autocompleteTV.setOnItemClickListener { adapterView, view, i, l ->
            hideKeyboard(view)
            val item: Coin = adapter.getItem(i)
            makeChip(item)
            viewModel.getHoldingsById(item.id)
                    .subscribe ({
                        holdings = it
                        transactionAdapter.dataSource = holdings!!.transaction.toMutableList()
                    }, {}, {
                        holdings = Holdings()
                        holdings!!.id = item.id//This means holdings wasn't found in the db
                        holdings!!.symbol = item.symbol
                        holdings!!.name = item.name
                        transactionAdapter.dataSource = holdings!!.transaction.toMutableList()
                    })
        }

        @Suppress()
        autocompleteTV.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX <= autocompleteTV.compoundDrawables[0].bounds.width() + 2 * autocompleteTV.left) {
                    adapter.filter.filter(null)
                    val v = currentFocus
                    val viewGroup = v.parent as ViewGroup?
                    viewGroup?.isFocusableInTouchMode = true
                    viewGroup?.requestFocus()
                    autocompleteTV.force = true
                    autocompleteTV.showDropDown()
                    hideKeyboard(view)
                }
            }

            else if (event.rawX > autocompleteTV.compoundDrawables[0].bounds.width() + 2 * autocompleteTV.left )
                autocompleteTV.force = false

            super.onTouchEvent(event)
        }
    }

    private fun makeChip(coin: Coin, closeable: Boolean = true) {
        chip = Chip(this)
        chip!!.id = R.id.chip
        chip!!.chipText = coin.toString()
        chip!!.isHasIcon = true
        chip!!.isClosable = closeable
        chip!!.isClickable = true

        viewModel.picasso.load(AppModule.IMAGE_URL + coin.id + ".png").into(object : Target {
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
            override fun onBitmapFailed(errorDrawable: Drawable?) {}
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                chip!!.chipIcon = BitmapDrawable(resources, bitmap)
            }
        })

        chip!!.setOnCloseClickListener {
            constaintLayout.removeView(chip)
            autocompleteTV.hint = autoCompleteHint
            autocompleteTV.isEnabled = true
            transactionAdapter.removeAll()
            holdings = null
            chip = null
        }

        constaintLayout.addView(chip)
        autocompleteTV.setText("")
        autocompleteTV.hint = ""

        //Get padding and drawable size
        val margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48f, resources.displayMetrics)

        val constraintSet = ConstraintSet()
        with(constraintSet) {
            connect(R.id.chip, ConstraintSet.BOTTOM, R.id.holdings_autocomplete, ConstraintSet.BOTTOM)
            connect(R.id.chip, ConstraintSet.START, R.id.holdings_autocomplete, ConstraintSet.START, margin.toInt())
            connect(R.id.chip, ConstraintSet.TOP, R.id.holdings_autocomplete, ConstraintSet.TOP)
            constrainDefaultHeight(R.id.chip, ConstraintSet.MATCH_CONSTRAINT_WRAP)

            applyTo(findViewById(R.id.holdings_constraint))
        }

        autocompleteTV.isEnabled = false
        autocompleteTV.text.clear()
    }
}
