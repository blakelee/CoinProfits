package net.blakelee.coinprofits.activities

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.view.*
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import dagger.android.AndroidInjection
import net.blakelee.coinprofits.R
import kotlinx.android.synthetic.main.activity_add.*
import net.blakelee.coinprofits.adapters.AutoCompleteCurrencyAdapter
import net.blakelee.coinprofits.viewmodels.AddHoldingsViewModel
import javax.inject.Inject
import android.view.inputmethod.InputMethodManager
import net.blakelee.coinprofits.base.InstantAutoComplete

class AddHoldingsActivity : AppCompatActivity(), LifecycleRegistryOwner {

    private val registry = LifecycleRegistry(this)
    private var id: String? = null
    private val autocompleteTV by lazy { findViewById<InstantAutoComplete>(R.id.autocomplete_currency) }
    @Inject lateinit var adapter: AutoCompleteCurrencyAdapter
    @Inject lateinit var viewModel: AddHoldingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add)
        setSupportActionBar(toolbar)

        val arrow: Drawable = resources.getDrawable(R.drawable.ic_arrow_back)
        supportActionBar?.setHomeAsUpIndicator(arrow)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        id = savedInstanceState?.getString("id", null)

        if (id == null)
            supportActionBar?.setTitle(R.string.dialog_add)
        else
            supportActionBar?.setTitle(R.string.dialog_edit)

        handleInstantTextView()
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
            else -> super.onOptionsItemSelected(item)
        }

    override fun getLifecycle(): LifecycleRegistry = registry

    private fun hideKeyboard(view: View) {
        val inputManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    private fun handleInstantTextView() {
        autocompleteTV.setAdapter(adapter)

        autocompleteTV.setOnItemClickListener { adapterView, view, i, l ->
            hideKeyboard(view)
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
}
